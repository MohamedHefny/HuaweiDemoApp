package com.mohamedhefny.huawei.ui.home

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.huawei.hms.iap.entity.ProductInfo
import com.mohamedhefny.huawei.utils.PaymentHelper

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val paymentHelper: PaymentHelper by lazy { PaymentHelper(application) }

    var canPlayVideo: Boolean = false
        private set

    /**
     * Use PaymentHelper to get the product for parches.
     */
    fun getAvailableProducts(): LiveData<List<ProductInfo>> =
        paymentHelper.loadProducts()

    /**
     * Call this method to get payment status wither success or failed.
     */
    fun getPaymentStatus(): LiveData<Boolean> = paymentHelper.isPaymentSuccess

    /**
     * Call this method to get different errors during the payment follow.
     */
    fun getErrorObservable(): LiveData<Int> = paymentHelper.paymentError

    /**
     * Pay for the selected product.
     * @param callbackActivity indicates the activity object that initiates a request.
     * @param productId ID list of products to be queried. Each product ID must exist and be unique in the current app.
     */
    fun goToPay(callbackActivity: Activity, productId: String) {
        paymentHelper.goToPay(callbackActivity, productId)
    }

    /***
     * Send data to the PaymentHelper class to parse it and determine
     * if the process success or field.
     * @param data returned intent from IAP SDK.
     **/
    fun sendPaymentResultData(data: Intent) {
        paymentHelper.onActivityResult(data = data)
    }

    fun productPayed() {
        canPlayVideo = true
    }
}