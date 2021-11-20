package sk.stuba.fei.mobv.cryptowallet.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.stellar.sdk.Server
import org.stellar.sdk.responses.AccountResponse
import sk.stuba.fei.mobv.cryptowallet.database.AppDatabase
import sk.stuba.fei.mobv.cryptowallet.database.entity.Account
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentHomeBinding
import sk.stuba.fei.mobv.cryptowallet.repository.AccountRepository
import sk.stuba.fei.mobv.cryptowallet.viewmodel.account.AccountViewModel
import sk.stuba.fei.mobv.cryptowallet.viewmodel.account.AccountViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var accountViewModel: AccountViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val application = requireNotNull(this.activity).application
        val database = AppDatabase.getDatabase(application)
        accountViewModel = ViewModelProvider(
            this,
            AccountViewModelFactory(AccountRepository(database.AccountDao()))
        ).get(AccountViewModel::class.java)


//        val queue = Volley.newRequestQueue(this.activity)
//        val id = "GBOWAUCRCZPPI5WE6RAMRNHILHVZAI6SHIFTXRKDECIKK4273H755I37"
//        val url = String.format("https://horizon-testnet.stellar.org/accounts/%s", id)
//
//        val stringRequest = StringRequest(
//            Request.Method.GET, url,
//            { response ->
//                val server = Server("https://horizon-testnet.stellar.org")
//                val accountResponse: AccountResponse = server.accounts().account(id)
//                val balance = accountResponse.balances.get(0).getBalance()
//                binding.balance = balance.toString()
//            },
//            {
//                Log.e("CREATE_ACCOUNT", " That didn't work!")
//            })
//
//        queue.add(stringRequest)
//

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}