package sk.stuba.fei.mobv.cryptowallet.ui.fragment.transaction

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import sk.stuba.fei.mobv.cryptowallet.R
import sk.stuba.fei.mobv.cryptowallet.api.RemoteDataSource
import sk.stuba.fei.mobv.cryptowallet.api.StellarApi
import sk.stuba.fei.mobv.cryptowallet.database.AppDatabase
import sk.stuba.fei.mobv.cryptowallet.database.entity.Contact
import sk.stuba.fei.mobv.cryptowallet.database.entity.Transaction
import sk.stuba.fei.mobv.cryptowallet.database.entity.TransactionType
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentAddTransactionBinding
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.repository.ContactRepository
import sk.stuba.fei.mobv.cryptowallet.repository.TransactionRepository
import sk.stuba.fei.mobv.cryptowallet.security.Crypto
import sk.stuba.fei.mobv.cryptowallet.viewmodel.TransactionViewModelFactory
import sk.stuba.fei.mobv.cryptowallet.viewmodel.contact.ContactViewModel
import sk.stuba.fei.mobv.cryptowallet.viewmodel.contact.ContactViewModelFactory
import sk.stuba.fei.mobv.cryptowallet.viewmodel.transaction.TransactionViewModel
import java.util.ArrayList

class AddTransactionFragment : Fragment() {

    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var contactViewModel: ContactViewModel
    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    private var contactsList: List<Contact> = emptyList()
    private var selectedContact: Contact? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)

        val application = requireNotNull(this.activity).application
        val database = AppDatabase.getDatabase(application)

        contactViewModel = ViewModelProvider(
            this,
            ContactViewModelFactory(
                ContactRepository(database.contactDao()),
                AccountRepository(database.accountDao(), RemoteDataSource.getStellarApi())
            )
        )[ContactViewModel::class.java]

        transactionViewModel = ViewModelProvider(
            this,
            TransactionViewModelFactory(TransactionRepository(database.transactionDao()), StellarApi(), AccountRepository(database.accountDao(), StellarApi()))
        )[TransactionViewModel::class.java]

        val contactsString: MutableList<String> = ArrayList()
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, contactsString)

        binding.contactSelect.setAdapter(arrayAdapter)

        contactViewModel.allContacts.observe(viewLifecycleOwner, { contacts ->
            contactsList = contacts
            contactsString.add("")
            for ((_, _, name) in contacts) {
                contactsString.add(name)
            }
            arrayAdapter.notifyDataSetChanged()
        })

        binding.contactSelect.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, i, l ->
                if(TextUtils.isEmpty(binding.contactSelect.text)){
                    binding.publicKey.isEnabled = true
                    selectedContact = null
                    binding.publicKey.setText("")
                } else {
                    binding.publicKey.isEnabled = false
                    selectedContact = contactsList.filter { it.name.equals(binding.contactSelect.text.toString()) }.first()
                    binding.publicKey.setText(selectedContact?.publicKey)
                }

            }

        publicKeyTextChanged()
        amountTextChanged()
        pinTextChanged()

        binding.sendButton.setOnClickListener {
            insertTransactionToDatabase()
        }

        transactionViewModel.newTransactionResponse.observe(viewLifecycleOwner, {
            if(it != null){
                Toast.makeText(requireContext(), "Transaction successfully added", Toast.LENGTH_SHORT).show()

                val action = AddTransactionFragmentDirections.actionAddTransactionFragmentToTransactionListFragment()
                findNavController().navigate(action)
            } else {
                Toast.makeText(requireContext(), "ERROR!", Toast.LENGTH_SHORT).show()
            }
        })

        return binding.root
    }

    private fun insertTransactionToDatabase() {
        val publicKey = binding.publicKey.text
        val amount = binding.amount.text
        val pin = binding.pin.text

        if (isInputValid(publicKey, amount, pin)) {
            if(pinIsValid(pin)){

                val newTransaction = Transaction(0L, 0L, amount.toString(), TransactionType.DEBET, if (selectedContact != null) selectedContact!!.publicKey else publicKey.toString(), "")
                transactionViewModel.sendTransaction(newTransaction, pin.toString())

            } else {
                Toast.makeText(requireContext(), "INVALID PIN", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Please fill required fields", Toast.LENGTH_LONG)
                .show()
        }
    }


    private fun pinTextChanged() {
        binding.pin.doOnTextChanged { text, _, _, _ ->
            if (TextUtils.isEmpty(text)) {
                binding.pinLayout.error = "Amount is required"
            } else {
                binding.pinLayout.error = null
            }
        }
    }

    private fun amountTextChanged() {
        binding.amount.doOnTextChanged { text, _, _, _ ->
            if(TextUtils.isEmpty(text)){
                binding.amountLayout.error = "Amount is required"
            } else if (text!!.startsWith("-")) {
                binding.amount.setText("")
                binding.amountLayout.error = "Negative amount not allowed!"
            } else if (text.contains(" ")) {
                binding.amount.setText(text.replace(" ".toRegex(), ""))
            } else if (text.contains(",")) {
                binding.amountLayout.error = ", is not allowed use ."
            } else {
                binding.amountLayout.error = null
            }

            // TODO doplniť možnosť zadať maximálne current balance
        }
    }

    private fun publicKeyTextChanged() {
        binding.publicKey.doOnTextChanged { text, _, _, _ ->
            if (TextUtils.isEmpty(text)) {
                binding.publicKeyLayout.error = "Public key is required"
            } else if(!text!!.startsWith("G")){
                binding.publicKeyLayout.error = "Key must start with 'G' character"
            } else if (text.length > 56) {
                binding.publicKeyLayout.error = "Key must contain 56 alphanumeric characters"
            } else {
                binding.publicKeyLayout.error = null
            }
        }
    }

    private fun pinIsValid(pin: Editable?): Boolean {
        val account = transactionViewModel.account

        return Crypto().secretKeyGenerator.generateSecretKey(pin.toString(), account.pin.salt).encoded.contentEquals(
            (account.pin.pin)
        )
    }

    private fun isInputValid(publicKey: Editable?, amount: Editable?, pin: Editable?): Boolean {

        binding.publicKeyLayout.error = null
        binding.amountLayout.error = null
        binding.pinLayout.error = null

        var valid = true

        if (TextUtils.isEmpty(publicKey)) {
            valid = false
            binding.publicKeyLayout.error = "Public key is required"
        }
        if (TextUtils.isEmpty(amount)) {
            valid = false
            binding.amountLayout.error = "Amount is required"
        }
        if(TextUtils.isEmpty(pin)){
            valid = false
            binding.pinLayout.error = "Pin is required"
        }

        return valid
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}