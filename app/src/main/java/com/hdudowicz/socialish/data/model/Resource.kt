package com.hdudowicz.socialish.data.model

/**
 * Resource class for wrapping the results or errors of async operations like Firebase calls.
 *
 * @param T Type of object contained in successful resource
 */
sealed class Resource<out T> {
    /**
     * Successful operation result wrapper class
     *
     * @param T Type of object in wrapper
     * @property data Wrapped successful result object
     * @constructor Create Resource.Success wrapper for data object
     */
    data class Success<out T>(val data: T) : Resource<T>()

    /**
     * Operation error exception wrapper class
     *
     * @property exception thrown by operation
     * @constructor Create Resource.Error wrapper with an exception object
     */
    data class Error(val exception: Exception) : Resource<Nothing>()

    // Method to display a string result for debugging
    override fun toString(): String {
        return when (this) {
            is Error -> "Error exception=$exception"
            is Success<*> -> "Success data=$data"
        }
    }
}