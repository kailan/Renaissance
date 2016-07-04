package net.hungerstruck.renaissance

import com.sk89q.bukkit.util.CommandsManagerRegistration
import com.sk89q.minecraft.util.commands.*
import jdk.nashorn.api.scripting.NashornScriptEngineFactory
import net.hungerstruck.renaissance.commands.AdminCommands
import net.hungerstruck.renaissance.commands.EventCommands
import net.hungerstruck.renaissance.commands.MapCommands
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandException
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import javax.script.ScriptEngine

/**
 * Bukkit entry point for Renaissance.
 *
 * Created by molenzwiebel on 22-12-15.
 */
class RenaissancePlugin : JavaPlugin() {
    private val sessions: MutableMap<CommandSender, ScriptEngine> = hashMapOf()
    private val nashornEngine: NashornScriptEngineFactory = NashornScriptEngineFactory()
    private val commands: CommandsManager<CommandSender> = object : CommandsManager<CommandSender>() {
        override fun hasPermission(sender: CommandSender, perm: String): Boolean {
            return sender is ConsoleCommandSender || sender.hasPermission(perm);
        }
    }

    override fun onEnable() {
        saveDefaultConfig()

        Renaissance.initialize(this)

        val cmdRegister: CommandsManagerRegistration = CommandsManagerRegistration(this, this.commands);
        cmdRegister.register(MapCommands::class.java)
        cmdRegister.register(AdminCommands::class.java)
        cmdRegister.register(EventCommands::class.java)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name.equals("debug")) {
            val engine = if (sessions[sender] != null) {
                sessions[sender]!!
            } else {
                sessions[sender] = nashornEngine.getScriptEngine(classLoader)
                sessions[sender]!!
            }

            engine.put("player", sender)
            if (sender is Player) engine.put("rplayer", sender.rplayer)
            engine.put("Renaissance", Renaissance)
            engine.put("server", Bukkit.getServer())
            engine.eval("var Bukkit = org.bukkit.Bukkit")

            try {
                val res = engine.eval(args.joinToString(" "))

                sender.sendMessage("${ChatColor.GRAY}> ${ChatColor.WHITE}$res")
            } catch (e: Exception) {
                sender.sendMessage("${ChatColor.GRAY}> ${ChatColor.RED}${e.message}")
            }

            return true
        }

        try {
            commands.execute(command.name, args, sender, sender);
        } catch (e: CommandPermissionsException) {
            sender.sendMessage(ChatColor.RED.toString() + "You do not have permission to execute this command.");
        } catch (e: MissingNestedCommandException) {
            sender.sendMessage(ChatColor.RED.toString() + e.usage);
        } catch (e: CommandUsageException) {
            sender.sendMessage(ChatColor.RED.toString() + e.message);
            sender.sendMessage(ChatColor.RED.toString() + e.usage);
        } catch (e: WrappedCommandException) {
            val cause = e.cause
            if (cause is NumberFormatException) {
                val extra = if (cause.message?.contains("For input string:") ?: false) {
                    ", not " + cause.message?.replace("For input string: ", "")
                } else "."
                sender.sendMessage(ChatColor.RED.toString() + "Whoops. We expected a number" + extra);
            } else {
                sender.sendMessage(ChatColor.RED.toString() + "We're sorry, but some unexpected error occurred. Please contact Enviark support at our website, hungerstruck.net, if the error persists.");
                e.printStackTrace();
            }
        } catch (e: CommandException) {
            sender.sendMessage(ChatColor.RED.toString() + e.message);
        }

        return true
    }
}