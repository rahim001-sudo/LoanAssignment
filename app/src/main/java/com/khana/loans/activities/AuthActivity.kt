package com.khana.loans.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.khana.loans.utils.AppPref
import com.khana.loans.R
import com.khana.loans.databinding.ActivitySplashBinding
import com.khana.loans.utils.getUser

class AuthActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        decideNavigation()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
        }
        setClickListeners()
    }

    private fun init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
    }

    private fun decideNavigation() {
        if (getUser() != null) {
            finish()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setClickListeners() {
        binding.btnSignIn.setOnClickListener(this)
        binding.btnSignUp.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnSignIn -> {
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
            }

            R.id.btnSignUp -> {
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }
        }
    }
}