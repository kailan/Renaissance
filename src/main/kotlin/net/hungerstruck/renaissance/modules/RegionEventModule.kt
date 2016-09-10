package net.hungerstruck.renaissance.modules

import net.hungerstruck.renaissance.event.match.RMatchStartEvent
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.modules.region.*
import net.hungerstruck.renaissance.xml.builder.inject
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import org.bukkit.event.EventHandler
import org.bukkit.util.NumberConversions
import org.bukkit.util.Vector

class RegionEventModule(match: RMatch, modCtx: RModuleContext) : RModule(match, modCtx) {
    @inject var events: (RMatch, RModuleContext) -> Unit = {x,y ->}

    override fun init() {
        registerEvents()
    }

    @EventHandler
    fun onMatchStartEvent(event: RMatchStartEvent) {
        if (!isMatch(event.match)) return
        events(match, moduleContext)
    }

    /**
     * Map utility method for mapmakers to get the blocks of a region easily
     *
     * Is not really meant for use on "infinite" regions, such as circles or rectangles, but support is provided given a min and a max optional parameter
     *
     * This is something that mapmakers should use sparingly, as it involves iterating large regions and performing lots of calculations
     */
    fun getBlocks(region: RRegion, minY: Int = 0, maxY: Int = 1) : List<Vector> {
        if(region is UnionRegion) {
            val ret = arrayListOf<Vector>()
            for(subRegion in region.regions)
                ret.addAll(getBlocks(subRegion))
            return ret
        } else if(region is IntersectRegion) {
            val ret = arrayListOf<Vector>()
            for(subRegion in region.regions)
                ret.addAll(getBlocks(subRegion))
            return ret
        } else if(region is BlockRegion) {
            return listOf(region.loc)
        } else if(region is CircleRegion) {
            val ret = arrayListOf<Vector>()
            for(x in  (region.baseX - region.radius).toInt() .. (region.baseX + region.radius).toInt())
                for (z in (region.baseZ - region.radius).toInt()..(region.baseZ + region.radius).toInt())
                    if (Math.pow(x - region.baseX, 2.0) + Math.pow(z - region.baseZ, 2.0) < region.radius * region.radius)
                        for( y in minY..maxY)
                            ret.add(Vector(x, y, z))
            return ret
        } else if (region is CuboidRegion) {
            val ret = arrayListOf<Vector>()
            for (x in region.min.x.toInt()..region.max.x.toInt())
                for (y in region.min.y.toInt()..region.max.y.toInt())
                    for (z in region.min.z.toInt()..region.max.z.toInt())
                        ret.add(Vector(x, y, z))
            return ret
        } else if (region is  RectangleRegion) {
            val ret = arrayListOf<Vector>()
            for (x in region.min.x.toInt()..region.max.x.toInt())
                for (y in minY..maxY)
                    for (z in region.min.z.toInt()..region.max.z.toInt())
                        ret.add(Vector(x, y, z))
            return ret
        } else if (region is CylinderRegion) {
            val ret = arrayListOf<Vector>()
            for(x in  (region.base.x - region.radius).toInt() .. (region.base.x + region.radius).toInt())
                for (z in (region.base.z - region.radius).toInt()..(region.base.z + region.radius).toInt())
                    if (Math.pow(x - region.base.x, 2.0) + Math.pow(z - region.base.z, 2.0) <= (region.radius * region.radius))
                        for( y in region.base.y.toInt() .. (region.base.y + region.height).toInt())
                            ret.add(Vector(x, y, z))
            return ret
        } else if (region is SphereRegion) {
            val ret = arrayListOf<Vector>()
            for(x in  (region.center.x - region.radius).toInt() .. (region.center.x + region.radius).toInt())
                for (z in (region.center.z - region.radius).toInt()..(region.center.z + region.radius).toInt())
                    for( y in (region.center.y - region.radius).toInt() .. (region.center.y + region.radius).toInt())
                        if (NumberConversions.square(region.center.x - x) + NumberConversions.square(region.center.y - y) + NumberConversions.square(region.center.z - z) <= NumberConversions.square(region.radius.toDouble())) {
                            ret.add(Vector(x, y, z))
                        }
            return ret
        }


        return arrayListOf()
    }

}

