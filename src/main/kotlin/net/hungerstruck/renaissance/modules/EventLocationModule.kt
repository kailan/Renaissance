package net.hungerstruck.renaissance.modules

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import net.hungerstruck.renaissance.get
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import net.hungerstruck.renaissance.xml.parseVector3D
import org.bukkit.util.Vector
import org.jdom2.Document

/**
 * Created by molenzwiebel on 21-12-15.
 */
class EventLocationModule(match: RMatch, document: Document, modCtx: RModuleContext) : RModule(match, document, modCtx) {
    val locations: Multimap<Int, Vector>

    init {
        val el = document.rootElement.getChild("eventlocations")

        this.locations = ArrayListMultimap.create()
        if (el != null) {
            for (child in el.children) {
                locations.put(child["sector"]!!.toInt(), child.textNormalize.parseVector3D())
            }
        }
    }
}