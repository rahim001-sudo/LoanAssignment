package com.khana.loans.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.khana.loans.R
import javax.inject.Inject


class LoadingDialog(val context: Context) {

    private var dialog: AlertDialog? = null

    init {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        builder.setView(inflater.inflate(R.layout.item_loading_dialog, null))
        builder.setCancelable(false)
        dialog = builder.create()
    }

    @SuppressLint("InflateParams")
    fun show() {
        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}