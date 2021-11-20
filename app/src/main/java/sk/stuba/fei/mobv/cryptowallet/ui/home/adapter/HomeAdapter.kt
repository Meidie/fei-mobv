package sk.stuba.fei.mobv.cryptowallet.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sk.stuba.fei.mobv.cryptowallet.R

class HomeAdapter(private val balanceList: List<String>) : RecyclerView.Adapter<HomeAdapter.BalanceRowViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BalanceRowViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.balance_row,
        parent, false)

        return BalanceRowViewHolder(itemView)
    }

    //TODO natiahnut balance z uctu
    override fun onBindViewHolder(holder: BalanceRowViewHolder, position: Int) {
        val currentItem = balanceList[position]

        holder.textViewBalance.text = currentItem
    }

    override fun getItemCount() = balanceList.size

    class BalanceRowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textViewBalance: TextView = itemView.findViewById(R.id.balance)
        val textViewCurrencyName: TextView = itemView.findViewById(R.id.currency_name)
        val textViewCurrency: TextView = itemView.findViewById(R.id.currency)
    }


}