package sk.stuba.fei.mobv.cryptowallet.ui.fragment.authentication


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import sk.stuba.fei.mobv.cryptowallet.MainActivity
import sk.stuba.fei.mobv.cryptowallet.R
import sk.stuba.fei.mobv.cryptowallet.api.RemoteDataSource
import sk.stuba.fei.mobv.cryptowallet.database.AppDatabase
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentAccountRegisterBinding
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.repository.BalanceRepository
import sk.stuba.fei.mobv.cryptowallet.ui.LoadingDialog
import sk.stuba.fei.mobv.cryptowallet.viewmodel.account.AccountViewModel
import sk.stuba.fei.mobv.cryptowallet.viewmodel.account.AccountViewModelFactory

class RegisterFragment : Fragment() {
    private lateinit var accountViewModel: AccountViewModel
    private lateinit var binding: FragmentAccountRegisterBinding



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val loadingDialog = LoadingDialog(activity as MainActivity)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account_register,container,  false)

        val application = requireNotNull(this.activity).application
        val database = AppDatabase.getDatabase(application)
        accountViewModel = ViewModelProvider(
            this,
            AccountViewModelFactory(
                AccountRepository(database.accountDao(), RemoteDataSource.getStellarApi()),
                BalanceRepository(database.balanceDao())
            )
        )[AccountViewModel::class.java]

        binding.viewmodel = accountViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.loginbutton.setOnClickListener @Suppress("UNUSED_ANONYMOUS_PARAMETER")
        { view: View ->
            view.findNavController().navigate(R.id.action_register_to_login)
        }

        accountViewModel.loadingResponse.observe(viewLifecycleOwner, {
            loadingDialog.startLoading()
        })

        accountViewModel.accountRegistrationResponse.observe(viewLifecycleOwner, {
            loadingDialog.isDismiss()
            if (it.isSuccessful) {
                Log.d("Account Created", it?.isSuccessful.toString())
                findNavController().navigate(R.id.action_registerFragment_to_generatePair)
            } else {
                Toast.makeText(requireContext(),"Account not created" + it?.isSuccessful.toString(), Toast.LENGTH_SHORT).show()
            }
        })

        return binding.root
    }


}