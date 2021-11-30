package sk.stuba.fei.mobv.cryptowallet.ui.fragment.authentication

import android.app.AlertDialog
import sk.stuba.fei.mobv.cryptowallet.MainActivity
import sk.stuba.fei.mobv.cryptowallet.R

class LoadingDialog(private val mainActivity: MainActivity) {
    private lateinit var loadingDialog: AlertDialog

    fun startLoading() {
        val dialogView = mainActivity.layoutInflater.inflate(R.layout.loading_dialog, null)
        loadingDialog =
            AlertDialog.Builder(mainActivity, R.style.WrapContentDialog).setView(dialogView)
                .setCancelable(false).create()
        loadingDialog.show()
    }

    fun dismissLoading() {
        loadingDialog.dismiss()
    }
}