package net.hungerstruck.renaissance.xml

import java.io.File

/**
 * Represents a map.
 *
 * Created by molenzwiebel on 20-12-15.
 */
class RMap {
    // Make this lateinit for now so that any references to this field don't blow up.
    lateinit var mapInfo: RMapInfo
    val location: File

    constructor(loc: File) {
        this.location = loc
        //this.mapInfo = loadMapInfo()
    }

    //private fun loadMapInfo(): RMapInfo {
    //    return RMapInfo(name, version, lobbyName, lobbyProperties, objective, authors, contributors, rules, difficulty, dimension)
    //}
}