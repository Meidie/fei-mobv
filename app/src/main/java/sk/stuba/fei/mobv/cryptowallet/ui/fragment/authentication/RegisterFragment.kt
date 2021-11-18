package sk.stuba.fei.mobv.cryptowallet.ui.fragment.authentication

import org.stellar.sdk.KeyPair;
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import sk.stuba.fei.mobv.cryptowallet.R
import sk.stuba.fei.mobv.cryptowallet.database.AppDatabase
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentRegisterBinding
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.viewmodel.account.AccountViewModel
import sk.stuba.fei.mobv.cryptowallet.viewmodel.account.AccountViewModelFactory
import org.stellar.sdk.Server;
import org.stellar.sdk.responses.AccountResponse;
import sk.stuba.fei.mobv.cryptowallet.database.entity.Account


class RegisterFragment : Fragment() {
    private lateinit var accountViewModel: AccountViewModel
    private  var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        val application = requireNotNull(this.activity).application
        val database = AppDatabase.getDatabase(application)
        accountViewModel = ViewModelProvider(
            this,
            AccountViewModelFactory(AccountRepository(database.AccountDao()))
        ).get(AccountViewModel::class.java)


        binding.registerbutton.setOnClickListener{
            createAccount()
        }

        binding.loginbutton.setOnClickListener{
            findNavController().navigate(R.id.action_register_to_login)
        }

        return binding.root
    }

    private fun createAccount() {
        val pin = binding.pin.text.toString()
        if(areInputsValid(pin)) {
            val pair: KeyPair = KeyPair.random()
            val queue = Volley.newRequestQueue(this.activity)
            val url = String.format("https://friendbot.stellar.org/?addr=%s", pair.accountId)

            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    val server = Server("https://horizon-testnet.stellar.org")
                    val accountResponse: AccountResponse = server.accounts().account(pair.accountId)
                    val account = Account(0, pair.accountId, pin + pair.secretSeed, accountResponse.balances.get(0).getBalance())
                    accountViewModel.insert(account)

                    Toast.makeText(requireContext(), "Successfully logged", Toast.LENGTH_SHORT)
                        .show()
                },
                {
                    Log.e("CREATE_ACCOUNT", " That didn't work!")
                })

            queue.add(stringRequest)
        } else {
            Toast.makeText(requireContext(), "Pin must contains 4 digits", Toast.LENGTH_LONG)
                .show()
        }
    }


    private fun areInputsValid(pin: String): Boolean {
        return !(TextUtils.isEmpty(pin.toString()) && pin.length == 4)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}