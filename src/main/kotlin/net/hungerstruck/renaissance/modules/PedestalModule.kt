package net.hungerstruck.renaissance.modules

import com.google.common.collect.Iterables
import net.hungerstruck.renaissance.RPlayer
import net.hungerstruck.renaissance.event.lobby.RLobbyEndEvent
import net.hungerstruck.renaissance.event.player.RPlayerJoinMatchEvent
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.modules.region.BlockRegion
import net.hungerstruck.renaissance.modules.region.RegionModule
import net.hungerstruck.renaissance.rplayer
import net.hungerstruck.renaissance.teleportable
import net.hungerstruck.renaissance.xml.flatten
import net.hungerstruck.renaissance.xml.module.Dependencies
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.jdom2.Document

/**
 * Parses pedestals.
 *
 * Created by molenzwiebel on 21-12-15.
 */
@Dependencies(RegionModule::class)
class PedestalModule(match: RMatch, document: Document, modCtx: RModuleContext) : RModule(match, document, modCtx) {
    val pedestals: List<BlockRegion>
    val pedestalIt: Iterator<BlockRegion>

    init {
        pedestals = document.rootElement.flatten("pedestals", "pedestal").map {
            val parsed = modCtx.regionParser.parse(it.children[0])
            if (parsed !is BlockRegion) throw IllegalArgumentException("Pedestal must be BlockRegion")
            parsed as BlockRegion
        }

        pedestalIt = Iterables.cycle(pedestals).iterator()

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