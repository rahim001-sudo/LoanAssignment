package com.khana.loans.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.khana.loans.utils.AppPref
import com.google.gson.Gson
import com.khana.loans.MyApplication
import com.khana.loans.R
import com.khana.loans.api.NetworkState
import com.khana.loans.databinding.ActivitySignInBinding
import com.khana.loans.models.User
import com.khana.loans.utils.LoadingDialog
import com.khana.loans.viewmodels.AuthViewModel
import com.khana.loans.viewmodels.ViewModelFactory


class SignInActivity : BaseActivity() {
    lateinit var binding: ActivitySignInBinding
    private lateinit var viewModel: AuthViewModel
    private val user = User()

    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setObservers()
    }

    private fun init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory((application as MyApplication))
        )[AuthViewModel::class]
        binding.user = user
        binding.authmodel = viewModel
        loadingDialog = LoadingDialog(this)
    }

    private fun setObservers() {
        viewModel.isLoading.observe(this) {
            if (it) {
                loadingDialog.show()
            } else {
                loadingDialog.dismiss()
            }
        }
        viewModel.userSignIn.observe(this) { state ->
            when (state) {
                is NetworkState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }

                is NetworkState.Success<*> -> {
                    AppPref.getInstance()
                        .setValue(AppPref.USER_DATA, Gson().toJson(user).toString())
                    finishAffinity()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }


}