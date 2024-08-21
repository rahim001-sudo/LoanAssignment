package com.khana.loans.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.khana.loans.services.DataUploaderService
import com.khana.loans.R
import com.khana.loans.databinding.ActivityMainBinding
import com.khana.loans.utils.DATA_TO_UPLOAD
import com.khana.loans.utils.PROFILE_DATA


class MainActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        init()
        uploadDataToServer()
    }

    private fun uploadDataToServer() {
        /**
         *  commenting this line knowingly. So that user can test the data uploading part again and again
         */
//        if (AppPref.getInstance().getValue(AppPref.DATA_UPLOADED, false)) {
//            startService(Intent(this, DataUploaderService::class.java).apply {
//                putExtra(DATA_TO_UPLOAD, PROFILE_DATA)
//            })
//        }
        startService(Intent(this, DataUploaderService::class.java).apply {
            putExtra(DATA_TO_UPLOAD, PROFILE_DATA)
        })
    }


    private fun init() {

        setClickListener()


    }

    private fun setClickListener() {
        binding.icLoans.setOnClickListener(this)
        binding.icProfile.setOnClickListener(this)
        binding.btnApplyLoan.setOnClickListener(this)
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnApplyLoan -> {
                val intent = Intent(this, LoanApplicationActivity::class.java)
                startActivity(intent)
            }

            R.id.icProfile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }

            R.id.icLoans -> {
                val intent = Intent(this, LoanHistoryActivity::class.java)
                startActivity(intent)
            }
        }
    }

}