package net.hungerstruck.renaissance.modules

import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import org.bukkit.event.EventHandler
import org.bukkit.event.world.ChunkUnloadEvent

/***
 *
 *
 * STUBBED
 * needs to be reimplemented for twenty-twenty
 */

class ChunkLoadModule(match: RMatch, modCtx: RModuleContext) : RModule(match, modCtx) {
    override fun init() {
        registerEvents()
    }

    @EventHandler
    fun onChunkUnload(event: ChunkUnloadEvent) {
        //if(isMatch(event.chunk.world) && match.state != RMatch.State.PLAYING)
            //event.isCancelled = true
    }
}
