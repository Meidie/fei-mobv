package sk.stuba.fei.mobv.cryptowallet.ui.fragment.authentication

import android.app.AlertDialog
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import sk.stuba.fei.mobv.cryptowallet.R

class RegistrationDetailDialogFragment : DialogFragment() {

    companion object {
        const val PUBLIC_KEY_TAG = "public_key"
        const val PRIVATE_KEY_TAG = "private_key"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.registration_dialog, null)

            arguments?.let { arg ->

                    view.findViewById<TextInputEditText>(R.id.publicKeyText)
                        .setText(arg.getString(PUBLIC_KEY_TAG))
                    view.findViewById<TextInputEditText>(R.id.privateKeyText)
                        .setText(arg.getString(PRIVATE_KEY_TAG))
            }

            view.findViewById<TextInputLayout>(R.id.publicKeyLayout).setEndIconOnClickListener {
                view.findViewById<TextInputEditText>(R.id.publicKeyText)?.let { pk ->
                    val clipboardManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("public key", pk.text)
                    clipboardManager.setPrimaryClip(clipData)
                    Toast.makeText(requireContext(),"Public key copied to clipboard",
                        Toast.LENGTH_SHORT).show()
                }
            }

            view.findViewById<TextInputLayout>(R.id.privateKeyLayout).setEndIconOnClickListener {
                    view.findViewById<TextInputEditText>(R.id.privateKeyText)?.let { pk ->
                        val clipboardManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clipData = ClipData.newPlainText("public key", pk.text)
                        clipboardManager.setPrimaryClip(clipData)
                        Toast.makeText(requireContext(), "Private key copied to clipboard",
                            Toast.LENGTH_SHORT).show()
                    }
                }

            builder.setView(view)
                .setPositiveButton("Continue") { dialog, _ ->
                    dialog.dismiss()
                    findNavController().navigate(R.id.action_global_homeFragment)
                }

            builder.setTitle(R.string.please_save_your_private_and_public_keys)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}