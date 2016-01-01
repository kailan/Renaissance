package net.hungerstruck.renaissance

import jdk.nashorn.api.scripting.NashornScriptEngineFactory
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
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

    override fun onEnable() {
        saveDefaultConfig()

        Renaissance.initialize(this)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val engine = if (sessions[sender] != null) {
            sessions[sender]!!
        } else {
            sessions[sender] = nashornEngine.getScriptEngine(classLoader)
            sessions[sender]!!
        }

        engine.put("player", sender)
        if (sender is Player) engine.put("rplayer", sender.getRPlayer())
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
}