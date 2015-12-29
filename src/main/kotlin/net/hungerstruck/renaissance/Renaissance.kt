package net.hungerstruck.renaissance

import net.hungerstruck.renaissance.config.RConfig
import net.hungerstruck.renaissance.lobby.RLobbyManager
import net.hungerstruck.renaissance.match.RMatchManager
import net.hungerstruck.renaissance.modules.*
import net.hungerstruck.renaissance.modules.region.RegionModule
import net.hungerstruck.renaissance.xml.RMapContext
import net.hungerstruck.renaissance.xml.module.RModuleRegistry
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

/**
 * Main class.
 *
 * Created by molenzwiebel on 20-12-15.
 */
object Renaissance {
    var plugin: JavaPlugin? = null

    val mapContext: RMapContext = RMapContext()
    val rotationManager: RRotationManager = RRotationManager(File("rotation.txt"), mapContext)
    val matchManager: RMatchManager = RMatchManager(mapContext, rotationManager)
    val lobbyManager: RLobbyManager = RLobbyManager()

    fun initialize(plugin: JavaPlugin) {
        this.plugin = plugin

        //ConfvarPlugin.get().register(RenaissanceDebug())

        RModuleRegistry.register<RegionModule>()
        RModuleRegistry.register<PedestalModule>()
        RModuleRegistry.register<BoundaryModule>()
        RModuleRegistry.register<EventLocationModule>()
        RModuleRegistry.register<GameRuleModule>()
        RModuleRegistry.register<SanityModule>()
        RModuleRegistry.register<TimeLockModule>()
        RModuleRegistry.register<TimeSetModule>()

        mapContext.loadMaps(File(RConfig.Maps.mapDir))
        mapContext.resolveLobbies()

        rotationManager.load()

        lobbyManager.createLobbyFor(rotationManager.getNextAndIncrement())

        Bukkit.getPluginManager().registerEvents(RPlayer.Companion, plugin)
    }
}