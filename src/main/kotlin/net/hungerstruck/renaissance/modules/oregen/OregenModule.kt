package net.hungerstruck.renaissance.modules.oregen

import net.hungerstruck.renaissance.event.match.RMatchLoadEvent
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.modules.BoundaryModule
import net.hungerstruck.renaissance.util.RandomCollection
import net.hungerstruck.renaissance.xml.module.Dependencies
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import net.minecraft.server.v1_8_R3.BlockPosition
import net.minecraft.server.v1_8_R3.Blocks
import net.minecraft.server.v1_8_R3.IBlockData
import net.minecraft.server.v1_8_R3.WorldGenMinable
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.event.EventHandler
import java.util.*

/**
 * Handles ore generation.
 *
 * Created by molenzwiebel on 10-01-16.
 */
@Dependencies(BoundaryModule::class)
class OregenModule(match: RMatch, modCtx: RModuleContext) : RModule(match, modCtx) {
    val ores: RandomCollection<IBlockData> = RandomCollection()
    val random: Random = Random()

    val oresPerChunk: Int = 10
    val maxVeinSize: Int = 8

    override fun init() {
        ores[0.05] = Blocks.LAPIS_ORE.blockData
        ores[0.001] = Blocks.DIAMOND_ORE.blockData
        ores[0.025] = Blocks.GOLD_ORE.blockData
        ores[0.1] = Blocks.REDSTONE_ORE.blockData
        ores[1.0] = Blocks.COAL_ORE.blockData
        ores[0.5] = Blocks.IRON_ORE.blockData
        ores[5.0] = Blocks.STONE.blockData

        registerEvents()
    }

    @EventHandler
    public fun onMatchLoad(event: RMatchLoadEvent) {
        // Generate list of chunks in the world.
        val boundary = getModule<BoundaryModule>()!!.region

        val lowerChunkX = boundary.min.blockX shr 4
        val lowerChunkZ = boundary.min.blockZ shr 4
        val upperChunkX = boundary.max.blockX shr 4
        val upperChunkZ = boundary.max.blockZ shr 4

        val worldServer = (event.match.world as CraftWorld).handle

        println("Starting ore generation...")
        val start = System.currentTimeMillis()

        for (x in lowerChunkX..upperChunkX) {
            for (z in lowerChunkZ..upperChunkZ) {
                event.match.world.loadChunk(x, z)

                for (i in 1..oresPerChunk) {
                    val blockX = x * 16 + random.nextInt(16)
                    val blockZ = z * 16 + random.nextInt(16)
                    val blockY = random.nextInt(69) + 1

                    WorldGenMinable(ores.next(), maxVeinSize).generate(worldServer, Random(), BlockPosition(blockX, blockY, blockZ))
                }
            }
        }

        println("Generated ores in ${System.currentTimeMillis() - start} ms!")
    }
}