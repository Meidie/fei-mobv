package sk.stuba.fei.mobv.cryptowallet.ui.fragment.account

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import sk.stuba.fei.mobv.cryptowallet.R
import sk.stuba.fei.mobv.cryptowallet.api.RemoteDataSource
import sk.stuba.fei.mobv.cryptowallet.database.AppDatabase
import sk.stuba.fei.mobv.cryptowallet.databinding.AccountDialogBinding
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentAccountDetailBinding
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentContactEditBinding
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.repository.ContactRepository
import sk.stuba.fei.mobv.cryptowallet.viewmodel.account.AccountViewModel
import sk.stuba.fei.mobv.cryptowallet.viewmodel.account.AccountViewModelFactory
import sk.stuba.fei.mobv.cryptowallet.viewmodel.contact.ContactViewModel
import sk.stuba.fei.mobv.cryptowallet.viewmodel.contact.ContactViewModelFactory

class AccountDetailFragment : Fragment() {

    private lateinit var accountViewModel: AccountViewModel
    private var _binding: FragmentAccountDetailBinding? = null
    private val binding get() = _binding!!

    private var _dialogBinding: AccountDialogBinding? = null
    private val dialogBinding get() = _dialogBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _dialogBinding = AccountDialogBinding.inflate(inflater, container, false)
        _binding = FragmentAccountDetailBinding.inflate(inflater, container, false)
        val application = requireNotNull(this.activity).application
        val database = AppDatabase.getDatabase(application)
        accountViewModel = ViewModelProvider(
            this,
            AccountViewModelFactory(
                AccountRepository(database.accountDao(), RemoteDataSource.getStellarApi())
            )
        )[AccountViewModel::class.java]

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = accountViewModel

        setHasOptionsMenu(true)

        binding.publicKeyLayout.setEndIconOnClickListener {
            val pk =  accountViewModel.publicKey.get()
            if(!pk.isNullOrEmpty()){
                val clipboardManager =
                    requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("public key", pk)
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(requireContext(), "Public key copied to clipboard", Toast.LENGTH_LONG).show()
            }
        }

        binding.privateKeyLayout.setEndIconOnClickListener {

            showAccountDialog()
            //TODO pin
//            binding.privateKeyLayout.setEndIconDrawable(R.drawable.ic_content_copy)
//            binding.privateKeyText.inputType =
//                (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
//
//            binding.privateKeyLayout.setEndIconOnClickListener {
//                val pk =  accountViewModel.privateKey.get()
//                if(!pk.isNullOrEmpty()){
//                    val clipboardManager =
//                        requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//                    val clipData = ClipData.newPlainText("private key", pk)
//                    clipboardManager.setPrimaryClip(clipData)
//                    Toast.makeText(requireContext(), "Private key copied to clipboard", Toast.LENGTH_LONG).show()
//                }
//            }
        }

        binding.SignOutButton.setOnClickListener {
            accountViewModel.signOut()
        }

        accountViewModel.signedOut.observe(viewLifecycleOwner, {
            findNavController().navigate(R.id.action_global_loginFragment)
        })

        return binding.root
    }

    private fun showAccountDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.account_dialog)
        dialog.show()
    }
}