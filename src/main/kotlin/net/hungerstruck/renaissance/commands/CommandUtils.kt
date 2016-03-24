package net.hungerstruck.renaissance.commands

import org.bukkit.command.CommandException
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Created by neiljohari on 3/24/16.
 */
object CommandUtils {
    /**
     * Gets a player from sender
     *
     * @param sender CommandSender
     * @return Player
     * @throws CommandException thrown if the sender is not a player
     */
    public fun getPlayer( sender: CommandSender) : Player {
        // If the sender is an in game player then return a casted sender
        if ((sender is Player)) return sender;
        // otherwise stop the rest of the command from executing and provide a helpful message to the player
        throw CommandException("You must be a player to execute this command!");
    }
}