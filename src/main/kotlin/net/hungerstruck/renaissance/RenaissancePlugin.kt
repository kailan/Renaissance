package net.hungerstruck.renaissance

import co.enviark.speak.Translation
import com.sk89q.bukkit.util.CommandsManagerRegistration
import com.sk89q.minecraft.util.commands.*
import jdk.nashorn.api.scripting.NashornScriptEngineFactory
import net.hungerstruck.renaissance.commands.CommandUtils
import net.hungerstruck.renaissance.commands.EventCommands
import net.hungerstruck.renaissance.commands.MapCommands
import net.hungerstruck.renaissance.util.ColorUtil
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
            sender.sendMessage(Translation("command.no-permission").to(CommandUtils.getLocale(sender)).put("p", ColorUtil.errorColors[0]).get());
        } catch (e: MissingNestedCommandException) {
            sender.sendMessage(Translation("command.invalid-usage").to(CommandUtils.getLocale(sender)).put("p", ColorUtil.errorColors[0]).get());
            sender.sendMessage(ColorUtil.errorColors[0] + e.usage);
        } catch (e: CommandUsageException) {
            sender.sendMessage(Translation("command.invalid-usage").to(CommandUtils.getLocale(sender)).put("p", ColorUtil.errorColors[0]).get());
            sender.sendMessage(ColorUtil.errorColors[0] + e.usage);
        } catch (e: WrappedCommandException) {
            val cause = e.cause
            if (cause is NumberFormatException) {
                val extra = if (cause.message?.contains("For input string:") ?: false) {
                    cause.message?.replace("For input string: ", "")
                } else ""
                sender.sendMessage(Translation("command.expected-number").to(CommandUtils.getLocale(sender)).put("p", ColorUtil.errorColors[0]).put("s", ColorUtil.errorColors[1]).put("extra", extra!!).get());
            } else {
                sender.sendMessage(Translation("command.unexpected-error").to(CommandUtils.getLocale(sender)).put("p", ColorUtil.errorColors[0]).get());
                e.printStackTrace();
            }
        } catch (e: CommandException) {
            sender.sendMessage(e.message);
        }

        return true
    }
}