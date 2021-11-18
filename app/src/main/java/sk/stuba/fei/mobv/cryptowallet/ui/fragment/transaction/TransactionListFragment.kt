package sk.stuba.fei.mobv.cryptowallet.ui.fragment.transaction

import androidx.fragment.app.Fragment
import sk.stuba.fei.mobv.cryptowallet.R

class TransactionListFragment: Fragment(R.layout.fragment_transaction_list) {

//    private var _binding: FragmentTransactionListBinding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var transactionViewModel: TransactionViewModel
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//
//        _binding = FragmentTransactionListBinding.inflate(inflater, container, false)
//
//        val application = requireNotNull(this.activity).application
//        val database = AppDatabase.getDatabase(application)
//        transactionViewModel = ViewModelProvider(
//            this,
//            TransactionViewModelFactory(TransactionRepository(database.transactionDao()))
//        ).get(TransactionViewModel::class.java)
//
//        val adapter = TransactionListAdapter()
//        binding.transactionListRecycleView.adapter = adapter
//        transactionViewModel.allContacts.observe(viewLifecycleOwner, {
//            it?.let {
//                adapter.submitList(it.sortedBy { t -> t.transactionId })
//            }
//        })
//
//        return binding.root
//    }
//
//
//    override fun onDestroy() {
//        super.onDestroy()
//        _binding = null
//    }

}