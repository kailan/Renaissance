package net.hungerstruck.renaissance

import net.hungerstruck.renaissance.config.RConfig
import net.hungerstruck.renaissance.countdown.CountdownManager
import net.hungerstruck.renaissance.listeners.ConnectionListener
import net.hungerstruck.renaissance.listeners.LobbyListener
import net.hungerstruck.renaissance.listeners.SimpleEventsListener
import net.hungerstruck.renaissance.lobby.RLobbyManager
import net.hungerstruck.renaissance.match.RMatchManager
import net.hungerstruck.renaissance.modules.*
import net.hungerstruck.renaissance.modules.oregen.OregenModule
import net.hungerstruck.renaissance.modules.scoreboard.ScoreboardModule
import net.hungerstruck.renaissance.modules.ux.BloodModule
import net.hungerstruck.renaissance.modules.ux.ParticleModule
import net.hungerstruck.renaissance.modules.ux.SoundModule
import net.hungerstruck.renaissance.settings.Settings
import net.hungerstruck.renaissance.util.ActionBarSender
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
    val matchManager: RMatchManager = RMatchManager(mapContext)
    val lobbyManager: RLobbyManager = RLobbyManager()
    val countdownManager: CountdownManager = CountdownManager()
    val eventManager: REventManager = REventManager()

    fun initialize(plugin: JavaPlugin) {
        this.plugin = plugin

        ActionBarSender.runTaskTimerAsynchronously(plugin, 5, 5)

        RModuleRegistry.register<ChestModule>()
        RModuleRegistry.register<ChatModule>()
        RModuleRegistry.register<DeathModule>()
        RModuleRegistry.register<PedestalModule>()
        RModuleRegistry.register<BoundaryModule>()
        RModuleRegistry.register<GameRuleModule>()
        RModuleRegistry.register<SanityModule>()
        RModuleRegistry.register<TimeLockModule>()
        RModuleRegistry.register<TimeSetModule>()
        RModuleRegistry.register<OregenModule>()
        RModuleRegistry.register<ThirstModule>()
        RModuleRegistry.register<ScoreboardModule>()
        RModuleRegistry.register<SoundModule>()
        RModuleRegistry.register<ParticleModule>()
        RModuleRegistry.register<BloodModule>()
        RModuleRegistry.register<ChunkLoadModule>()

        mapContext.loadMaps(File(RConfig.Maps.mapDir))
        mapContext.resolveLobbies()

        if (mapContext.getMaps().size == 0) throw IllegalStateException("Must have at least one map to start loading Renaissance.")
        if (mapContext.getMaps().filter { it.mapInfo.lobbyProperties != null }.size == 0) throw IllegalStateException("Must have at least one lobby to start loading Renaissance.")
        if (mapContext.getMaps().filter { it.mapInfo.lobbyProperties == null }.size == 0) throw IllegalStateException("Must have at least one game map to start loading Renaissance.")
        lobbyManager.createLobbyFor(mapContext.getMaps().first { it.mapInfo.lobbyProperties == null })

        Bukkit.getPluginManager().registerEvents(RPlayer.Companion, plugin)
        Bukkit.getPluginManager().registerEvents(LobbyListener(), plugin)
        Bukkit.getPluginManager().registerEvents(ConnectionListener(), plugin)
        Bukkit.getPluginManager().registerEvents(SimpleEventsListener(), plugin)

        Settings.register()
    }


}