package sk.stuba.fei.mobv.cryptowallet.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sk.stuba.fei.mobv.cryptowallet.R
import sk.stuba.fei.mobv.cryptowallet.database.entity.Contact

//TODO cez databinding
class ContactListAdapter : RecyclerView.Adapter<ContactListAdapter.ContactRowViewHolder>() {

    var contactList = emptyList<Contact>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = contactList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactRowViewHolder {
        return ContactRowViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ContactRowViewHolder, position: Int) {
        val currentItem = contactList[position]
        holder.bind(currentItem)
    }

    class ContactRowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val rowCount: TextView = itemView.findViewById(R.id.rowCount)
        private val firstname: TextView = itemView.findViewById(R.id.rowFirstName)
        private val lastName: TextView = itemView.findViewById(R.id.rowLastName)

        fun bind(item: Contact) {
            rowCount.text = item.id.toString()
            firstname.text = item.fistName
            lastName.text = item.lastName
        }

        companion object {
            fun create(parent: ViewGroup): ContactRowViewHolder {
                return ContactRowViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.contact_row, parent, false)
                )
            }
        }
    }
}