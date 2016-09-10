package net.hungerstruck.renaissance.modules

import net.hungerstruck.renaissance.event.match.RMatchCountdownTickEvent
import net.hungerstruck.renaissance.event.match.RMatchEndEvent
import net.hungerstruck.renaissance.event.match.RMatchLoadEvent
import net.hungerstruck.renaissance.event.match.RMatchStartEvent
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.modules.region.*
import net.hungerstruck.renaissance.xml.builder.inject
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import org.bukkit.event.EventHandler
import org.bukkit.util.NumberConversions
import org.bukkit.util.Vector

class SpecCallbackModule(match: RMatch, modCtx: RModuleContext) : RModule(match, modCtx) {
    @inject var onMatchLoad: (RMatch, RModuleContext) -> Unit = {x,y ->}
    @inject var onMatchStart: (RMatch, RModuleContext) -> Unit = {x,y ->}
    @inject var onMatchCountdownTick: (RMatch, RModuleContext) -> Unit = {x,y ->}
    @inject var onMatchEnd: (RMatch, RModuleContext) -> Unit = {x,y ->}

    override fun init() {
        registerEvents()
    }

    @EventHandler
    fun onMatchStartEvent(event: RMatchStartEvent) {
        if (!isMatch(event.match)) return
        onMatchStart(match, moduleContext)
    }

    @EventHandler
    fun onMatchStartLoadEvent(event: RMatchLoadEvent) {
        if (!isMatch(event.match)) return
        onMatchLoad(match, moduleContext)
    }

    @EventHandler
    fun onMatchCountdownTick(event: RMatchCountdownTickEvent) {
        if (!isMatch(event.match)) return
        onMatchCountdownTick(match, moduleContext)
    }

    @EventHandler
    fun onMatchEnd(event: RMatchEndEvent) {
        if (!isMatch(event.match)) return
        onMatchEnd(match, moduleContext)
    }
}

