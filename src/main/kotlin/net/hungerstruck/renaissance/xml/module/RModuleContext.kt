package net.hungerstruck.renaissance.xml.module

import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.modules.region.RegionManager
import net.hungerstruck.renaissance.modules.region.RegionParser
import org.jdom2.Document
import kotlin.reflect.KClass

/**
 * Created by molenzwiebel on 20-12-15.
 */
class RModuleContext {
    protected final val modules: MutableSet<RModule> = hashSetOf()

    protected final val match: RMatch
    protected final val document: Document

    final val regionManager: RegionManager = RegionManager()
    final val regionParser: RegionParser = RegionParser(regionManager)

    constructor(match: RMatch, document: Document) {
        this.match = match
        this.document = document

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

        val instance = info.constructor.newInstance(match, document, this)
        modules.add(instance)
        return true
    }
}