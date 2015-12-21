package net.hungerstruck.renaissance.modules.region

import net.hungerstruck.renaissance.util.ContextStore

/**
 * Manages regions. Duh
 *
 * Created by molenzwiebel on 21-12-15.
 */
class RegionManager : ContextStore<RRegion>() {
    val regionParser = RegionParser(this)
}