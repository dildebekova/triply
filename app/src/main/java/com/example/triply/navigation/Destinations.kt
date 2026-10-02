package com.example.triply.navigation

import kotlinx.serialization.Serializable

@Serializable
object MyTripsDestination

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
