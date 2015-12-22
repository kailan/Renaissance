package net.hungerstruck.renaissance

import com.google.common.io.Files
import net.hungerstruck.renaissance.xml.RMap
import net.hungerstruck.renaissance.xml.RMapContext
import java.io.File
import java.nio.charset.Charset

/**
 * Manages rotation.
 *
 * Created by molenzwiebel on 22-12-15.
 */
class RRotationManager {
    private val rotationFile: File
    var rotation: List<RMap> = listOf()

    private var current: Int = -1
    private val mapCtx: RMapContext

    constructor(file: File, mapCtx: RMapContext) {
        this.rotationFile = file
        this.mapCtx = mapCtx
    }

    public fun load() {
        try {
            val contents = Files.readLines(rotationFile, Charset.defaultCharset())
            rotation = contents.map {
                mapCtx.matchMap(it) ?: throw IllegalArgumentException("Unknown map in rotation: ${it}")
            }
        } catch (e: Exception) {
            throw IllegalArgumentException("Could not read rotation: ${e.message}")
        }
    }

    public fun getNext(): RMap {
        var newI = current + 1
        if (newI == rotation.size) newI = 0
        return rotation[newI]
    }

    public fun getNextAndIncrement(): RMap {
        this.current += 1
        if (current >= rotation.size) current = 0
        return rotation[current]
    }

    public fun getPrevious(): RMap {
        var newI = current - 1
        if (newI < 0) newI = rotation.size - 1
        return rotation[newI]
    }
}