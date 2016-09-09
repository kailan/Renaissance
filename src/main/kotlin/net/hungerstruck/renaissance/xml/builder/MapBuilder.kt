package net.hungerstruck.renaissance.xml.builder

import net.hungerstruck.renaissance.modules.*
import net.hungerstruck.renaissance.modules.region.*
import net.hungerstruck.renaissance.util.RandomCollection
import net.hungerstruck.renaissance.xml.Contributor
import net.hungerstruck.renaissance.xml.RLobbyProperties
import org.bukkit.Difficulty
import org.bukkit.World
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

/**
 * Class that builds maps.
 */
class MapBuilder : AbstractMapBuilder<MapBuilder>() {
    class StringListBuilder : SingleTypeListBuilder<StringListBuilder, String, String>()
    class RegionListBuilder : SingleTypeListBuilder<RegionListBuilder, RRegion, RRegion>()
    class ContributorListBuilder : SingleTypeListBuilder<ContributorListBuilder, String, Contributor>({ Contributor(it) })
    class BlockRegionListBuilder : SingleTypeListBuilder<BlockRegionListBuilder, Vector, BlockRegion>({ BlockRegion(it) })

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
        lateinit var region: RectangleRegion
    }

    /**
     * Specifies boundary settings.
     */
    fun boundary(x: BoundarySettings.() -> Unit)
            = register<BoundaryModule>(BoundarySettings().build(x))

    class ChestSettings(val instance: MapBuilder) : BuilderPropertySet<ChestSettings>() {
        var mode: ChestModule.Mode = ChestModule.Mode.AUTOMATIC
        var chests: MutableList<BlockRegion> = arrayListOf()

        operator fun Vector.unaryMinus() {
            chests.add(BlockRegion(this))
        }

        operator fun BlockRegion.unaryMinus() {
            chests.add(this)
        }

        fun setupInitialItems(f: (RandomCollection<ItemStack>) -> Unit) {
            instance.register<ChestModule>("setupInitialItems", f)
        }

        fun setupFeastItems(f: (RandomCollection<ItemStack>, Double) -> Unit) {
            instance.register<ChestModule>("setupFeastItems", f)
        }
    }

    /**
     * Specifies chest settings.
     */
    fun chests(x: ChestSettings.() -> Unit)
            = register<ChestModule>(ChestSettings(this).build(x))

    class GameRuleSettings : BuilderPropertySet<GameRuleSettings>() {
        var rules: MutableList<Pair<String, Boolean>> = arrayListOf()

        operator fun String.unaryMinus() = this
        operator fun String.timesAssign(other: Boolean) {
            rules.add(Pair(this, other))
        }
    }

    /**
     * Specifies gamerule settings.
     */
    fun gamerules(x: GameRuleSettings.() -> Unit)
            = register<GameRuleModule>(GameRuleSettings().build(x))

    /**
     * Specifies pedestal locations.
     */
    fun pedestals(x: BlockRegionListBuilder.() -> Unit)
            = register<PedestalModule>("pedestals", BlockRegionListBuilder().run(x))

    class SanitySettings : BuilderPropertySet<SanitySettings>() {
        var airHeight = 0
        var overallLightLevel = 0
    }

    /**
     * Specifies sanity settings.
     */
    fun sanity(x: SanitySettings.() -> Unit)
            = register<SanityModule>(SanitySettings().build(x))

    class TimeLockSettings : BuilderPropertySet<TimeLockSettings>() {
        var locked = false
    }


    class TNTSettings : BuilderPropertySet<TNTSettings>() {
        var blockDamage = false
        var instantIgnite = false
        var `yield` = -1
        var damageUnderWater = false
    }

    /**
     * Specifies tnt settings.
     */
    fun tnt(x: TNTSettings.() -> Unit)
            = register<TNTSettingsModule>(TNTSettings().build(x))


    class ProjectileSettings : BuilderPropertySet<ProjectileSettings>() {
        var underwaterVelocityModifier = 0.6f
    }

    /**
     * Specifies projectile settings.
     */
    fun projectiles(x: ProjectileSettings.() -> Unit)
            = register<ProjectileModule>(ProjectileSettings().build(x))

    /**
     * Specifies timelock settings.
     */
    fun timelock(x: TimeLockSettings.() -> Unit)
            = register<TimeLockModule>(TimeLockSettings().build(x))

    class TimeSetSettings : BuilderPropertySet<TimeSetSettings>() {
        var value = 0L
    }

    /**
     * Specifies timeset settings.
     */
    fun timeset(x: TimeSetSettings.() -> Unit)
            = register<TimeSetModule>(TimeSetSettings().build(x))

    /**
     * ==============================
     * ====== Region building. ======
     * ==============================
     */
    fun block(location: Vector) = BlockRegion(location)
    fun circle(base: IntRange, radius: Int) = CircleRegion(base.first.toDouble(), base.last.toDouble(), radius)
    fun cuboid(min: Vector, max: Vector) = CuboidRegion(min, max)
    fun cylinder(base: Vector, radius: Int, height: Int) = CylinderRegion(base, radius, height)
    fun rectangle(min: IntRange, max: IntRange) = RectangleRegion(Vector(min.first, 0, min.last), Vector(max.first, 0, max.last))
    fun sphere(center: Vector, radius: Int) = SphereRegion(center, radius)

    fun intersect(x: RegionListBuilder.() -> Unit) = IntersectRegion(RegionListBuilder().run(x).toList())
    fun union(x: RegionListBuilder.() -> Unit) = UnionRegion(RegionListBuilder().run(x).toList())
    operator fun RRegion.not() = InvertedRegion(this)
}

/**
 * Implements a..b..c.
 * a..b is an IntRange, we then override IntRange..c.
 */
operator fun IntRange.rangeTo(other: Int): Vector {
    return Vector(first, last, other)
}

fun map(x: MapBuilder.() -> Unit): MapBuilder {
    val builder = MapBuilder()
    builder.x()
    return builder
}