package net.hungerstruck.renaissance.util;

import net.hungerstruck.renaissance.RPlayer

object TitleUtil {
    public fun sendTitle(player: RPlayer, title: String, subtitle: String, fadeIn: Int, stay: Int, fadeOut: Int ) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut)
    }
}