package sk.stuba.fei.mobv.cryptowallet.ui.adapter.contact

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sk.stuba.fei.mobv.cryptowallet.database.entity.Contact
import sk.stuba.fei.mobv.cryptowallet.databinding.ContactRowBinding
import sk.stuba.fei.mobv.cryptowallet.ui.fragment.contact.ContactListFragmentDirections

class ContactListAdapter :
    ListAdapter<Contact, ContactListAdapter.ContactRowViewHolder>(ContactDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactRowViewHolder {
        return ContactRowViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ContactRowViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class ContactRowViewHolder(private val binding: ContactRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Contact) {
            binding.contact = item
            //TODO cez databinding?
            binding.contactRow.setOnClickListener {
                binding.root.findNavController().navigate(
                    ContactListFragmentDirections.actionContactListFragmentToContactEditFragment(
                        item
                    )
                )
            }
        }

        companion object {
            fun create(parent: ViewGroup): ContactRowViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ContactRowBinding.inflate(layoutInflater, parent, false)
                return ContactRowViewHolder(binding)
            }
        }
    }
}

class ContactDiffCallback : DiffUtil.ItemCallback<Contact>() {
    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem.contactId == newItem.contactId
    }

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem == newItem
    }
}

