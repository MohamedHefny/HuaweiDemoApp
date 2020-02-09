package com.mohamedhefny.huawei.utils

import android.app.Activity
import android.view.View
import android.widget.Toast

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