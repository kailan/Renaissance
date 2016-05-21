package net.hungerstruck.renaissance.modules

import co.enviark.speak.Translation
import net.hungerstruck.renaissance.RPlayer
import net.hungerstruck.renaissance.config.RConfig
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.rplayer
import net.hungerstruck.renaissance.xml.module.Dependencies
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.player.AsyncPlayerChatEvent

/**
 * Created by molenzwiebel on 03-01-16.
 */
@Dependencies
class ChatModule(match: RMatch, modCtx: RModuleContext) : RModule(match, modCtx) {
    override fun init() {
        registerEvents()
    }

    //FIXME: Currently, if a player is not in a match or lobby, everyone (including people in a match) hears their message. Maybe change this behaviour?
    //TODO: localise all of this
    @EventHandler
    public fun onChat(event: AsyncPlayerChatEvent) {
        if (!isMatch(event.player)) return

        val rplayer = event.player.rplayer
        event.isCancelled = true

        if (match.state == RMatch.State.PLAYING) {
            if (rplayer.state == RPlayer.State.PARTICIPATING) {
                match.sendPrefixlessMessage(Translation("${ChatColor.YELLOW}Player${ChatColor.GRAY}${ChatColor.BOLD} | ${ChatColor.RESET}${rplayer.displayName} ${ChatColor.GRAY}${RConfig.General.mainMessagePrefix}${event.message}"), { it.location.distance(rplayer.location) <= RConfig.Chat.radius || it.state == RPlayer.State.SPECTATING })
            } else {
                match.sendPrefixlessMessage(Translation("${ChatColor.AQUA}Spectator${ChatColor.GRAY}${ChatColor.BOLD} | ${ChatColor.RESET}${rplayer.displayName} ${ChatColor.GRAY}${RConfig.General.mainMessagePrefix}${event.message}"), { it.state == RPlayer.State.SPECTATING })
            }
        } else {
            match.sendPrefixlessMessage(Translation("${rplayer.displayName} ${ChatColor.GRAY}${RConfig.General.mainMessagePrefix}${event.message}"))
        }
    }
}