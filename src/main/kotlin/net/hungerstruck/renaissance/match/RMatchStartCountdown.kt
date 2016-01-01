package net.hungerstruck.renaissance.match

import net.hungerstruck.renaissance.config.RConfig
import net.hungerstruck.renaissance.countdown.Countdown

/**
 * Created by molenzwiebel on 01-01-16.
 */
class RMatchStartCountdown(val match: RMatch) : Countdown() {
    override fun onTick(timeLeft: Int) {
        val status = RConfig.Match.tickMessage.format(timeLeft)

        if (timeLeft % 10 == 0 || timeLeft <= 5)
            match.sendMessage(status)
    }

    override fun onFinish() {
        match.startMatch()
    }
}