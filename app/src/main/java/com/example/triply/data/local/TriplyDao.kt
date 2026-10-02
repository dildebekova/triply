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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleItem(item: ScheduleItem)

    @Delete
    suspend fun deleteScheduleItem(item: ScheduleItem)

    // Expenses
    @Query("SELECT * FROM expenses WHERE tripId = :tripId ORDER BY date DESC")
    fun getExpensesForTrip(tripId: Long): Flow<List<Expense>>

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
}
