package net.hungerstruck.renaissance.countdown

import net.hungerstruck.renaissance.Renaissance
import org.bukkit.scheduler.BukkitRunnable

/**
 * Created by molenzwiebel on 01-01-16.
 */
class CountdownWrapper(val countdown: Countdown) : BukkitRunnable() {
    var timeLeft: Int = -1

    fun start(seconds: Int) {
        assert(seconds > 0, { "Countdown has to have positive time." })
        assert(timeLeft == -1, { "Countdown already started." })

        this.timeLeft = seconds

        countdown.onStart(seconds)
        // Run every 20 ticks, assuming the server runs on 20 TPS.
        this.runTaskTimer(Renaissance.plugin!!, 0, 20)
    }

    override fun cancel() {
        countdown.onCancel()
        super.cancel()
    }

    override fun run() {
        if (timeLeft == 0) {
            countdown.onFinish()
            // Cancel the countdown. **NOTE**: This calls super.cancel(), not this.cancel().
            super.cancel()
        } else {
            countdown.onTick(timeLeft--)
        }
    }
}