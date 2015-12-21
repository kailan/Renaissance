package net.hungerstruck.renaissance.modules

import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import net.hungerstruck.renaissance.xml.toInt
import org.jdom2.Document

/**
 * Created by molenzwiebel on 21-12-15.
 */
class SanityModule(match: RMatch, document: Document, modCtx: RModuleContext) : RModule(match, document, modCtx) {
    val airHeight: Int

    val naturalLightLevel: Int
    val artificialLightLevel: Int
    val overallLightLevel: Int

    init {
        val el = document.rootElement.getChild("sanity")
        if (el == null) {
            this.airHeight = 150
            this.naturalLightLevel = 11
            this.artificialLightLevel = 11
            this.overallLightLevel = 11
        } else {
            this.airHeight = el.getChild("heightSettings")?.getChildTextNormalize("airHeight").toInt(defaultValue = 150)

            this.naturalLightLevel = el.getChild("lightSettings")?.getChildTextNormalize("naturalLightLevel").toInt(defaultValue = 11)
            this.artificialLightLevel = el.getChild("lightSettings")?.getChildTextNormalize("artificialLightLevel").toInt(defaultValue = 11)
            this.overallLightLevel = el.getChild("lightSettings")?.getChildTextNormalize("overallLightLevel").toInt(defaultValue = 11)
        }
    }
}