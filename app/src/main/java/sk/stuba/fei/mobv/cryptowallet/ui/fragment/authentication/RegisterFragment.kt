package sk.stuba.fei.mobv.cryptowallet.ui.fragment.authentication


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import sk.stuba.fei.mobv.cryptowallet.MainActivity
import sk.stuba.fei.mobv.cryptowallet.R
import sk.stuba.fei.mobv.cryptowallet.api.RemoteDataSource
import sk.stuba.fei.mobv.cryptowallet.database.AppDatabase
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentAccountRegisterBinding
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.repository.BalanceRepository
import sk.stuba.fei.mobv.cryptowallet.viewmodel.authentication.AuthenticationViewModel
import sk.stuba.fei.mobv.cryptowallet.viewmodel.authentication.AuthenticationViewModelFactory

class RegisterFragment : Fragment() {

    private lateinit var authenticationViewModel: AuthenticationViewModel
    private var _binding: FragmentAccountRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val loadingDialog = LoadingDialog(activity as MainActivity)

        _binding = FragmentAccountRegisterBinding.inflate(inflater, container, false)
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

        binding.existButton.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_importAccount)
        }

        authenticationViewModel.loadingResponse.observe(viewLifecycleOwner, {
            loadingDialog.startLoading()
        })

        authenticationViewModel.accountRegistrationResponse.observe(viewLifecycleOwner, {
            loadingDialog.isDismiss()
            if (it.isSuccessful) {
                Log.d("Account Created", it?.isSuccessful.toString())
                findNavController().navigate(R.id.action_registerFragment_to_generatePair)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Account not created" + it?.isSuccessful.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        return binding.root
    }
}