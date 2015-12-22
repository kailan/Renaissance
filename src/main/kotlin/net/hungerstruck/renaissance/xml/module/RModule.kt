package net.hungerstruck.renaissance.xml.module

import net.hungerstruck.renaissance.Renaissance
import net.hungerstruck.renaissance.match.RMatch
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.jdom2.Document

/**
 * Base class for modules.
 *
 * Created by molenzwiebel on 21-12-15.
 */
abstract class RModule : Listener {
    final val match: RMatch
    final val document: Document
    final var moduleContext: RModuleContext

    private val listeners: MutableList<Listener> = arrayListOf()

    constructor(match: RMatch, document: Document, modCtx: RModuleContext) {
        this.match = match
        this.document = document
        this.moduleContext = modCtx
    }

    protected inline fun <reified T : RModule> getModule(): T? {
        return moduleContext.getModule<T>()
    }

    protected fun registerEvents(instance: Listener = this) {
        if (listeners.contains(instance)) {
            throw IllegalStateException("Listener $instance is already registered.")
        }

        listeners.add(instance)
        Bukkit.getPluginManager().registerEvents(instance, Renaissance.plugin)
    }

    /**
     * Called when the module gets unloaded. Can be overwritten by the module.
     */
    public fun cleanup() {
        for (listener in listeners) {
            HandlerList.unregisterAll(listener)
        }
    }
}