package sk.stuba.fei.mobv.cryptowallet.ui.fragment.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import sk.stuba.fei.mobv.cryptowallet.R
import sk.stuba.fei.mobv.cryptowallet.api.RemoteDataSource
import sk.stuba.fei.mobv.cryptowallet.database.AppDatabase
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentAccountPrintKeypairBinding
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.repository.BalanceRepository
import sk.stuba.fei.mobv.cryptowallet.viewmodel.authentication.AuthenticationViewModel
import sk.stuba.fei.mobv.cryptowallet.viewmodel.authentication.AuthenticationViewModelFactory

class PrintKeyPairFragment : Fragment() {

    private lateinit var binding: FragmentAccountPrintKeypairBinding
    private lateinit var authenticationViewModel: AuthenticationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account_print_keypair , container, false)

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


        binding.continuebutton.setOnClickListener @Suppress("UNUSED_ANONYMOUS_PARAMETER")
        { view: View ->
            view.findNavController().navigate(R.id.action_global_homeFragment)
        }
        return binding.root
    }
}