package com.khana.loans.utils

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.khana.loans.R

@BindingAdapter("app:loanBackground")
fun setSrcCompatBasedOnValue(textView: TextView, value: String) {
    val drawableRes = when (value) {
        "Rejected" -> R.drawable.drawable_rejected
        "Accepted" -> R.drawable.accept
        else -> R.drawable.drawable_pending
    }
    textView.background = ContextCompat.getDrawable(textView.context, drawableRes)
}