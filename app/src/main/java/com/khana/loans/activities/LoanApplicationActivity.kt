package com.khana.loans.activities

import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.khana.loans.MyApplication
import com.khana.loans.R
import com.khana.loans.api.NetworkState
import com.khana.loans.databinding.ActivityLoansBinding
import com.khana.loans.models.Loans
import com.khana.loans.utils.LoadingDialog
import com.khana.loans.viewmodels.ViewModelFactory
import com.khana.loans.viewmodels.LoanViewModel

class LoanApplicationActivity : BaseActivity() {

    private var loans: Loans = Loans()
    private lateinit var binding: ActivityLoansBinding
    private lateinit var viewModel: LoanViewModel
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_loans)
        init()
        setLoanApplicationObserver()
    }


    private fun init() {
        binding.loan = loans
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(application as MyApplication)
        )[LoanViewModel::class]
        binding.loanmodel = viewModel
        loadingDialog = LoadingDialog(this)
        binding.lifecycleOwner = this
    }

    private fun setLoanApplicationObserver() {
        viewModel.loanData.observe(this) { state ->
            when (state) {
                is NetworkState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }

                is NetworkState.Success<*> -> {
                    Toast.makeText(
                        this,
                        getString(R.string.your_loan_application_has_been_submitted),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }

        viewModel.isLoading.observe(this) {
            it?.let {
                if (it) {
                    loadingDialog.show()
                } else {
                    loadingDialog.dismiss()
                }
            }
        }
    }
}