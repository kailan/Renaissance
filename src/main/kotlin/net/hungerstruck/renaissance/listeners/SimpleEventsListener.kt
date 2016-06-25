package net.hungerstruck.renaissance.listeners

import net.hungerstruck.renaissance.Renaissance
import net.hungerstruck.renaissance.event.match.RMatchEndEvent
import net.hungerstruck.renaissance.match.RMatch
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.weather.WeatherChangeEvent
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

/**
 * Cancels world events in matches that have not started
 */
class SimpleEventsListener : Listener {
    @EventHandler
    public fun onMatchEnd(event: RMatchEndEvent) {
        var f : File = File("someonefixthislater.txt");
        if(f.isDirectory) {
            f.deleteRecursively()
        }
        if(!f.exists()) {
            f.createNewFile()
            f.appendText("0")
        } else {
            var reader : BufferedReader = BufferedReader(FileReader(f));
            var mapIndex : Int = reader.readLine().toInt();

            f.delete()
            f.appendText(mapIndex++.toString())
        }
    }

    /**
     * Cancels an event if the match state is not PLAYING (the world has been loaded but players are not in it)
     */
    private fun cancelEventIfNotStarted(event: org.bukkit.event.Cancellable, world: World) {
        if (shouldCancel(world))
            event.isCancelled = true
    }

    /**
     * Returns true if the match exists and is not in PLAYING state
     */
    private fun shouldCancel(world: World): Boolean {
        return Renaissance.matchManager.matches.get(world) != null && Renaissance.matchManager.matches.get(world)!!.state != RMatch.State.PLAYING
    }

    @EventHandler
    public fun onBlockRedstoneEvent(event: BlockRedstoneEvent) {
        if (shouldCancel(event.block.world))
            event.newCurrent = event.oldCurrent
    }

    @EventHandler
    public fun onCreatureSpawn(event: CreatureSpawnEvent) {
        cancelEventIfNotStarted(event, event.entity.world)
    }

    @EventHandler
    public fun onWeatherChange(event: WeatherChangeEvent) {
        cancelEventIfNotStarted(event, event.world)
    }

    @EventHandler(priority = EventPriority.LOW)
    public fun onEntityExplode(event: EntityExplodeEvent) {
        cancelEventIfNotStarted(event, event.location.world)
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onBurn(event: BlockBurnEvent) {
        cancelEventIfNotStarted(event, event.block.world)
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onFade(event: BlockFadeEvent) {
        cancelEventIfNotStarted(event, event.block.world)
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onForm(event: BlockFormEvent) {
        cancelEventIfNotStarted(event, event.block.world)
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onSpread(event: BlockSpreadEvent) {
        cancelEventIfNotStarted(event, event.block.world)
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onFromTo(event: BlockFromToEvent) {
        cancelEventIfNotStarted(event, event.block.world)
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onBlockIgnite(event: BlockIgniteEvent) {
        cancelEventIfNotStarted(event, event.block.world)
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onPistonExtend(event: BlockPistonExtendEvent) {
        cancelEventIfNotStarted(event, event.block.world)
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onPistonRetract(event: BlockPistonRetractEvent) {
        cancelEventIfNotStarted(event, event.block.world)
    }

}
