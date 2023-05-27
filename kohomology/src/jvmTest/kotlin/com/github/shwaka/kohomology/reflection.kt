package com.github.shwaka.kohomology

import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

// https://stackoverflow.com/questions/42522739/kotlin-check-if-lazy-val-has-been-initialised
val KProperty0<*>.isLazyInitialized: Boolean
    get() {
        // Prevent IllegalAccessException from JVM access check on private properties.
        val originalAccessLevel = this.isAccessible
        this.isAccessible = true
        val delegate: Any = this.getDelegate() ?: throw Exception("Not delegate!")
        if (delegate !is Lazy<*>) {
            throw Exception("Not Lazy!")
        }
        val isLazyInitialized = delegate.isInitialized()
        // Reset access level.
        this.isAccessible = originalAccessLevel
        return isLazyInitialized
    }

object PrivateMemberAccessor {
    private fun getProperty(target: Any, name: String): KProperty1<out Any, *> {
        for (property in target::class.declaredMemberProperties) {
            if (property.name == name) {
                return property
            }
        }
        throw Exception("Property not found: $name")
    }

    fun getPropertyValue(target: Any, name: String): Any? {
        val property = this.getProperty(target, name)
        property.isAccessible = true
        return property.call(target)
    }

    @Suppress("UNCHECKED_CAST")
    fun isLazyInitialized(target: Any, name: String): Boolean {
        // This cast is necessary to call property.getDelegate(target) (Why?)
        val property = this.getProperty(target, name) as KProperty1<Any, *>
        property.isAccessible = true
        val delegate: Any = property.getDelegate(target) ?: throw Exception("Not delegate!")
        if (delegate !is Lazy<*>) {
            throw Exception("Not Lazy!")
        }
        return delegate.isInitialized()
    }
}
