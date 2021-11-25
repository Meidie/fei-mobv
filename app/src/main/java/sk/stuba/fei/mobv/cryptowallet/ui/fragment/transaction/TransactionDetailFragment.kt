package sk.stuba.fei.mobv.cryptowallet.ui.fragment.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import sk.stuba.fei.mobv.cryptowallet.R
import sk.stuba.fei.mobv.cryptowallet.database.entity.Transaction
import sk.stuba.fei.mobv.cryptowallet.database.entity.TransactionType
import sk.stuba.fei.mobv.cryptowallet.databinding.FragmentTransactionDetailBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class TransactionDetailFragment : Fragment() {

    private val args: TransactionDetailFragmentArgs by navArgs()
    private lateinit var transaction: Transaction

    private var _binding: FragmentTransactionDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTransactionDetailBinding.inflate(inflater, container, false)

        transaction = args.currentTransaction
        binding.publicKey.setText(transaction.publicKey)
        binding.amount.setText(transaction.amount)
        binding.date.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy")))
        binding.image.setBackgroundResource(if (transaction.type.name.equals(TransactionType.CREDIT.name))
            R.drawable.ic_baseline_trending_up_green_24 else R.drawable.ic_baseline_trending_down_red_24)

        return binding.root
    }
}