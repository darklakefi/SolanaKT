package com.solana.vendor.borshj

/**
 * Android-compatible Optional implementation for API levels < 24
 * Replacement for java.util.Optional to maintain compatibility
 */
data class Optional<T>(private val value: T?) {
    val isPresent: Boolean get() = value != null
    
    fun get(): T = value ?: throw NoSuchElementException("No value present")
    
    companion object {
        fun <T> empty(): Optional<T> = Optional(null)
        
        fun <T> of(value: T): Optional<T> {
            if (value == null) throw NullPointerException("Value cannot be null")
            return Optional(value)
        }
        
        fun <T> ofNullable(value: T?): Optional<T> = Optional(value)
    }
}