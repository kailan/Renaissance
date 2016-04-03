package net.hungerstruck.renaissance.event.player

import net.hungerstruck.renaissance.RPlayer

/**
 * Created by teddy on 30/03/2016.
 */
class RPlayerThirstUpdateEvent(player: RPlayer, val thirst: Int) : RPlayerEvent(player)
