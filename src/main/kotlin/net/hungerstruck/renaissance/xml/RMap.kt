package net.hungerstruck.renaissance.xml

import net.hungerstruck.renaissance.mapAs
import org.bukkit.Difficulty
import org.bukkit.World
import org.jdom2.Document
import org.jdom2.input.SAXBuilder
import java.io.File

/**
 * Represents a map.
 *
 * Created by molenzwiebel on 20-12-15.
 */
class RMap {
    val mapInfo: RMapInfo
    val location: File

    val document: Document

    constructor(loc: File) {
        this.location = loc
        this.document = SAXBuilder().build(File(loc, "map.xml"))
        this.mapInfo = loadMapInfo()
    }

    private fun loadMapInfo(): RMapInfo {
        val root = document.rootElement

        val name = root.getChildTextNormalize("name") ?: throw RuntimeException("Map must have name")
        val version = root.getChildTextNormalize("version") ?: throw RuntimeException("Map must have version")
        val objective = root.getChildTextNormalize("objective") ?: throw RuntimeException("Map must have objective")

        val authors = root.flatten("authors", "author").mapAs { Contributor(textNormalize, getAttributeValue("contribution")) }
        if (authors.size < 1) throw RuntimeException("Map $name must have at least one author")

        val contributors = root.flatten("contributors", "contributor").mapAs { Contributor(textNormalize, getAttributeValue("contribution")) }
        val rules = root.flatten("rules", "rule").mapAs { textNormalize }

        val difficulty = root.getChildTextNormalize("difficulty").toEnum(Difficulty.NORMAL)!!
        val dimension = root.getChildTextNormalize("dimension").toEnum(World.Environment.NORMAL)!!

        val lobbyEl = root.getChild("lobby")
        var lobbyProperties: RLobbyProperties? = null
        if (lobbyEl != null) {
            val lobbyName = lobbyEl.getChildTextNormalize("name") ?: lobbyEl.textNormalize
            val blockBreaking = lobbyEl.getChild("blockbreaking") != null
            val damage = lobbyEl.getChild("damage") != null

            lobbyProperties = RLobbyProperties(lobbyName, blockBreaking, damage)
        }

        return RMapInfo(name, version, lobbyProperties, objective, authors, contributors, rules, difficulty, dimension)
    }
}