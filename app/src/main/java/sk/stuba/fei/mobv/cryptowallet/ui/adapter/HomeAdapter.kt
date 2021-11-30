package sk.stuba.fei.mobv.cryptowallet.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import sk.stuba.fei.mobv.cryptowallet.database.entity.Balance
import sk.stuba.fei.mobv.cryptowallet.databinding.BalanceRowBinding


class HomeAdapter: ListAdapter<Balance, HomeAdapter.BalanceRowViewHolder>(
    BalanceDiffCallback()
) {

    class BalanceRowViewHolder(private val binding: BalanceRowBinding): RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun create(parent: ViewGroup): BalanceRowViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = BalanceRowBinding.inflate(layoutInflater, parent, false)
                return BalanceRowViewHolder(binding)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(currentItem: Balance) {
            binding.balance.text = currentItem.amount.dropLast(2)
            if (currentItem.currency == "native"){
                binding.currency.text = "XLM"
                binding.currencyName.text = "Stellar Lumen"
            }
            else{
                binding.currency.text = currentItem.currency
                binding.currencyName.text = currentItem.currency
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BalanceRowViewHolder {
        return BalanceRowViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: BalanceRowViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }
}

class BalanceDiffCallback: DiffUtil.ItemCallback<Balance>(){
    override fun areItemsTheSame(oldItem: Balance, newItem: Balance): Boolean {
        return oldItem.balanceId == newItem.balanceId
    }

    override fun areContentsTheSame(oldItem: Balance, newItem: Balance): Boolean {
        return oldItem == newItem
    }

}

