package net.hungerstruck.renaissance.modules

import net.hungerstruck.renaissance.get
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.modules.region.RectangleRegion
import net.hungerstruck.renaissance.modules.region.RegionModule
import net.hungerstruck.renaissance.xml.module.Dependencies
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import net.hungerstruck.renaissance.xml.parseVector2D
import org.bukkit.util.Vector
import org.jdom2.Document

/**
 * Boundary module.
 *
 * Created by molenzwiebel on 21-12-15.
 */
@Dependencies(RegionModule::class)
class BoundaryModule(match: RMatch, document: Document, modCtx: RModuleContext) : RModule(match, document, modCtx) {
    val center: Vector
    val region: RectangleRegion

    init {
        val boundaryEl = document.rootElement.getChild("boundary") ?: throw IllegalArgumentException("Map ${match.map.mapInfo.name} has no boundary element")

        val region = modCtx.regionParser.parse(boundaryEl.children[0])
        if (region !is RectangleRegion)
            throw IllegalArgumentException("Expected boundary of ${match.map.mapInfo.name} to be a rectangle")

        this.center = boundaryEl["center"].parseVector2D()
        this.region = region
    }
}