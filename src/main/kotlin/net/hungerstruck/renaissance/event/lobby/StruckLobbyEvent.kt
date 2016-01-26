package net.hungerstruck.renaissance.event.lobby

import net.hungerstruck.renaissance.event.StruckEvent
import net.hungerstruck.renaissance.lobby.RLobby

abstract class StruckLobbyEvent(val lobby: RLobby) : StruckEvent()