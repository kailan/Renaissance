package net.hungerstruck.renaissance.xml

import com.google.common.collect.ImmutableList
import net.hungerstruck.renaissance.util.LiquidMetal
import java.io.File

/**
 * Manages maps.
 *
 * Created by molenzwiebel on 21-12-15.
 */
class RMapContext {
    protected final val maps: MutableMap<String, RMap> = hashMapOf()

    public fun getMapExact(name: String) = maps[name]
    public fun getMaps() = ImmutableList.copyOf(maps.values)

    public fun loadMaps(directory: File) {
        if (!directory.exists() || !directory.isDirectory) throw IllegalArgumentException("Illegal map path: ${directory.absolutePath}")

        for (f in directory.listFiles()) {
            if (!f.isDirectory) continue

            if (File(f, "map.xml").exists()) {
                val map = RMap(f)
                maps.put(map.mapInfo.name, map)
            }
        }
    }

    public fun resolveLobbies() {

    }

    public fun matchMap(query: String): RMap? {
        return LiquidMetal.fuzzyMatch(getMaps(), query, { it.mapInfo.name }, 0.9)
    }
}