package com.alexey_freelancee.delivery.utils

open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write


    fun peekContent(): T {

        hasBeenHandled = true
        return content
    }
}