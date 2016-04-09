package net.hungerstruck.renaissance.modules

import com.google.common.collect.Iterables
import net.hungerstruck.renaissance.RPlayer
import net.hungerstruck.renaissance.event.lobby.RLobbyEndEvent
import net.hungerstruck.renaissance.event.player.RPlayerJoinMatchEvent
import net.hungerstruck.renaissance.lookAt
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.modules.region.BlockRegion
import net.hungerstruck.renaissance.rplayer
import net.hungerstruck.renaissance.teleportable
import net.hungerstruck.renaissance.xml.builder.inject
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.util.Vector
import java.util.*

/**
 * Parses pedestals.
 *
 * Created by molenzwiebel on 21-12-15.
 */
class PedestalModule(match: RMatch, val modCtx: RModuleContext) : RModule(match, modCtx) {
    @inject lateinit var pedestals: List<BlockRegion>
    lateinit var pedestalIt: Iterator<BlockRegion>

    override fun init() {
        pedestalIt = Iterables.cycle(ArrayList(pedestals)).iterator()
        registerEvents()
    }

    @EventHandler
    public fun onPlayerJoinMatch(event: RPlayerJoinMatchEvent) {
        if (!isMatch(event.match)) return
        if (match.state != RMatch.State.STARTING) return

        event.player.state = RPlayer.State.PARTICIPATING
        event.player.reset()
        event.player.teleport(pedestalIt.next().loc.add(0.0, 0.5, 0.0).toLocation(match.world).teleportable)

        val boundaryCenter = modCtx.getModule<BoundaryModule>()?.center
        if(boundaryCenter != null)
            event.player.teleport(event.player.location.lookAt(boundaryCenter.toLocation(match.world)))
        else
            event.player.teleport(Vector(0,0,0).toLocation(match.world))
    }

    @EventHandler
    public fun onLobbyEnd(event: RLobbyEndEvent) {
        match.beginCountdown()
    }

    @EventHandler
    public fun onPlayerMove(event: PlayerMoveEvent) {
        if (!isMatch(event.player)) return

        if (match.state == RMatch.State.STARTING && event.player.rplayer.state == RPlayer.State.PARTICIPATING) {
            if (event.to.blockX != event.from.blockX || event.to.blockZ != event.from.blockZ)
                event.to = event.from
        }
    }
}