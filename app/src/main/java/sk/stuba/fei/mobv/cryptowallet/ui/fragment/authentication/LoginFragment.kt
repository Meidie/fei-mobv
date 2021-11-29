package sk.stuba.fei.mobv.cryptowallet.ui.fragment.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import sk.stuba.fei.mobv.cryptowallet.R
import sk.stuba.fei.mobv.cryptowallet.api.RemoteDataSource
import sk.stuba.fei.mobv.cryptowallet.database.AppDatabase
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentAccountLoginBinding
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.repository.BalanceRepository
import sk.stuba.fei.mobv.cryptowallet.viewmodel.authentication.AuthenticationViewModelFactory
import sk.stuba.fei.mobv.cryptowallet.viewmodel.authentication.AuthenticationViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentAccountLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var authenticationViewModel: AuthenticationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAccountLoginBinding.inflate(inflater, container, false)
        val application = requireNotNull(this.activity).application
        val database = AppDatabase.getDatabase(application)
        authenticationViewModel = ViewModelProvider(
            this,
            AuthenticationViewModelFactory(
                AccountRepository(database.accountDao(), RemoteDataSource.getStellarApi()),
                BalanceRepository(database.balanceDao())
            )
        )[AuthenticationViewModel::class.java]

        binding.viewmodel = authenticationViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // TODO nejako pockat na odpoved ak to ide
        authenticationViewModel.doesActiveAccountExist.observe(viewLifecycleOwner, {
            if(it){
                findNavController().navigate(R.id.action_global_loginActiveFragment)
            }
        })

        binding.registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        authenticationViewModel.account.observe(viewLifecycleOwner, {
            if (it != null) {
                findNavController().navigate(R.id.action_global_homeFragment)
            }
        })

        return binding.root
    }
}