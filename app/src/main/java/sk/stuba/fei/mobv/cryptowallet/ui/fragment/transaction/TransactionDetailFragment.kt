package sk.stuba.fei.mobv.cryptowallet.ui.fragment.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import sk.stuba.fei.mobv.cryptowallet.R
import sk.stuba.fei.mobv.cryptowallet.database.entity.TransactionAndContact
import sk.stuba.fei.mobv.cryptowallet.database.entity.TransactionType
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentTransactionDetailBinding

class TransactionDetailFragment : Fragment() {

    private val args: TransactionDetailFragmentArgs by navArgs()
    private lateinit var transaction: TransactionAndContact

    private var _binding: FragmentTransactionDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTransactionDetailBinding.inflate(inflater, container, false)

        transaction = args.currentTransaction
        binding.publicKey.setText(transaction.transaction.publicKey)
        binding.amount.setText(transaction.transaction.amount)
        binding.date.setText(transaction.transaction.dateTime)
        binding.contact.setText(if (transaction.contact.name != "") transaction.contact.name else "Contact not saved")
        binding.image.setBackgroundResource(if (transaction.transaction.type.name.equals(TransactionType.CREDIT.name))
            R.drawable.ic_baseline_trending_up_green_24 else R.drawable.ic_baseline_trending_down_red_24)

        return binding.root
    }
}