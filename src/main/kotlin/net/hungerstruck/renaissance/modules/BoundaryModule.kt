package net.hungerstruck.renaissance.modules

import net.hungerstruck.renaissance.get
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.modules.region.RectangleRegion
import net.hungerstruck.renaissance.modules.region.RegionModule
import net.hungerstruck.renaissance.rplayer
import net.hungerstruck.renaissance.xml.module.Dependencies
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import net.hungerstruck.renaissance.xml.parseVector2D
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.util.Vector
import org.jdom2.Document

/**
 * Boundary module.
 *
 * Created by molenzwiebel on 21-12-15.
 */
@Dependencies(RegionModule::class)
class BoundaryModule(match: RMatch, document: Document, modCtx: RModuleContext) : RModule(match, document, modCtx) {
    val center: Vector
    val region: RectangleRegion

    init {
        val boundaryEl = document.rootElement.getChild("boundary") ?: throw IllegalArgumentException("Map ${match.map.mapInfo.name} has no boundary element")
        
        val region = modCtx.regionParser.parse(boundaryEl.children[0])
        if (region !is RectangleRegion)
            throw IllegalArgumentException("Expected boundary of ${match.map.mapInfo.name} to be a rectangle")

        this.center = boundaryEl["center"].parseVector2D()
        this.region = region

        registerEvents()
    }

    @EventHandler
    public fun onMove(event: PlayerMoveEvent) {
        if (!isMatch(event.player)) return

        if (match.state == RMatch.State.PLAYING) {
            if (!region.contains(event.to.toVector())) {
                event.player.vehicle?.eject()
                event.to = event.from
            }
        }
    }

    @EventHandler
    fun onTeleport(event: PlayerTeleportEvent){
        if(!isMatch(event.player)) return

        if (match.state == RMatch.State.PLAYING) {
            if (!region.contains(event.to.toVector())) {
                event.isCancelled = true
            }
        }
    }
}