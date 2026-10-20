package com.example.triply.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.triply.data.local.User
import com.example.triply.data.repository.TriplyRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ProfileUiState(
    val totalTrips: Int = 0,
    val totalPlaces: Int = 0,
    val totalExpenses: Double = 0.0,
    val avgExpensePerTrip: Double = 0.0,
    val userName: String = "Traveler",
    val bio: String = "World Explorer",
    val avatarUri: String? = null,
    val currency: String = "$",
    val isDarkMode: Boolean? = null,
    val emergencyContact: String = "",
    val medicalNotes: String = ""
)

class ProfileViewModel(private val repository: TriplyRepository) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        repository.getAllTripsStream(),
        repository.getAllExpensesStream(),
        repository.getUserStream()
    ) { trips, expenses, user ->
        val uniqueLocations = trips.map { it.location.lowercase().trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .size
            
        val totalExp = expenses.sumOf { it.amount }

        ProfileUiState(
            totalTrips = trips.size,
            totalExpenses = totalExp,
            avgExpensePerTrip = if (trips.isNotEmpty()) totalExp / trips.size else 0.0,
            userName = user?.name ?: "Traveler",
            bio = user?.bio ?: "World Explorer",
            avatarUri = user?.avatarUri,
            currency = user?.currency ?: "$",
            isDarkMode = user?.isDarkMode,
            emergencyContact = user?.emergencyContact ?: "",
            medicalNotes = user?.medicalNotes ?: "",
            totalPlaces = uniqueLocations
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState()
    )

    fun saveUserSettings(
        name: String, 
        bio: String, 
        currency: String, 
        darkMode: Boolean?,
        emergency: String,
        medical: String
    ) {
        viewModelScope.launch {
            repository.insertUser(User(
                id = 1,
                name = name,
                bio = bio,
                avatarUri = uiState.value.avatarUri,
                currency = currency,
                isDarkMode = darkMode,
                emergencyContact = emergency,
                medicalNotes = medical
            ))
        }
    }

    fun updateAvatar(uri: String) {
        viewModelScope.launch {
            val s = uiState.value
            repository.insertUser(User(
                id = 1,
                name = s.userName,
                bio = s.bio,
                avatarUri = uri,
                currency = s.currency,
                isDarkMode = s.isDarkMode,
                emergencyContact = s.emergencyContact,
                medicalNotes = s.medicalNotes
            ))
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            // Если эти методы подчеркнуты, выполните Build -> Rebuild Project
            repository.deleteAllTrips()
            repository.deleteUser()
        }
    }

    suspend fun generateFullReport(): String {
        val tripsList = repository.getAllTripsStream().first()
        val expensesList = repository.getAllExpensesStream().first()
        val user = uiState.value
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        return buildString {
            appendLine("📊 ОТЧЕТ ПУТЕШЕСТВЕННИКА: ${user.userName}")
            appendLine("📅 Сформирован: ${dateFormat.format(Date())}")
            appendLine("-------------------------------")
            appendLine("Общая статистика:")
            appendLine("- Поездок: ${user.totalTrips}")
            appendLine("- Мест: ${user.totalPlaces}")
            
            // Выносим форматирование из шаблона строки, чтобы избежать ошибок с кавычками
            val totalFormatted = String.format(Locale.getDefault(), "%.2f", user.totalExpenses)
            appendLine("- Потрачено: ${user.currency}$totalFormatted")
            
            appendLine("\n✈️ ИСТОРИЯ ПОЕЗДОК:")
            tripsList.forEach { trip ->
                appendLine("- ${trip.title} | ${trip.location}")
                val tripExp = expensesList.filter { it.tripId == trip.id }.sumOf { it.amount }
                val tripExpFormatted = String.format(Locale.getDefault(), "%.2f", tripExp)
                appendLine("  Бюджет: ${user.currency}${trip.budget} | Потрачено: ${user.currency}$tripExpFormatted")
            }
            appendLine("\nСоздано в Triply 🌸")
        }
    }
}

class ProfileViewModelFactory(private val repository: TriplyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
