package net.hungerstruck.renaissance.util

import java.lang.reflect.Field
import java.util.HashMap

import org.apache.commons.lang.NullArgumentException
import com.google.common.collect.ImmutableMap

import net.minecraft.server.v1_8_R3.Block
import net.minecraft.server.v1_8_R3.MathHelper
import org.apache.commons.lang3.reflect.FieldUtils

/**
 * Overrides particular values on blocks,

 * @author Kristian
 */
class BlockOverride(// The block we will override
        private val block: Block) {

    // Old values
    private val oldValues = HashMap<String, Any>()
    private val fieldCache = HashMap<String, Field>()

    /**
     * Update the given field with a new value.
     * @param fieldName - name of field.
     * *
     * @param value - new value.
     * *
     * @throws IllegalArgumentException If the field name is NULL or the field doesn't exist.
     * *
     * @throws RuntimeException If we don't have security clearance.
     */
    operator fun set(fieldName: String, value: Any) {

        try {
            // Write the value directly
            FieldUtils.writeField(getField(fieldName), block, value)
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Unable to read field.", e)
        }

    }

    /**
     * Retrieves the current field value.
     * @param fieldName - name of field.
     * *
     * @throws IllegalArgumentException If the field name is NULL or the field doesn't exist.
     * *
     * @throws RuntimeException If we don't have security clearance.
     */
    operator fun get(fieldName: String): Any {
        try {
            // Read the value directly
            return FieldUtils.readField(getField(fieldName), block)
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Unable to read field.", e)
        }

    }

    /**
     * Retrieves the old vanilla field value.
     * @param fieldName - name of field.
     * *
     * @throws IllegalArgumentException If the field name is NULL or the field doesn't exist.
     * *
     * @throws RuntimeException If we don't have security clearance.
     */
    fun getVanilla(fieldName: String?): Any {
        if (fieldName == null)
            throw NullArgumentException("fieldName")

        if (oldValues.containsKey(fieldName))
            return oldValues[fieldName]!!
        else
            return get(fieldName)
    }

    /**
     * Retrieves a immutable representation of the stored vanilla values.
     * @return Old values.
     */
    val vanillaValues: ImmutableMap<String, Any>
        get() = ImmutableMap.copyOf(oldValues)

    /**
     * Reset everything to vanilla.
     */
    fun revertAll() {
        // Reset what we have
        for (stored in oldValues.keys) {
            set(stored, getVanilla(stored))
        }

        // Remove list
        oldValues.clear()
    }

    /**
     * Called when we wish to persist every change, even when this class is garbage collected.
     */
    fun saveAll() {
        oldValues.clear()
    }

    private fun getField(fieldName: String): Field {

        var cached: Field? = fieldCache[fieldName]

        if (cached == null) {
            cached = FieldUtils.getField(block.javaClass, fieldName, true)

            // Remember this particular field
            if (cached != null) {
                fieldCache.put(fieldName, cached)
            } else {
                throw IllegalArgumentException("Cannot locate field " + fieldName)
            }
        }

        return cached
    }
}