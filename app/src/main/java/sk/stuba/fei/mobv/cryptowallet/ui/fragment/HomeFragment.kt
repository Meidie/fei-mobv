package sk.stuba.fei.mobv.cryptowallet.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import sk.stuba.fei.mobv.cryptowallet.api.RemoteDataSource
import sk.stuba.fei.mobv.cryptowallet.database.AppDatabase
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentHomeBinding
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.repository.BalanceRepository
import sk.stuba.fei.mobv.cryptowallet.viewmodel.authentication.AuthenticationViewModel
import sk.stuba.fei.mobv.cryptowallet.viewmodel.authentication.AccountViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

//    private lateinit var accountViewModel: AuthenticationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val application = requireNotNull(this.activity).application
        val database = AppDatabase.getDatabase(application)
//        accountViewModel = ViewModelProvider(
//            this,
//            AccountViewModelFactory(
//                AccountRepository(database.accountDao(), RemoteDataSource.getStellarApi()),
//                BalanceRepository(database.balanceDao())
//            )
//        )[AuthenticationViewModel::class.java]

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}