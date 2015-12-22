package net.hungerstruck.renaissance

import org.bukkit.plugin.java.JavaPlugin

/**
 * Bukkit entry point for Renaissance.
 *
 * Created by molenzwiebel on 22-12-15.
 */
class RenaissancePlugin : JavaPlugin() {
    override fun onEnable() {
        Renaissance.initialize(this)
    }
}