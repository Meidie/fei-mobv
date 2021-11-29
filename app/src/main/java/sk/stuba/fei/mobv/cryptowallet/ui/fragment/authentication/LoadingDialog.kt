package sk.stuba.fei.mobv.cryptowallet.ui.fragment.authentication

import android.app.AlertDialog
import sk.stuba.fei.mobv.cryptowallet.MainActivity
import sk.stuba.fei.mobv.cryptowallet.R

class LoadingDialog(private val mainActivity: MainActivity) {
    private lateinit var isDialog: AlertDialog

    fun startLoading() {
        val dialogView = mainActivity.layoutInflater.inflate(R.layout.loading_dialog, null)
        isDialog =
            AlertDialog.Builder(mainActivity).setView(dialogView).setCancelable(false).create()
        isDialog.show()
    }

    fun isDismiss() {
        isDialog.dismiss()
    }
}