package net.hungerstruck.renaissance.xml.module

import net.hungerstruck.renaissance.match.RMatch
import kotlin.reflect.KClass

/**
 * Created by molenzwiebel on 20-12-15.
 */
class RModuleContext {
    final val modules: MutableSet<RModule> = hashSetOf()
    protected final val match: RMatch

    constructor(match: RMatch) {
        this.match = match

        for (info in RModuleRegistry.MODULES) {
            loadModule(info)
        }
    }

    public inline fun <reified T : RModule> getModule(): T? {
        return modules.filterIsInstance<T>().firstOrNull()
    }

    public inline fun <reified T : RModule> hasModule(): Boolean {
        return hasModule(T::class)
    }

    public fun hasModule(clazz: KClass<out RModule>): Boolean {
        return modules.filterIsInstance(clazz.java).any()
    }

    private fun loadModule(info: RModuleInfo): Boolean {
        if (hasModule(info.clazz)) return true

        for (dep in info.dependencies) {
            if (!loadModule(dep)) return false
        }

        val instance = info.constructor.newInstance(match, this)
        modules.add(instance)
        return true
    }
}