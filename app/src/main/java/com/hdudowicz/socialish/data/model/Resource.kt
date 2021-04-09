package com.hdudowicz.socialish.data.model

// Resource class for wrapping objects from async operations
sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val exception: Exception) : Resource<Nothing>()

    // Method to display a string result for debugging
    override fun toString(): String {
        return when (this) {
            is Error -> "Error exception=$exception"
            is Success<*> -> "Success data=$data"
        }
    }
}