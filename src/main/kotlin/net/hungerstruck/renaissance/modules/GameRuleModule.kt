package net.hungerstruck.renaissance.modules

import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import net.hungerstruck.renaissance.xml.toBool
import org.jdom2.Document

/**
 * Created by molenzwiebel on 21-12-15.
 */
class GameRuleModule(match: RMatch, document: Document, modCtx: RModuleContext) : RModule(match, document, modCtx) {
    val rules: Map<String, Boolean>

    init {
        val el = document.rootElement.getChild("gamerules")

        if (el != null) {
            this.rules = el.children.toMap({ it.name }, { it.textNormalize.toBool(defaultValue = true) })
        } else {
            this.rules = mapOf()
        }
    }
}