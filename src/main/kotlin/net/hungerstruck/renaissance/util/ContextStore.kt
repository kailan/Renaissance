package net.hungerstruck.renaissance.util

import java.util.*

/**
 * Overcast Commons ContextStore util
 *
 * Created by molenzwiebel on 21-12-15.
 */
open class ContextStore<T> : Iterable<Map.Entry<String, T>> {
    protected val store: MutableMap<String, T> = linkedMapOf()

    override fun iterator() = store.entries.iterator()

    /**
     * Adds an object to this context with a random name.
     * @param obj Object to add.
     * @return Randomly generated name for this object.
     */
    fun add(obj: T): String {
        while (true) {
            val name = UUID.randomUUID().toString()
            try {
                add(name, obj)
                return name
            } catch (ignored: IllegalArgumentException) {
                // ignore, try to find another name that works
            }
        }
    }

    /**
     * Adds an object to this context with a specified name.
     * @param name Name for this object.
     * @param obj  Object to add.
     */
    fun add(name: String, obj: T) {
        if (store.containsKey(name)) {
            throw IllegalArgumentException("ContextStore already has an object assigned to '$name'")
        }
        store.put(name, obj)
    }

    /**
     * Gets an object by the name it was registered with.
     * @param name Name to look up.
     * @return Object that was registered to the given name or null if none exists.
     */
    operator fun get(name: String) = store[name]

    /**
     * Gets the first name the specified object was registered with.
     * @param obj Object to look up.
     * @return Name for the object or null if none is found.
     */
    fun getName(obj: T) = store.entries.firstOrNull { it.value == obj }?.key
}