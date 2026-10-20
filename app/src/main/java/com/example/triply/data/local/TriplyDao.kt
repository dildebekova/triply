package com.example.triply.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TriplyDao {
    // Trips
    @Query("SELECT * FROM trips ORDER BY startDate DESC")
    fun getAllTrips(): Flow<List<Trip>>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    fun getTripById(tripId: Long): Flow<Trip?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip): Long

    @Update
    suspend fun updateTrip(trip: Trip)

    @Delete
    suspend fun deleteTrip(trip: Trip)

    // Places
    @Query("SELECT * FROM places WHERE tripId = :tripId")
    fun getPlacesForTrip(tripId: Long): Flow<List<Place>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: Place)

    @Delete
    suspend fun deletePlace(place: Place)

    // Schedule
    @Query("SELECT * FROM schedule_items WHERE tripId = :tripId ORDER BY dateTime ASC")
    fun getScheduleForTrip(tripId: Long): Flow<List<ScheduleItem>>

    @Query("SELECT * FROM schedule_items ORDER BY dateTime ASC")
    fun getAllScheduleItems(): Flow<List<ScheduleItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleItem(item: ScheduleItem)

    @Delete
    suspend fun deleteScheduleItem(item: ScheduleItem)

    // Expenses
    @Query("SELECT * FROM expenses WHERE tripId = :tripId ORDER BY date DESC")
    fun getExpensesForTrip(tripId: Long): Flow<List<Expense>>

    @Query("SELECT * FROM expenses")
    fun getAllExpenses(): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    // Notes
    @Query("SELECT * FROM notes WHERE tripId = :tripId ORDER BY createdAt DESC")
    fun getNotesForTrip(tripId: Long): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    // Photos
    @Query("SELECT * FROM photos WHERE tripId = :tripId")
    fun getPhotosForTrip(tripId: Long): Flow<List<Photo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: Photo)

    @Delete
    suspend fun deletePhoto(photo: Photo)

    // Packing Items
    @Query("SELECT * FROM packing_items WHERE tripId = :tripId ORDER BY isChecked ASC, itemName ASC")
    fun getPackingItemsForTrip(tripId: Long): Flow<List<PackingItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackingItem(item: PackingItem)

    @Update
    suspend fun updatePackingItem(item: PackingItem)

    @Delete
    suspend fun deletePackingItem(item: PackingItem)

    // Documents
    @Query("SELECT * FROM documents WHERE tripId = :tripId")
    fun getDocumentsForTrip(tripId: Long): Flow<List<Document>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: Document)

    @Delete
    suspend fun deleteDocument(document: Document)

    // User Profile
    @Query("SELECT * FROM users WHERE id = 1")
    fun getUser(): Flow<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    // Методы очистки (используем @Query для надежности)
    @Query("DELETE FROM trips")
    suspend fun deleteAllTrips()

    @Query("DELETE FROM users")
    suspend fun deleteUser()
}
