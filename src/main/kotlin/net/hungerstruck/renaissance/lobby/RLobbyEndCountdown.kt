package net.hungerstruck.renaissance.lobby

import net.hungerstruck.renaissance.config.RConfig
import net.hungerstruck.renaissance.countdown.Countdown

/**
 * Lobby end countdown.
 *
 * Created by molenzwiebel on 01-01-16.
 */
class RLobbyEndCountdown(val lobby: RLobby) : Countdown() {
    override fun onTick(timeLeft: Int) {
        val status = RConfig.Lobby.tickMessage.format(timeLeft)

        if (timeLeft % 10 == 0 || timeLeft <= 5)
            lobby.sendMessage(status)
    }

    override fun onFinish() {
        lobby.end()
    }
}