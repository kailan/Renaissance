package net.hungerstruck.renaissance.config

import kotlin.reflect.KProperty

/**
 * Created by molenzwiebel on 22-12-15.
 */
class ConfigEntry<T>(private val path: String) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return RConfig.config.get(path) as T
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        RConfig.config.set(path, value)
        RConfig.config.save(RConfig.configFile)
    }
}

class DefaultConfigEntry<T>(private val path: String, private val default: T) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return RConfig.config.get(path, default) as T
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        RConfig.config.set(path, value)
        RConfig.config.save(RConfig.configFile)
    }
}

class ComputedConfigEntry<T>(private val path: String, private val read: (String) -> T, private val write: (T) -> String) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return read(RConfig.config.getString(path))
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        RConfig.config.set(path, write(value))
        RConfig.config.save(RConfig.configFile)
    }
}

fun <T> path(s: String): ConfigEntry<T> {
    return ConfigEntry(s)
}

fun <T> path(s: String, def: T): DefaultConfigEntry<T> {
    return DefaultConfigEntry(s, def)
}

fun <T> path(s: String, fn1: (String) -> T, fn2: (T) -> String): ComputedConfigEntry<T> {
    return ComputedConfigEntry(s, fn1, fn2)
}