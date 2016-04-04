package net.hungerstruck.renaissance.xml

import net.hungerstruck.renaissance.xml.builder.MapBuilder
import net.hungerstruck.renaissance.xml.builder.MapBuilderClassLoader
import java.io.File

/**
 * Represents a map.
 *
 * Created by molenzwiebel on 20-12-15.
 */
class RMap {
    var mapBuilder: MapBuilder

    var mapInfo: RMapInfo
    val location: File

    constructor(loc: File) {
        this.location = loc

        this.mapBuilder = this.loadMapBuilder()
        this.mapInfo = loadMapInfo()
    }

    private fun loadMapBuilder(): MapBuilder {
        val loader = MapBuilderClassLoader(this.location)

        val mapClass: Class<*> = loader.loadClass("Map")
        val mapInstance: Any = mapClass.newInstance()

        for (method in mapClass.declaredMethods) {
            if (method.name == "map") {
                method.isAccessible = true
                return method.invoke(mapInstance) as MapBuilder
            }
        }

        throw RuntimeException("Map builder has no method map()")
    }

    private fun loadMapInfo(): RMapInfo {
        val name = mapBuilder.name
        val version = mapBuilder.version
        val objective = mapBuilder.objective
        val authors = mapBuilder.authors
        val contributors = mapBuilder.contributors
        val rules = mapBuilder.rules
        val difficulty = mapBuilder.difficulty
        val dimension = mapBuilder.dimension
        val lobby = mapBuilder.lobby
        val lobbyProperties = mapBuilder.lobbyProperties

        return RMapInfo(name, version, lobby, lobbyProperties, objective, authors, contributors, rules, difficulty, dimension)
    }
}