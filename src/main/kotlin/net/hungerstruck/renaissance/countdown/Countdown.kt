package net.hungerstruck.renaissance.countdown

/**
 * Created by molenzwiebel on 01-01-16.
 */
abstract class Countdown {
    abstract fun onStart(timeLeft: Int)
    abstract fun onTick(timeLeft: Int)
    abstract fun onFinish()
    abstract fun onCancel()
}