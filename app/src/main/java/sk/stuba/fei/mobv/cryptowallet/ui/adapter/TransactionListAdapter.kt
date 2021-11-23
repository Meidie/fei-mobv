package sk.stuba.fei.mobv.cryptowallet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sk.stuba.fei.mobv.cryptowallet.database.entity.Transaction
import sk.stuba.fei.mobv.cryptowallet.databinding.TransactionRowBinding

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
            binding.count.text = currentItem.transactionId.toString()
            binding.recipient.text = currentItem.publicKey
            binding.amount.text = currentItem.amount.toString()
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