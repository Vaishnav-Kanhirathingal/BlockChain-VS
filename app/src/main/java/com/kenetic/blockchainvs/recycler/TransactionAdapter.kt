package com.kenetic.blockchainvs.recycler

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kenetic.blockchainvs.app_viewmodel.MainViewModel
import com.kenetic.blockchainvs.databinding.TransactionListItemBinding
import com.kenetic.blockchainvs.datapack.database.TransactionData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService

class TransactionAdapter(
    private val viewModel: MainViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val context: Context
) :
    ListAdapter<Int, TransactionAdapter.PartyViewHolder>(diffCallback) {

    class PartyViewHolder(
        private val itemBinding: TransactionListItemBinding,
        private val viewModel: MainViewModel
    ) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(currentTransactionData: TransactionData, context: Context) {
            itemBinding.apply {
                transactionHashTextView.text = currentTransactionData.transactionHash
                copyImageButton.setOnClickListener {
                    val clipboardManager =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText(
                        "Transaction Hash",
                        currentTransactionData.transactionHash
                    )
                    clipboardManager.setPrimaryClip(clipData)
                    Toast.makeText(context, "Transaction Hash Copied", Toast.LENGTH_SHORT).show()
                }
                openInBrowser.setOnClickListener {
                    val i = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://ropsten.etherscan.io/tx/${currentTransactionData.transactionHash}")
                    )
                    startActivity(context, i, null)
                }
                methodCalledTextView.text = currentTransactionData.methodCalled
                if (currentTransactionData.gasFee == null) {
                    gasFeeTextView.text = viewModel.gettingGasUsed
                    CoroutineScope(Dispatchers.IO).launch {
                        val web3j =
                            Web3j.build(HttpService("https://ropsten.infura.io/v3/c358089e1aaa4746aa50e61d4ec41c5c"))
                        val cost = web3j
                            .ethGetTransactionReceipt(currentTransactionData.transactionHash)
                            .send()
                            .transactionReceipt
                            .get()
                            .gasUsed
                        currentTransactionData.gasFee = cost.toLong()
                        viewModel.updateTransaction(currentTransactionData)
                    }
                } else {
                    gasFeeTextView.text = currentTransactionData.gasFee!!.toString()
                }
                transactionTimeTextView.text = currentTransactionData.transactionTime
            }
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Int>() {
            override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartyViewHolder {
        return PartyViewHolder(
            TransactionListItemBinding.inflate(LayoutInflater.from(parent.context)),
            viewModel
        )
    }

    override fun onBindViewHolder(holder: PartyViewHolder, position: Int) {
        viewModel.getById(getItem(position)).asLiveData().observe(lifecycleOwner) {
            holder.bind(it, context)
        }
    }
}

