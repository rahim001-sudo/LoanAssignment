package com.khana.loans.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.khana.loans.services.DataUploaderService
import com.khana.loans.MyApplication
import com.khana.loans.R
import com.khana.loans.api.NetworkState
import com.khana.loans.databinding.ActivityProfileBinding
import com.khana.loans.models.ResponseData
import com.khana.loans.models.User
import com.khana.loans.utils.AppPref
import com.khana.loans.utils.DATA_TO_UPLOAD
import com.khana.loans.utils.IMAGE_DATA
import com.khana.loans.utils.IMAGE_LIST
import com.khana.loans.utils.LoadingDialog
import com.khana.loans.utils.PROFILE_DATA
import com.khana.loans.viewmodels.ViewModelFactory
import com.khana.loans.viewmodels.ProfileUpdateModel


class ProfileActivity : BaseActivity(), View.OnClickListener {
    private lateinit var profileModel: ProfileUpdateModel
    private lateinit var binding: ActivityProfileBinding
    private var user: User = User()
    private lateinit var loadingDialog: LoadingDialog

    private val imageSelectionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { data ->
        data.data?.let {
            processData(it)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setObservers()
        setClickListener()
    }

    private fun setClickListener() {
        binding.btnUploadImages.setOnClickListener(this)
        binding.btnUploadUserData.setOnClickListener(this)
        binding.btnLogOut.setOnClickListener(this)
    }

    private fun setObservers() {
        profileModel.result.observe(this) {
            binding.btnUpdate.apply {
                isClickable = it
                isEnabled = it
            }
        }
        profileModel.isLoading.observe(this) {
            if (it) {
                loadingDialog.show()
            } else {
                loadingDialog.dismiss()
            }
        }

        profileModel.userUpdated.observe(this) {
            when (it) {
                is NetworkState.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

                is NetworkState.Success<*> -> {
                    Toast.makeText(this, (it.data as ResponseData).message, Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
            }
        }
    }

    private fun init() {
        loadingDialog = LoadingDialog(this)

        profileModel = ViewModelProvider(
            this,
            ViewModelFactory((application as MyApplication))
        )[ProfileUpdateModel::class]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        binding.user = user
        binding.lifecycleOwner = this
        binding.model = profileModel
    }

    private fun processData(data: Intent) {
        data.clipData?.let {
            val lstImageUri = ArrayList<String>()
            val count: Int = data.clipData!!.itemCount
            for (i in 0 until count) {
                val imageUri: Uri = data.clipData!!.getItemAt(i).getUri()
                lstImageUri.add(imageUri.toString())
            }
            val intent = Intent(this, DataUploaderService::class.java)
            intent.putExtra(DATA_TO_UPLOAD, IMAGE_DATA)
            intent.putStringArrayListExtra(IMAGE_LIST, lstImageUri)
            Toast.makeText(this, getString(R.string.image_uploading_started), Toast.LENGTH_SHORT)
                .show()

            startService(intent)
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnUploadImages -> {
                launchImageIntent()
            }
            R.id.btnLogOut -> {
                AppPref.getInstance().clear()
                val intent = Intent(this, AuthActivity::class.java)
                finishAffinity()
                startActivity(intent)
            }
            R.id.btnUploadUserData ->{
                Toast.makeText(this, "Data Uploading Started", Toast.LENGTH_SHORT).show()
                startService(Intent(this, DataUploaderService::class.java).apply {
                    putExtra(DATA_TO_UPLOAD, PROFILE_DATA)
                })
            }
        }
    }

    private fun launchImageIntent() {
        val intent = Intent()
        intent.setType("image/*")
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.setAction(Intent.ACTION_GET_CONTENT)
        imageSelectionLauncher.launch(intent)
    }


}