package sk.stuba.fei.mobv.cryptowallet.ui.fragment.authentication

import android.os.Bundle
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
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentAccountActiveLoginBinding
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.repository.BalanceRepository
import sk.stuba.fei.mobv.cryptowallet.viewmodel.authentication.AuthenticationViewModel
import sk.stuba.fei.mobv.cryptowallet.viewmodel.authentication.AuthenticationViewModelFactory

class LoginActiveFragment : Fragment() {

    private var _binding: FragmentAccountActiveLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var authenticationViewModel: AuthenticationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAccountActiveLoginBinding.inflate(inflater, container, false)
        val application = requireNotNull(this.activity).application
        val database = AppDatabase.getDatabase(application)
        authenticationViewModel = ViewModelProvider(
            this,
            AuthenticationViewModelFactory(
                AccountRepository(database.accountDao(), RemoteDataSource.getStellarApi()),
                BalanceRepository(database.balanceDao())
            )
        )[AuthenticationViewModel::class.java]

        binding.viewModel = authenticationViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        authenticationViewModel.pinError.observe(viewLifecycleOwner, {
            if(it.message.isNotEmpty()){
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
            }
        })

        authenticationViewModel.loginResult.observe(viewLifecycleOwner, {
            if (it.equals(AuthenticationViewModel.LoginState.SUCCESSFUL)) {
                findNavController().navigate(R.id.action_global_homeFragment)
            } else {
                Toast.makeText(requireContext(), "Unable to login", Toast.LENGTH_LONG)
                    .show()
            }
        })

        authenticationViewModel.signedOut.observe(viewLifecycleOwner, {
            findNavController().navigate(R.id.action_global_loginFragment)
        })

        return binding.root
    }
}