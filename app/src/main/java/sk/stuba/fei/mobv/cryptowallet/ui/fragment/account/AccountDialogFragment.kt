package sk.stuba.fei.mobv.cryptowallet.ui.fragment.account

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.textfield.TextInputEditText
import sk.stuba.fei.mobv.cryptowallet.R

class AccountDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.account_dialog, null)

            builder.setView(view)
                .setPositiveButton("Confirm") { dialog, _ ->
                    val v = view.findViewById<TextInputEditText>(R.id.pin)
                    setFragmentResult("confirm", bundleOf("pin" to v.text.toString()))
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}