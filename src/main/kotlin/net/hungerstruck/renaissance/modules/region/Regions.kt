package net.hungerstruck.renaissance.modules.region

import net.hungerstruck.renaissance.between
import net.hungerstruck.renaissance.pow
import org.bukkit.util.Vector

/**
 * Main parent interface for all regions.
 *
 * Created by molenzwiebel on 21-12-15.
 */
interface RRegion {
    public fun contains(point: Vector): Boolean
}

data class BlockRegion(val loc: Vector) : RRegion {
    override fun contains(point: Vector) = loc == point
}

data class CircleRegion(val baseX: Double, val baseZ: Double, val radius: Int) : RRegion {
    override fun contains(point: Vector) = ((point.x - baseX) pow 2.0) + ((point.z - baseZ) pow 2.0) <= radius * radius
}

data class CuboidRegion(val min: Vector, val max: Vector) : RRegion {
    override fun contains(point: Vector) = point.isInAABB(min, max)
}

data class CylinderRegion(val base: Vector, val radius: Int, val height: Int) : RRegion {
    override fun contains(point: Vector): Boolean {
        if (point.y < this.base.y || point.y > (this.base.y + this.height))
            return false
        return Math.pow(point.x - base.x, 2.0) + Math.pow(point.z - base.z, 2.0) <= (radius * radius)
    }
}

data class IntersectRegion(val regions: List<RRegion>) : RRegion {
    override fun contains(point: Vector) = regions.all { it.contains(point) }
}

data class InvertedRegion(val region: RRegion) : RRegion {
    override fun contains(point: Vector) = !region.contains(point)
}

data class RectangleRegion(val min: Vector, val max: Vector) : RRegion {
    override fun contains(point: Vector) = point.x.between(min.x, max.x) && point.z.between(min.z, max.z)
}

data class SphereRegion(val center: Vector, val radius: Int) : RRegion {
    override fun contains(point: Vector) = point.isInSphere(center, radius.toDouble())
}

data class UnionRegion(val regions: List<RRegion>) : RRegion {
    override fun contains(point: Vector) = regions.any { it.contains(point) }
}