package net.hungerstruck.renaissance.commands

import com.sk89q.minecraft.util.commands.Command
import com.sk89q.minecraft.util.commands.CommandContext
import com.sk89q.minecraft.util.commands.CommandException
import com.sk89q.minecraft.util.commands.CommandPermissions
import net.hungerstruck.renaissance.Renaissance
import net.hungerstruck.renaissance.lobby.RLobbyEndCountdown
import net.hungerstruck.renaissance.rplayer
import net.hungerstruck.renaissance.sendPrefixMessage
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

/**
 * Created by teddy on 04/07/2016.
 */
object AdminCommands {

    @JvmStatic
    @Command(aliases = arrayOf("start"), desc = "Force start the match", flags = "f")
    @CommandPermissions("renaissance.admin")
    fun startMatch(args: CommandContext, sender: CommandSender) {
        val player = CommandUtils.getPlayer(sender)
        val force = args.hasFlag('f')

        if(player.rplayer.lobby == null) throw CommandException("You must be in a lobby to run this command.")
        if(Renaissance.countdownManager.hasCountdown(RLobbyEndCountdown::class.java) and !force) throw CommandException("Countdown has already started. Use the '-f' flag to force start.")

        if(force) {
            player.sendPrefixMessage("Force starting the game.", ChatColor.RED)
            if(Renaissance.countdownManager.hasCountdown(RLobbyEndCountdown::class.java)) Renaissance.countdownManager.cancel(RLobbyEndCountdown::class.java)
            player.rplayer.lobby!!.end()
        } else {
            player.sendPrefixMessage("Starting lobby countdown.", ChatColor.RED)
            player.rplayer.lobby!!.startCountdown()
        }
    }

}