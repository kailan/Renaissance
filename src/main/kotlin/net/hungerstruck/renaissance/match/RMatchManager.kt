package net.hungerstruck.renaissance.match

import net.hungerstruck.renaissance.RRotationManager
import net.hungerstruck.renaissance.util.FileUtil
import net.hungerstruck.renaissance.xml.RMapContext
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.generator.ChunkGenerator
import java.io.File

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

    public fun cycle(oldMatch: RMatch?) {
        val worldName = "match-${matchCount++}"
        val nextMap = rotationManager.getNextAndIncrement()

        val worldFolder = File(Bukkit.getServer().worldContainer, worldName)
        FileUtil.copy(nextMap.location, worldFolder)
        FileUtil.delete(File(worldFolder, "session.lock"))
        FileUtil.delete(File(worldFolder, "uid.dat"))
        FileUtil.delete(File(worldFolder, "players"))

        val gen = WorldCreator(worldName).generator(object : ChunkGenerator() {}).environment(nextMap.mapInfo.dimension)
        val world = Bukkit.createWorld(gen)
        world.isAutoSave = false
        world.difficulty = nextMap.mapInfo.difficulty

        val match = RMatch(nextMap, world)
        matches[world] = match

        if (oldMatch != null) {
            unloadMatch(oldMatch)
        }

        println("[+] Loaded ${nextMap.mapInfo.friendlyDescription}")
    }

    public fun unloadMatch(oldMatch: RMatch) {
        val dir = oldMatch.world.worldFolder
        Bukkit.unloadWorld(oldMatch.world, true)
        FileUtil.delete(dir)
        matches.remove(oldMatch.world)

        println("[+] Unloaded ${oldMatch.map.mapInfo.friendlyDescription}")
    }
}