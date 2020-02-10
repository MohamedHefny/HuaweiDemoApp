package com.mohamedhefny.huawei.utils

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.widget.Toast
import com.mohamedhefny.huawei.R

private var loadingDialog: Dialog? = null

fun Activity.showLoading(): Dialog {
    loadingDialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar).apply {
        setContentView(layoutInflater.inflate(R.layout.layout_loading, null))
        setCancelable(false)
        show()
    }
    return loadingDialog!!
}

fun Activity.hideLoading() {
    loadingDialog?.dismiss()
}

fun Activity.showToast(messageRes: Int, toastDuration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, messageRes, toastDuration).show()
}

fun Activity.showToast(message: String, toastDuration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, toastDuration).show()
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}