package sk.stuba.fei.mobv.cryptowallet.util

class OneTimeEvent {
    private var received = false

    fun receive(): Boolean {
        if (!received) {
            received = true
            return true
        }
        return false
    }
}