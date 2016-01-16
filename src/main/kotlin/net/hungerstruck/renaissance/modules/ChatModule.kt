package net.hungerstruck.renaissance.modules

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
import org.jdom2.Document

/**
 * Created by molenzwiebel on 03-01-16.
 */
@Dependencies
class ChatModule(match: RMatch, document: Document, modCtx: RModuleContext) : RModule(match, document, modCtx) {
    init {
        registerEvents()
    }

    //FIXME: Currently, if a player is not in a match or lobby, everyone (including people in a match) hears their message. Maybe change this behaviour?
    @EventHandler
    public fun onChat(event: AsyncPlayerChatEvent) {
        if (!isMatch(event.player)) return

        val rplayer = event.player.rplayer
        event.isCancelled = true

        if (match.state == RMatch.State.PLAYING) {
            if (rplayer.state == RPlayer.State.PARTICIPATING) {
                match.sendMessage("<${rplayer.displayName}> ${event.message}", { it.location.distance(rplayer.location) <= RConfig.Chat.radius })
            } else {
                match.sendMessage("<${ChatColor.AQUA}${rplayer.displayName}${ChatColor.WHITE}> ${event.message}", { it.state == RPlayer.State.SPECTATING })
            }
        } else {
            match.sendMessage("<${ChatColor.AQUA}${rplayer.displayName}${ChatColor.WHITE}> ${event.message}")
        }
    }
}