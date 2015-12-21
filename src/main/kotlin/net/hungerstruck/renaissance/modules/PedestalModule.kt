package net.hungerstruck.renaissance.modules

import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.modules.region.BlockRegion
import net.hungerstruck.renaissance.modules.region.RegionModule
import net.hungerstruck.renaissance.xml.flatten
import net.hungerstruck.renaissance.xml.module.Dependencies
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import org.jdom2.Document

/**
 * Parses pedestals.
 *
 * Created by molenzwiebel on 21-12-15.
 */
@Dependencies(RegionModule::class)
class PedestalModule(match: RMatch, document: Document, modCtx: RModuleContext) : RModule(match, document, modCtx) {
    val pedestals: List<BlockRegion>

    init {
        pedestals = document.rootElement.flatten("pedestals", "pedestal").map {
            val parsed = modCtx.regionParser.parse(it.children[0])
            if (parsed !is BlockRegion) throw IllegalArgumentException("Pedestal must be BlockRegion")
            parsed as BlockRegion
        }

        print("Loaded ${pedestals.size} pedestals. Here they are: ${pedestals}")
    }
}