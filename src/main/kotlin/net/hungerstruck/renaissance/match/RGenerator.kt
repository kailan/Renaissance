package net.hungerstruck.renaissance.match

import org.bukkit.World
import org.bukkit.generator.ChunkGenerator
import java.util.*
import javax.annotation.Nonnull


/**
 * Created by teddy on 01/04/2016.
 */
class RGenerator : ChunkGenerator() {

    @Override
    @Nonnull
    override fun generateChunkData(@Nonnull world: World, @Nonnull random: Random, x: Int, z: Int, @Nonnull biome: BiomeGrid): ChunkData {
        return createChunkData(world);
    }

}
