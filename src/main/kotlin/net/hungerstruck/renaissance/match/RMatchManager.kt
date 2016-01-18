package net.hungerstruck.renaissance.match

import net.hungerstruck.renaissance.RRotationManager
import net.hungerstruck.renaissance.config.RConfig
import net.hungerstruck.renaissance.util.FileUtil
import net.hungerstruck.renaissance.xml.RMap
import net.hungerstruck.renaissance.xml.RMapContext
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.generator.ChunkGenerator
import java.io.File
import java.util.*

/**
 * Manages matches.
 *
 * Created by molenzwiebel on 22-12-15.
 */
class RMatchManager {
    private val matches: MutableMap<World, RMatch> = hashMapOf()
    private val mapContext: RMapContext
    private val rotationManager: RRotationManager

    private var matchCount: Int = 0

    constructor(mapCtx: RMapContext, rotMan: RRotationManager) {
        this.mapContext = mapCtx
        this.rotationManager = rotMan
    }

    public fun constructMatch(nextMap: RMap): RMatch {
        val worldName = "match-${matchCount++}"

        val worldFolder = File(Bukkit.getServer().worldContainer, worldName)
        if(worldFolder.exists()) worldFolder.deleteRecursively()
        FileUtil.copyWorldFolder(nextMap.location, worldFolder)

        val gen = WorldCreator(worldName).generator(object : ChunkGenerator() {}).generateStructures(false).environment(nextMap.mapInfo.dimension)
        val world = Bukkit.createWorld(gen)
        world.isAutoSave = false
        world.difficulty = nextMap.mapInfo.difficulty

        val match = RMatch(nextMap, world)
        matches[world] = match

        println("[+] Loaded ${nextMap.mapInfo.friendlyDescription}")
        return match
    }

    public fun unloadMatch(oldMatch: RMatch) {
        //FIXME: Move this somewhere else, players should be teleported out of the world before this is called.
        for (participant in oldMatch.players) {
            participant.match = null
            participant.previousState?.restore(participant)
            participant.previousState = null
        }

        val dir = oldMatch.world.worldFolder
        Bukkit.unloadWorld(oldMatch.world, true)
        FileUtil.delete(dir)
        matches.remove(oldMatch.world)

        println("[+] Unloaded ${oldMatch.map.mapInfo.friendlyDescription}")
    }

    // Note: May return null if there are no active matches.
    public fun findMatch(strategy: RConfig.JoinStrategy): RMatch? {
        if (matches.isEmpty()) return null

        return when (strategy) {
            RConfig.JoinStrategy.FIRST -> matches.values.first()
            RConfig.JoinStrategy.RANDOM -> matches.values.toArrayList()[Random().nextInt(matches.size)]
            RConfig.JoinStrategy.SMALLEST -> matches.values.minBy { it.players.size }
        }
    }
}