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
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentAccountImportBinding
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.repository.BalanceRepository
import sk.stuba.fei.mobv.cryptowallet.viewmodel.authentication.AccountViewModelFactory
import sk.stuba.fei.mobv.cryptowallet.viewmodel.authentication.AuthenticationViewModel


class ImportAccountFragment : Fragment() {

    private var _binding: FragmentAccountImportBinding? = null
    private val binding get() = _binding!!
    private lateinit var authenticationViewModel: AuthenticationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAccountImportBinding.inflate(inflater, container, false)
        val application = requireNotNull(this.activity).application
        val database = AppDatabase.getDatabase(application)
        authenticationViewModel = ViewModelProvider(
            this,
            AccountViewModelFactory(
                AccountRepository(database.accountDao(), RemoteDataSource.getStellarApi()),
                BalanceRepository(database.balanceDao())
            )
        )[AuthenticationViewModel::class.java]

        binding.viewmodel = authenticationViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_importAccount_to_loginFragment)
        }

        return binding.root
    }
}