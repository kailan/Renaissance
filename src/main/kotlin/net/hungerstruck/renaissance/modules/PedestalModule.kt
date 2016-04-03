package net.hungerstruck.renaissance.modules

import net.hungerstruck.renaissance.RPlayer
import net.hungerstruck.renaissance.event.lobby.RLobbyEndEvent
import net.hungerstruck.renaissance.event.player.RPlayerJoinMatchEvent
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.modules.region.BlockRegion
import net.hungerstruck.renaissance.rplayer
import net.hungerstruck.renaissance.teleportable
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent

/**
 * Parses pedestals.
 *
 * Created by molenzwiebel on 21-12-15.
 */
class PedestalModule(match: RMatch, modCtx: RModuleContext) : RModule(match, modCtx) {
    lateinit var pedestals: List<BlockRegion>
    lateinit var pedestalIt: Iterator<BlockRegion>

    init {
        // pedestalIt = Iterables.cycle(pedestals).iterator()

        registerEvents()
    }

    @EventHandler
    public fun onPlayerJoinMatch(event: RPlayerJoinMatchEvent) {
        if (!isMatch(event.match)) return
        if (match.state != RMatch.State.STARTING) return

        event.player.state = RPlayer.State.PARTICIPATING
        event.player.reset()
        event.player.teleport(pedestalIt.next().loc.toLocation(match.world).teleportable)
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