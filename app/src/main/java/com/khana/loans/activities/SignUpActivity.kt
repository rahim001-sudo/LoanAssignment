package com.khana.loans.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.khana.loans.utils.AppPref
import com.google.gson.Gson
import com.khana.loans.MyApplication
import com.khana.loans.R
import com.khana.loans.api.NetworkState
import com.khana.loans.databinding.ActivitySignUpBinding
import com.khana.loans.databinding.PermssionDialogBinding
import com.khana.loans.models.User
import com.khana.loans.utils.LoadingDialog
import com.khana.loans.viewmodels.AuthViewModel
import com.khana.loans.viewmodels.ViewModelFactory

class SignUpActivity : BaseActivity() {

    private val user: User = User()
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var loadingDialog: LoadingDialog
    private val readSms = Manifest.permission.READ_SMS
    private val readCalls = Manifest.permission.READ_CALL_LOG
    private val readContacts = Manifest.permission.READ_CONTACTS
    private val readImages =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    private val permissions = mutableListOf(readContacts, readSms, readCalls)
    private var permissionDialogBinding: PermssionDialogBinding? = null
    private var permissionDialog: AlertDialog? = null


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { data ->
        removeGrantedPermissions()
        if (permissions.isNotEmpty()) {
            if (permissionDialog?.isShowing == false) {
                permissionDialog?.show()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        init()
    }

    private fun init() {
        viewModel = ViewModelProvider(this, ViewModelFactory(application as MyApplication))[AuthViewModel::class]
        loadingDialog = LoadingDialog(this)
        binding.user = user
        binding.authmodel = viewModel
        permissions.add(readImages)
        initBuilder()
        removeGrantedPermissions()
        if (permissions.isNotEmpty()) {
            requestPermissions(permissions.toTypedArray(), 101)
        }
        loadingListener()
        authStateListener()
    }


    private fun initBuilder() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        permissionDialogBinding = PermssionDialogBinding.inflate(layoutInflater)
        builder.setView(permissionDialogBinding!!.root)
        builder.setCancelable(false)
        permissionDialog = builder.create()

        permissionDialogBinding?.tvAllow?.setOnClickListener {
            permissionDialog?.dismiss()
            askPermission()
        }
    }

    private fun removeGrantedPermissions() {
        val it = permissions.iterator()
        while (it.hasNext()) {
            val permission = it.next()

            if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                it.remove()
                when (permission) {
                    readSms -> permissionDialogBinding?.ivReadSms?.setImageResource(R.drawable.accept)
                    readContacts -> permissionDialogBinding?.ivReadContactsPermission?.setImageResource(R.drawable.accept)
                    readImages -> permissionDialogBinding?.ivReadPhotosPermission?.setImageResource(R.drawable.accept)
                    readCalls -> permissionDialogBinding?.ivReadLogsPermission?.setImageResource(R.drawable.accept)
                }
            }
        }
    }


    private fun askPermission() {
        for (permission in permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(permission)) {
                    val intent: Intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.setData(uri)
                    requestPermissionLauncher.launch(intent)
                    return
                } else {
                    requestPermissions(arrayOf(permission), 101)
                    return
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        removeGrantedPermissions()
        if (this.permissions.isNotEmpty()) {
            if (permissionDialog?.isShowing == false) {
                permissionDialog?.show()
            }
        }
    }

    private fun authStateListener() {
        viewModel.userSignUp.observe(this) { state ->
            when (state) {
                is NetworkState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }

                is NetworkState.Success<*> -> {
                    AppPref.getInstance()
                        .setValue(AppPref.USER_DATA, Gson().toJson(user).toString())
                    finish()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun loadingListener() {
        viewModel.isLoading.observe(this) {
            if (it) {
                loadingDialog.show()
            } else {
                loadingDialog.dismiss()
            }
        }
    }

}