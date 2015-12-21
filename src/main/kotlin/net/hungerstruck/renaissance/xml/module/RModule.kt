package net.hungerstruck.renaissance.xml.module

import net.hungerstruck.renaissance.match.RMatch
import org.bukkit.event.Listener
import org.jdom2.Document

/**
 * Base class for modules.
 *
 * Created by molenzwiebel on 21-12-15.
 */
abstract class RModule : Listener {
    final val match: RMatch
    final val document: Document
    final var moduleContext: RModuleContext

    constructor(match: RMatch, document: Document, modCtx: RModuleContext) {
        this.match = match
        this.document = document
        this.moduleContext = modCtx
    }

    protected inline fun <reified T : RModule> getModule(): T? {
        return moduleContext.getModule<T>()
    }
}