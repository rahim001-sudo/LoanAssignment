package com.khana.loans.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.khana.loans.MyApplication
import com.khana.loans.R
import com.khana.loans.adapters.LoansAdapter
import com.khana.loans.api.NetworkState
import com.khana.loans.databinding.ActivityLoanHistoryBinding
import com.khana.loans.models.Loans
import com.khana.loans.models.LoansResponse
import com.khana.loans.models.ResponseData
import com.khana.loans.utils.LoadingDialog
import com.khana.loans.viewmodels.ViewModelFactory
import com.khana.loans.viewmodels.LoanViewModel

class LoanHistoryActivity : BaseActivity() {

    private lateinit var loansModel: LoanViewModel
    lateinit var binding: ActivityLoanHistoryBinding
    private val lst: ArrayList<LoansResponse> = ArrayList()
    private lateinit var adapter: LoansAdapter
    private lateinit var loadingDialog: LoadingDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setListener()
        setLoanDataObserver()
    }

    private fun setListener() {
        binding.srlLoans.setOnRefreshListener {
            loansModel.loanHistory()
        }
    }


    private fun init() {
        loansModel = ViewModelProvider(
            this,
            ViewModelFactory((application as MyApplication))
        ).get(LoanViewModel::class)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_loan_history)
        loadingDialog = LoadingDialog(this)
        adapter = LoansAdapter(lst)
        binding.rvLoansData.adapter = adapter
    }

    private fun setLoanDataObserver() {
        loansModel.appliedLoans.observe(this) {
            binding.srlLoans.isRefreshing = false
            when (it) {
                is NetworkState.Error -> {
                    if (lst.isEmpty()) {
                        binding.tvLoansApplied.text = it.message
                        binding.tvLoansApplied.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }

                }

                is NetworkState.Success<*> -> {
                    val data = (it.data as ResponseData)
                    data.loans.let {
                        lst.clear()
                        lst.addAll(it)
                        adapter.notifyDataSetChanged()
                        if (lst.isEmpty()) {
                            binding.tvLoansApplied.text = getString(R.string.no_loans_applied)
                            binding.tvLoansApplied.visibility = View.VISIBLE
                        } else {
                            binding.tvLoansApplied.visibility = View.GONE
                        }
                    }
                }
            }
        }
        loansModel.isLoading.observe(this) {
            it?.let {
                if (it) {
                    loadingDialog.show()
                } else {
                    loadingDialog.dismiss()
                }
            }
        }
        loansModel.loanHistory()
    }
}