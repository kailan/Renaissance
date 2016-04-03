package net.hungerstruck.renaissance.modules.ux

import net.hungerstruck.renaissance.event.match.RMatchCountdownTickEvent
import net.hungerstruck.renaissance.event.match.RMatchStartEvent
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import org.bukkit.Sound
import org.bukkit.event.EventHandler

/**
 * Created by teddy on 30/03/2016.
 */
class SoundModule(match: RMatch, modCtx: RModuleContext) : RModule(match, modCtx) {
    init {
        registerEvents()
    }

    @EventHandler
    fun onMatchountdownTick(event: RMatchCountdownTickEvent) {
        if (event.timeLeft <= 5) {
            for (player in event.match.alivePlayers) {
                player.playSound(player.location, Sound.BLOCK_ANVIL_LAND, 1f, 2f)
                player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.5f)
            }
        }
    }

    @EventHandler
    fun onMatchStart(event: RMatchStartEvent) {
        for (player in event.match.alivePlayers) {
            player.playSound(player.location, Sound.ENTITY_ENDERDRAGON_GROWL, 1f, 1f)
        }
    }
}
