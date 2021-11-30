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
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResultListener
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
import sk.stuba.fei.mobv.cryptowallet.security.Crypto
import sk.stuba.fei.mobv.cryptowallet.viewmodel.account.AccountViewModel
import sk.stuba.fei.mobv.cryptowallet.viewmodel.account.AccountViewModelFactory
import sk.stuba.fei.mobv.cryptowallet.viewmodel.contact.ContactViewModel
import sk.stuba.fei.mobv.cryptowallet.viewmodel.contact.ContactViewModelFactory

class AccountDetailFragment : Fragment() {

    private lateinit var accountViewModel: AccountViewModel
    private var _binding: FragmentAccountDetailBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

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
            val pk = accountViewModel.publicKey.get()
            if (!pk.isNullOrEmpty()) {
                val clipboardManager =
                    requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("public key", pk)
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(
                    requireContext(),
                    "Public key copied to clipboard",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.privateKeyLayout.setEndIconOnClickListener {
            val dialog = AccountDialogFragment()
            dialog.show(parentFragmentManager, "AccountDialogFragment")
        }

        binding.SignOutButton.setOnClickListener {
            accountViewModel.signOut()
        }

        accountViewModel.signedOut.observe(viewLifecycleOwner, {
            findNavController().navigate(R.id.action_global_loginFragment)
        })

        setFragmentResultListener("confirm") { _, bundle ->
            bundle.getString("pin")?.let {
                setPrivateKey(it)
            }
        }
        return binding.root
    }

    private fun setPrivateKey(pin: String) {
        val acc = accountViewModel.activeAccount
        val crypto = Crypto()
        val validPin = crypto.secretKeyGenerator.generateSecretKey(
            pin,acc.pinData.salt).encoded.contentEquals(acc.pinData.pin)

        if (validPin) {
            acc.privateKeyData?.let {
                val pk = crypto.decrypt(it, pin)

                binding.privateKeyLayout.setEndIconDrawable(R.drawable.ic_content_copy)
                binding.privateKeyText.inputType =
                    (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE)

                binding.privateKeyLayout.setEndIconOnClickListener {
                    val clipboardManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("private key", pk)
                    clipboardManager.setPrimaryClip(clipData)
                    Toast.makeText(requireContext(),"Private key copied to clipboard",Toast.LENGTH_LONG).show()
                }

                binding.privateKeyText.setText(pk)
            }
        } else {
            Toast.makeText(requireContext(),"Invalid PIN",Toast.LENGTH_LONG).show()
        }
    }
}