package net.hungerstruck.renaissance.modules.oregen

import net.hungerstruck.renaissance.event.RMatchLoadEvent
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.modules.BoundaryModule
import net.hungerstruck.renaissance.xml.module.Dependencies
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import org.bukkit.event.EventHandler
import org.jdom2.Document

/**
 * Handles ore generation.
 *
 * Created by molenzwiebel on 10-01-16.
 */
@Dependencies(BoundaryModule::class)
class OregenModule(match: RMatch, document: Document, modCtx: RModuleContext) : RModule(match, document, modCtx) {
    init {
        registerEvents()
    }

    @EventHandler
    public fun onMatchLoad(event: RMatchLoadEvent) {
        //FIXME: Generate ores.
        println("Generating ores...")
    }
}