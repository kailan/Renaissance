package net.hungerstruck.renaissance.xml.builder

import net.hungerstruck.renaissance.modules.BoundaryModule
import net.hungerstruck.renaissance.xml.Contributor
import org.bukkit.Difficulty
import org.bukkit.World
import org.bukkit.util.Vector

/**
 * Class that builds maps.
 *
 * Created by molenzwiebel on 03-04-16.
 */
class MapBuilder : AbstractMapBuilder<MapBuilder>() {
    class StringListBuilder : SingleTypeListBuilder<StringListBuilder, String, String>()
    class ContributorListBuilder : SingleTypeListBuilder<ContributorListBuilder, String, Contributor>({ Contributor(it) })

    class BoundarySettings : BuilderPropertySet<BoundarySettings>() {
        lateinit var center: Vector
        lateinit var min: Vector
        lateinit var max: Vector
    }

    class MapInfoSettings : BuilderPropertySet<MapInfoSettings>() {
        lateinit var name: String
        lateinit var objective: String
        lateinit var version: String
        var difficulty: Difficulty = Difficulty.EASY
        var dimension: World.Environment = World.Environment.NORMAL
    }

    /**
     * Specifies a list of rules (strings).
     */
    fun rules(x: StringListBuilder.() -> Unit)
            = register<BoundaryModule>("rules", StringListBuilder().run(x))

    /**
     * Specifies a list of authors (strings -> Contributors).
     */
    fun authors(x: ContributorListBuilder.() -> Unit)
            = register<BoundaryModule>("rules", ContributorListBuilder().run(x))

    /**
     * Specifies a list of contributors (strings -> Contributors).
     */
    fun contributors(x: ContributorListBuilder.() -> Unit)
            = register<BoundaryModule>("rules", ContributorListBuilder().run(x))

    /**
     * Specifies boundary settings.
     */
    fun boundary(x: BoundarySettings.() -> Unit)
            = register<BoundaryModule>(BoundarySettings().build(x))

    /**
     * Specifies general map settings.
     */
    fun info(x: MapInfoSettings.() -> Unit)
            = register<BoundaryModule>(MapInfoSettings().build(x))
}

fun map(x: MapBuilder.() -> Unit): MapBuilder {
    val builder = MapBuilder()
    builder.x()
    return builder
}