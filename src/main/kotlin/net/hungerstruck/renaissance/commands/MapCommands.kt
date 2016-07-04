package net.hungerstruck.renaissance.commands

import com.sk89q.minecraft.util.commands.Command
import com.sk89q.minecraft.util.commands.CommandContext
import com.sk89q.minecraft.util.commands.CommandException
import net.hungerstruck.renaissance.rplayer
import net.hungerstruck.renaissance.xml.RMap
import net.hungerstruck.renaissance.xml.RMapInfo
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object MapCommands {
    @JvmStatic
    @Command(aliases = arrayOf("map"), desc = "List map info for current map or the supplied one")
    fun map(args: CommandContext, sender: CommandSender) {
        val player: Player = CommandUtils.getPlayer(sender)
        if (player.rplayer.match == null && player.rplayer.lobby == null)
            throw CommandException("You must be in a match or lobby to execute this command")
        val map: RMap = if (player.rplayer.match != null) CommandUtils.getPlayer(sender).rplayer.match!!.map else CommandUtils.getPlayer(sender).rplayer.lobby!!.lobbyMap
        val mapInfo: RMapInfo = map.mapInfo

        sender.sendMessage(CommandUtils.formatHeader(ChatColor.GOLD.toString() + mapInfo.name + " " + ChatColor.GRAY + mapInfo.version, ChatColor.YELLOW))
        sender.sendMessage(ChatColor.YELLOW.toString() + mapInfo.objective)
        sender.sendMessage(ChatColor.YELLOW.toString() + "Author" + (if (mapInfo.authors.count() > 1) "s" else "") + ": " + mapInfo.authors.map { ChatColor.GOLD.toString() + it.name }.joinToString(", "))

        if (mapInfo.contributors.size > 0)
            sender.sendMessage(ChatColor.YELLOW.toString() + "Contributor" + (if (mapInfo.contributors.count() > 1) "s" else "") + ": " + mapInfo.contributors.map { ChatColor.GOLD.toString() + it.name }.joinToString(", "))

        val rules: Collection<String> = mapInfo.rules
        if (rules.size > 0) {
            sender.sendMessage(ChatColor.YELLOW.toString() + "Rules:")
            for ((idx, rule) in rules.withIndex())
                sender.sendMessage(ChatColor.GRAY.toString() + (idx + 1).toString() + ") " + ChatColor.GOLD + rule)

        CommandUtils.formatHeader(ChatColor.GOLD.toString() + "HungerStruck", ChatColor.YELLOW)
        }
    }
}
