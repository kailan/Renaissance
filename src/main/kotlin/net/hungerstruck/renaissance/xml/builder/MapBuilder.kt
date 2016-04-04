package net.hungerstruck.renaissance.xml.builder

import net.hungerstruck.renaissance.modules.BoundaryModule
import net.hungerstruck.renaissance.xml.Contributor
import net.hungerstruck.renaissance.xml.RLobbyProperties
import org.bukkit.Difficulty
import org.bukkit.World
import org.bukkit.util.Vector

/**
 * Class that builds maps.
 */
class MapBuilder : AbstractMapBuilder<MapBuilder>() {
    class StringListBuilder : SingleTypeListBuilder<StringListBuilder, String, String>()
    class ContributorListBuilder : SingleTypeListBuilder<ContributorListBuilder, String, Contributor>({ Contributor(it) })

    lateinit var name: String
    lateinit var version: String
    lateinit var objective: String

    var lobby: String? = null
    var lobbyProperties: RLobbyProperties? = null
    fun lobby(f: RLobbyProperties.() -> Unit) {
        lobbyProperties = RLobbyProperties()
        lobbyProperties!!.f()
    }

    lateinit var authors: Collection<Contributor>
    var contributors: Collection<Contributor> = arrayListOf()
    var rules: Collection<String> = arrayListOf("Standard rules apply.")

    var difficulty = Difficulty.NORMAL
    var dimension = World.Environment.NORMAL

    /**
     * Specifies a list of rules (strings).
     */
    fun rules(x: StringListBuilder.() -> Unit) {
        rules = StringListBuilder().run(x)
    }

    /**
     * Specifies a list of authors (strings -> Contributors).
     */
    fun authors(x: ContributorListBuilder.() -> Unit) {
        authors = ContributorListBuilder().run(x)
    }

    /**
     * Specifies a list of contributors (strings -> Contributors).
     */
    fun contributors(x: ContributorListBuilder.() -> Unit) {
        contributors = ContributorListBuilder().run(x)
    }

    class BoundarySettings : BuilderPropertySet<BoundarySettings>() {
        lateinit var center: Vector
        lateinit var min: Vector
        lateinit var max: Vector
    }

    /**
     * Specifies boundary settings.
     */
    fun boundary(x: BoundarySettings.() -> Unit)
            = register<BoundaryModule>(BoundarySettings().build(x))
}

fun map(x: MapBuilder.() -> Unit): MapBuilder {
    val builder = MapBuilder()
    builder.x()
    return builder
}