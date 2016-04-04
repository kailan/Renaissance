package net.hungerstruck.renaissance.modules

import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.modules.region.RectangleRegion
import net.hungerstruck.renaissance.xml.builder.inject
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.util.Vector

/**
 * Boundary module.
 *
 * Created by molenzwiebel on 21-12-15.
 */
class BoundaryModule(match: RMatch, modCtx: RModuleContext) : RModule(match, modCtx) {
    @inject lateinit var center: Vector
    @inject lateinit var region: RectangleRegion

    override fun init() {
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