package com.example.triply.data.repository

import com.example.triply.data.local.*
import kotlinx.coroutines.flow.Flow

class TriplyRepository(private val triplyDao: TriplyDao) {
    // Trips
    fun getAllTripsStream(): Flow<List<Trip>> = triplyDao.getAllTrips()
    fun getTripStream(id: Long): Flow<Trip?> = triplyDao.getTripById(id)
    suspend fun insertTrip(trip: Trip) = triplyDao.insertTrip(trip)
    suspend fun updateTrip(trip: Trip) = triplyDao.updateTrip(trip)
    suspend fun deleteTrip(trip: Trip) = triplyDao.deleteTrip(trip)

    // Places
    fun getPlacesStream(tripId: Long): Flow<List<Place>> = triplyDao.getPlacesForTrip(tripId)
    suspend fun insertPlace(place: Place) = triplyDao.insertPlace(place)
    suspend fun deletePlace(place: Place) = triplyDao.deletePlace(place)

    // Schedule
    fun getScheduleStream(tripId: Long): Flow<List<ScheduleItem>> = triplyDao.getScheduleForTrip(tripId)
    suspend fun insertScheduleItem(item: ScheduleItem) = triplyDao.insertScheduleItem(item)
    suspend fun deleteScheduleItem(item: ScheduleItem) = triplyDao.deleteScheduleItem(item)

    // Expenses
    fun getExpensesStream(tripId: Long): Flow<List<Expense>> = triplyDao.getExpensesForTrip(tripId)
    suspend fun insertExpense(expense: Expense) = triplyDao.insertExpense(expense)
    suspend fun deleteExpense(expense: Expense) = triplyDao.deleteExpense(expense)

    // Notes
    fun getNotesStream(tripId: Long): Flow<List<Note>> = triplyDao.getNotesForTrip(tripId)
    suspend fun insertNote(note: Note) = triplyDao.insertNote(note)
    suspend fun deleteNote(note: Note) = triplyDao.deleteNote(note)

    // Photos
    fun getPhotosStream(tripId: Long): Flow<List<Photo>> = triplyDao.getPhotosForTrip(tripId)
    suspend fun insertPhoto(photo: Photo) = triplyDao.insertPhoto(photo)
    suspend fun deletePhoto(photo: Photo) = triplyDao.deletePhoto(photo)
}
