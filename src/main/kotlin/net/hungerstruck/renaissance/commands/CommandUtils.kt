package net.hungerstruck.renaissance.commands

import co.enviark.speak.Translation
import com.google.common.base.Strings
import net.hungerstruck.renaissance.util.ColorUtil
import org.bukkit.ChatColor
import org.bukkit.command.CommandException
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.ChatPaginator
import java.util.*

object CommandUtils {
    /**
     * Gets a player from sender
     *
     * @param sender CommandSender
     * @return Player
     * @throws CommandException thrown if the sender is not a player
     */
    public fun getPlayer(sender: CommandSender): Player {
        if (sender is Player) return sender
        throw CommandException(Translation("command.not-player").to(CommandUtils.getLocale(sender)).put("p", ColorUtil.errorColors[0]).get())
    }


    public fun formatHeader(title: String): String {
        return formatHeader(title, ChatColor.RED)
    }

    public fun formatHeader(title: String, paddingColor: ChatColor): String {
        val titleLen: Int = ChatColor.stripColor(title).length
        val padLen: Int = (ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH - titleLen) / 2 - 2
        val padding: String = paddingColor.toString() + ChatColor.STRIKETHROUGH + Strings.repeat("-", padLen)
        return padding + ChatColor.RESET + " " + title + " " + padding
    }

    fun getLocale(sender: CommandSender): Locale {
        if(sender is Player) return sender.currentLocale
        return Locale.ENGLISH
    }
}