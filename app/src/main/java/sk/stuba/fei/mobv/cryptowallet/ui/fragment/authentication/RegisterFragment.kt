package sk.stuba.fei.mobv.cryptowallet.ui.fragment.authentication


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
import sk.stuba.fei.mobv.cryptowallet.R
import sk.stuba.fei.mobv.cryptowallet.api.RemoteDataSource
import sk.stuba.fei.mobv.cryptowallet.database.AppDatabase
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentRegisterBinding
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.repository.BalanceRepository
import sk.stuba.fei.mobv.cryptowallet.viewmodel.account.AccountViewModel
import sk.stuba.fei.mobv.cryptowallet.viewmodel.account.AccountViewModelFactory

class RegisterFragment : Fragment() {
    private lateinit var accountViewModel: AccountViewModel
    private var _binding: FragmentRegisterBinding? = null
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
            AccountViewModelFactory(
                AccountRepository(database.accountDao(), RemoteDataSource.getStellarApi()),
                BalanceRepository(database.balanceDao())
            )
        )[AccountViewModel::class.java]

        binding.viewModel = accountViewModel
        binding.registerbutton.setOnClickListener {
            createAccount()
        }

        // TODO asi by to malo byt cez Data Bindingu
        binding.loginbutton.setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }

        accountViewModel.accountRegistrationResponse.observe(viewLifecycleOwner, {

            if (it.isSuccessful) {
                Log.d("Account Created", it?.isSuccessful.toString())
                findNavController().navigate(R.id.action_global_homeFragment)
            } else {
                Log.d("Account not created", it?.isSuccessful.toString())
            }
        })

        return binding.root
    }

    // TODO asi by to malo byt cele vo ViewModel s pouzitim Data Bindingu
    private fun createAccount() {
        val pin = binding.pin.text.toString()

        if (areInputsValid(pin)) {
            accountViewModel.createAccount(pin)
        } else {
            Toast.makeText(requireContext(), "Data are not filled correctly", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun areInputsValid(pin: String): Boolean {
        return !TextUtils.isEmpty(pin) && pin.length == 4
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}