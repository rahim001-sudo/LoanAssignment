package com.khana.loans.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.khana.loans.databinding.ItemLoansBinding
import com.khana.loans.models.LoansResponse

class LoansAdapter(private val lstLoans:List<LoansResponse>) : RecyclerView.Adapter<LoansAdapter.LoanHolder>() {
    class LoanHolder(val binding: ItemLoansBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanHolder {
        val binding = ItemLoansBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return LoanHolder(binding)
    }

    override fun getItemCount(): Int {
        return lstLoans.size
    }

    override fun onBindViewHolder(holder: LoanHolder, position: Int) {
        holder.binding.loan = lstLoans[position]
    }
}