package com.example.imilipocket.ui.transaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.imilipocket.data.Transaction
import com.example.imilipocket.databinding.ItemTransactionBinding
import com.example.imilipocket.util.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(
    private val onEditClick: (Transaction) -> Unit,
    private val onDeleteClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding, onEditClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TransactionViewHolder(
        private val binding: ItemTransactionBinding,
        private val onEditClick: (Transaction) -> Unit,
        private val onDeleteClick: (Transaction) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        fun bind(transaction: Transaction) {
            binding.apply {
                tvTitle.text = transaction.title
                tvAmount.text = CurrencyFormatter.formatAmount(root.context, transaction.amount)
                tvCategory.text = transaction.category
                tvDate.text = dateFormat.format(Date(transaction.date))
                
                // Set color based on transaction type
                val colorRes = if (transaction.type == Transaction.Type.INCOME) {
                    android.R.color.holo_green_dark
                } else {
                    android.R.color.holo_red_dark
                }
                tvAmount.setTextColor(root.context.getColor(colorRes))
                viewTypeIndicator.setBackgroundColor(root.context.getColor(colorRes))

                // Set click listeners
                btnEdit.setOnClickListener { onEditClick(transaction) }
                btnDelete.setOnClickListener { onDeleteClick(transaction) }
            }
        }
    }

    private class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
} 