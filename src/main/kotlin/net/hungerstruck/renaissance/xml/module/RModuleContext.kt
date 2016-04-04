package net.hungerstruck.renaissance.xml.module

import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.xml.builder.AbstractMapBuilder
import net.hungerstruck.renaissance.xml.builder.MapBuilder
import net.hungerstruck.renaissance.xml.builder.inject
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
        
        injectProperties(match.map.mapBuilder, instance)
        instance.init()

        modules.add(instance)
        return true
    }

    private fun injectProperties(builder: AbstractMapBuilder<MapBuilder>, mod: RModule) {
        for (field in mod.javaClass.declaredFields) {
            if (!field.isAnnotationPresent(inject::class.java)) continue

            val value = builder.properties.filter { it.module == mod.javaClass && it.name == field.name }.firstOrNull()
            if (value == null) {
                throw IllegalStateException("No value passed for field ${field.name} in module ${mod.javaClass.simpleName}")
            } else {
                field.isAccessible = true
                field.set(mod, value.value)
            }
        }
    }
}