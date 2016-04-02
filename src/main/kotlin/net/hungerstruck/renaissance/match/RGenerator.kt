package net.hungerstruck.renaissance.match

import org.bukkit.World
import org.bukkit.generator.ChunkGenerator

import java.util.Random

/**
 * Created by teddy on 01/04/2016.
 */
class RGenerator : ChunkGenerator() {

    override fun generate(world: World?, random: Random?, x: Int, z: Int): ByteArray {
        return ByteArray(0)
    }

    override fun generateBlockSections(world: World?, random: Random?, x: Int, z: Int, biomes: ChunkGenerator.BiomeGrid?): Array<ByteArray> {
        return arrayOf(ByteArray(0))
    }

    override fun generateExtBlockSections(world: World?, random: Random?, x: Int, z: Int, biomes: ChunkGenerator.BiomeGrid?): Array<ShortArray> {
        return arrayOf(ShortArray(0))
    }
}
