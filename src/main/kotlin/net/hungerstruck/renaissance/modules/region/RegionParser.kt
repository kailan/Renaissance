package net.hungerstruck.renaissance.modules.region

import net.hungerstruck.renaissance.get
import net.hungerstruck.renaissance.xml.parseVector2D
import net.hungerstruck.renaissance.xml.parseVector3D
import org.jdom2.Element

/**
 * Parses regions. Also duh
 *
 * Created by molenzwiebel on 21-12-15.
 */
class RegionParser(val regionManager: RegionManager) {
    private final val KNOWN_TAGS = arrayOf("region", "rectangle", "cuboid", "circle", "cylinder", "sphere", "block", "union", "intersect", "negative")

    /**
     * Parses the specified element and returns the region. Throws if invalid.
     */
    public fun parse(el: Element): RRegion {
        val region = parseRegionTag(el)
        if (el.name == "region") return region

        if (el.getAttributeValue("name") != null) {
            regionManager.add(el.getAttributeValue("name"), region)
        } else {
            regionManager.add(region)
        }

        return region
    }

    /**
     * Parses all region children of the specified element. Throws if invalid.
     */
    public fun parseChildren(el: Element): List<RRegion> {
        return el.children.filter { it.name in KNOWN_TAGS }.map { parse(it) }
    }

    private fun parseRegionTag(el: Element): RRegion {
        return when (el.name) {
            "region" -> regionManager.get(el["name"]!!) ?: throw IllegalArgumentException("Undefined region ${el["name"]}")
            "rectangle" -> RectangleRegion(el["min"].parseVector2D(), el["max"].parseVector2D())
            "cuboid" -> CuboidRegion(el["min"].parseVector3D(), el.getAttributeValue("min").parseVector3D())
            "circle" -> CircleRegion(el["base"].parseVector2D().x, el["base"].parseVector2D().z, el["radius"]?.toInt() ?: diagnoseMissing(el, "radius", 0))
            "cylinder" -> CylinderRegion(el["base"].parseVector3D(), el["radius"]?.toInt() ?: diagnoseMissing(el, "radius", 0), el["height"]?.toInt() ?: diagnoseMissing(el, "height", 0))
            "sphere" -> SphereRegion(el["center"].parseVector3D(), el["radius"]?.toInt() ?: diagnoseMissing(el, "radius", 0))
            "block" -> BlockRegion(el["location"].parseVector3D())
            "union" -> UnionRegion(parseChildren(el))
            "intersect" -> IntersectRegion(parseChildren(el))
            "negative" -> InvertedRegion(UnionRegion(parseChildren(el)))
            else -> throw IllegalArgumentException("${el.name} is not a valid region tag name.")
        }
    }

    private fun <T> diagnoseMissing(el: Element, attr: String, retval: T): T {
        //FIXME: Make better
        println("Warning: missing attribute ${attr} in ${el.name} tag")
        return retval
    }
}