package sk.stuba.fei.mobv.cryptowallet.util

import android.view.View

fun View.visible(isVisible: Boolean){
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View.enable(enabled: Boolean){
    isEnabled = enabled
}