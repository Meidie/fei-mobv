package sk.stuba.fei.mobv.cryptowallet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sk.stuba.fei.mobv.cryptowallet.R
import sk.stuba.fei.mobv.cryptowallet.database.entity.Transaction
import sk.stuba.fei.mobv.cryptowallet.database.entity.TransactionType
import sk.stuba.fei.mobv.cryptowallet.databinding.TransactionRowBinding
import sk.stuba.fei.mobv.cryptowallet.ui.fragment.transaction.TransactionListFragmentDirections

class TransactionListAdapter: ListAdapter<Transaction, TransactionListAdapter.TransactionRowViewHolder>(TransactionDiffCallback()) {

    class TransactionRowViewHolder(private val binding: TransactionRowBinding): RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun create(parent: ViewGroup): TransactionRowViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TransactionRowBinding.inflate(layoutInflater, parent, false)
                return TransactionRowViewHolder(binding)
            }
        }

        fun bind(currentItem: Transaction) {
            binding.image.setBackgroundResource(if (currentItem.type.name.equals(TransactionType.CREDIT.name))
                R.drawable.ic_baseline_trending_up_green_24 else R.drawable.ic_baseline_trending_down_red_24)
            binding.recipient.text = currentItem.publicKey
            binding.amount.text = currentItem.amount

            binding.row.setOnClickListener{
                binding.root.findNavController().navigate(
                    TransactionListFragmentDirections.actionTransactionListFragmentToTransactionDetailFragment(currentItem)
                )
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionRowViewHolder {
        return TransactionRowViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TransactionRowViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }
}

class TransactionDiffCallback: DiffUtil.ItemCallback<Transaction>(){
    override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem.transactionId == newItem.transactionId
    }

    override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem == newItem
    }

}