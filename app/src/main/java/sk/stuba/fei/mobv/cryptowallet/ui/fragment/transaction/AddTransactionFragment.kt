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
import sk.stuba.fei.mobv.cryptowallet.database.AppDatabase
import sk.stuba.fei.mobv.cryptowallet.database.entity.Contact
import sk.stuba.fei.mobv.cryptowallet.database.entity.Transaction
import sk.stuba.fei.mobv.cryptowallet.database.entity.TransactionType
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentAddTransactionBinding
import sk.stuba.fei.mobv.cryptowallet.repository.ContactRepository
import sk.stuba.fei.mobv.cryptowallet.repository.TransactionRepository
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)

        val application = requireNotNull(this.activity).application
        val database = AppDatabase.getDatabase(application)
        contactViewModel = ViewModelProvider(
            this,
            ContactViewModelFactory(ContactRepository(database.contactDao()))
        ).get(ContactViewModel::class.java)

        transactionViewModel = ViewModelProvider(
            this,
            TransactionViewModelFactory(TransactionRepository(database.transactionDao()))
        ).get(TransactionViewModel::class.java)

        val contactsString: MutableList<String> = ArrayList()
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, contactsString)

        binding.contactSelect.setAdapter(arrayAdapter)

        contactViewModel.allContacts.observe(viewLifecycleOwner, { contacts ->
            contactsList = contacts
            for ((_, name) in contacts) {
                contactsString.add(name)
            }
            arrayAdapter.notifyDataSetChanged()
        })

        binding.contactSelect.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            binding.publicKey.setText(contactsList.filter { it.name.equals(binding.contactSelect.text.toString()) }.first().publicKey)
        }

        binding.contactSelect.doOnTextChanged { text, _, _, _ ->
            Log.i("SELECTED", text.toString())
            if(TextUtils.isEmpty(text)){
                binding.recipientLayout.error = "Recipient is required"
            } else {
                binding.recipientLayout.error = null
            }
        }

        binding.amount.doOnTextChanged { text, _, _, _ ->
            Log.i("AMOUNT", text!!.toString())
            if(text.startsWith("-")){
                binding.amount.setText("")
                binding.amountLayout.error = "Negative amount not allowed!"
            } else {
                binding.amountLayout.error = null
            }

            // TODO doplniť možnosť zadať maximálne current balance
        }

        binding.sendButton.setOnClickListener {
            insertTransactionToDatabase()
        }

        return binding.root
    }

    private fun insertTransactionToDatabase() {
        val recipient = binding.contactSelect.text
        val amount = binding.amount.text

        if (isInputValid(recipient, amount)) {
            val contact = contactsList.filter { it.name.equals(binding.contactSelect.text.toString()) }.first()
            val newTransaction = Transaction(0L, contact.contactId, amount.toString().toDouble(), TransactionType.CREDIT, contact.publicKey)
            transactionViewModel.insert(newTransaction)
            // TODO volanie stellar api
            Toast.makeText(requireContext(), "Transaction successfully added", Toast.LENGTH_SHORT).show()

            val action = AddTransactionFragmentDirections.actionAddTransactionFragmentToTransactionListFragment()
            findNavController().navigate(action)

        } else {
            Toast.makeText(requireContext(), "Please fill reqiured fields", Toast.LENGTH_LONG).show()
        }
    }

    private fun isInputValid(recipient: Editable, amount: Editable?): Boolean {

        binding.recipientLayout.error = null
        binding.amountLayout.error = null

        var valid = true

        if (TextUtils.isEmpty(recipient)){
            valid = false
            binding.recipientLayout.error = "Recipient is required"
        }

        if(TextUtils.isEmpty(amount)){
            valid = false
            binding.amountLayout.error = "Amount is required"
        }

        // TODO doplnit max

        return valid
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}