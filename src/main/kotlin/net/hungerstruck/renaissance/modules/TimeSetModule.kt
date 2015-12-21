package net.hungerstruck.renaissance.modules

import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import net.hungerstruck.renaissance.xml.toLong
import org.jdom2.Document

/**
 * Created by molenzwiebel on 21-12-15.
 */
class TimeSetModule(match: RMatch, document: Document, modCtx: RModuleContext) : RModule(match, document, modCtx) {
    val value: Long

    init {
        value = document.rootElement?.getChild("timestart")?.textNormalize.toLong()
    }
}