package net.hungerstruck.renaissance

import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.modules.PedestalModule
import net.hungerstruck.renaissance.modules.region.RegionModule
import net.hungerstruck.renaissance.xml.RMapContext
import net.hungerstruck.renaissance.xml.module.RModuleRegistry
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

/**
 * Main class.
 *
 * Created by molenzwiebel on 20-12-15.
 */
class Renaissance : JavaPlugin(), Listener {
    public val mapContext: RMapContext = RMapContext()

    override fun onEnable() {
        logger.info("Hello world!")

        RModuleRegistry.register<RegionModule>()
        RModuleRegistry.register<PedestalModule>()

        mapContext.loadMaps(File("maps"))

        for (map in mapContext.getMaps())
            println(map.mapInfo)

        RMatch(mapContext.matchMap("Alps")!!, Bukkit.getWorld("world"))

        Bukkit.getPluginManager().registerEvents(RPlayer.Companion, this)

        super.onEnable()
    }

    @EventHandler
    fun onLogin(event: PlayerLoginEvent) {
        println("Player logged in: ${event.player.name}")
        println("RPlayer: ${event.player.getRPlayer()}")
    }
}