package com.mohamedhefny.huawei.utils

import android.app.Activity
import android.widget.Toast

fun Activity.showToast(messageRes: Int, toastDuration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, messageRes, toastDuration).show()
}

fun Activity.showToast(message: String, toastDuration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, toastDuration).show()
}