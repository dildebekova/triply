package com.example.triply.navigation

import kotlinx.serialization.Serializable

@Serializable
object MyTripsDestination

@Serializable
object CalendarDestination

@Serializable
object StatisticsDestination

@Serializable
object ProfileDestination

@Serializable
data class CreateEditTripDestination(val tripId: Long = -1L)

@Serializable
data class TripDetailsDestination(val tripId: Long)

@Serializable
data class PlacesDestination(val tripId: Long)

@Serializable
data class ScheduleDestination(val tripId: Long)

@Serializable
data class ExpensesDestination(val tripId: Long)

@Serializable
data class NotesDestination(val tripId: Long)

@Serializable
data class PhotosDestination(val tripId: Long)

@Serializable
data class PackingListDestination(val tripId: Long)

@Serializable
data class DocumentsDestination(val tripId: Long)

@Serializable
data class TripMapDestination(val tripId: Long)
