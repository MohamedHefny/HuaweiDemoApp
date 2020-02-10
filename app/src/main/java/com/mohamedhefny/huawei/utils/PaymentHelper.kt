package com.mohamedhefny.huawei.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.huawei.hms.iap.Iap
import com.huawei.hms.iap.IapApiException
import com.huawei.hms.iap.IapClient
import com.huawei.hms.iap.entity.*
import org.json.JSONException


class PaymentHelper(private val context: Context) {

    private val _paymentError: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    val paymentError: LiveData<Int> by lazy { _paymentError }

    private val _isPaymentSuccess: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isPaymentSuccess: LiveData<Boolean> by lazy { _isPaymentSuccess }

    private val TAG: String = PaymentHelper::class.java.simpleName

    /**
     * load the available products that user can purchase for.
     * @param context the calling activity or context.
     */
    fun loadProducts(): LiveData<List<ProductInfo>> {
        val productInfoList = MutableLiveData<List<ProductInfo>>()
        val iapClient: IapClient = Iap.getIapClient(context)
        iapClient.obtainProductInfo(createProductInfoReq())
            .addOnSuccessListener {
                productInfoList.postValue(it.productInfoList)
            }
            .addOnFailureListener {
                _paymentError.postValue(GET_PRODUCT_INFO_ERROR)
                Log.e(TAG, "Error while loading products")
            }
        return productInfoList
    }

    /**
     * Create a request info for the specific list of products.
     * @return ProductRequestInfo object to use for your request.
     */
    private fun createProductInfoReq(): ProductInfoReq =
        ProductInfoReq().apply {
            priceType = IapClient.PriceType.IN_APP_SUBSCRIPTION
            productIds = arrayListOf("VODDemoProduct3")
        }

    /**
     * create orders for in-app products in the PMS.
     * @param callbackActivity indicates the activity object that initiates a request.
     * @param productId ID list of products to be queried. Each product ID must exist and be unique in the current app.
     * @param productType  In-app product type {The IN_APP_CONSUMABLE set as the default one if you don't pass type}.
     */
    fun goToPay(
        callbackActivity: Activity, productId: String,
        productType: Int = IapClient.PriceType.IN_APP_SUBSCRIPTION
    ) {
        val iapClient = Iap.getIapClient(context)
        iapClient.createPurchaseIntent(createPurchaseIntentReq(productId, productType))
            .addOnSuccessListener {
                when {
                    it == null -> return@addOnSuccessListener
                    it.status == null -> return@addOnSuccessListener
                    it.status.hasResolution() -> {
                        try {
                            it.status.startResolutionForResult(callbackActivity, REQ_CODE_BUY)
                        } catch (exp: IntentSender.SendIntentException) {
                            Log.e(TAG, exp.message.toString())
                            return@addOnSuccessListener
                        }
                    }
                }
            }
            .addOnFailureListener {
                Log.e(TAG, it.message.toString())
                _paymentError.postValue(PAY_FOR_PRODUCT_ERROR)

                if (it is IapApiException) {
                    Log.e(TAG, "ipaApiException, Status code: ${it.statusCode}")
                    // handle error scenarios
                }
            }
    }

    /**
     * Create a PurchaseIntentReq instance.
     * @param productId ID of the in-app product to be paid.
     * @param productType In-app product type.
     * The in-app product ID is the product ID you set during in-app product configuration in AppGallery Connect.
     * @return PurchaseIntentReq
     */
    private fun createPurchaseIntentReq(productId: String, productType: Int): PurchaseIntentReq =
        PurchaseIntentReq().apply {
            this.productId = productId
            this.priceType = productType
            this.developerPayload = "test"
        }

    fun onActivityResult(ctx: Context = context, data: Intent) {
        val purchaseResultInfo = Iap.getIapClient(ctx)
            .parsePurchaseResultInfoFromIntent(data)

        when (purchaseResultInfo.returnCode) {
            OrderStatusCode.ORDER_STATE_SUCCESS -> {
                val success: Boolean = CipherUtil.doCheck(
                    purchaseResultInfo.inAppPurchaseData,
                    purchaseResultInfo.inAppDataSignature,
                    PUBLIC_KEY
                )
                if (success)
                    consumeOwnedPurchase(ctx, purchaseResultInfo.inAppPurchaseData)
                else
                    _paymentError.postValue(Pay_SUCCESSFUL_SIGN_FAILED)
            }
            OrderStatusCode.ORDER_STATE_CANCEL ->
                _paymentError.postValue(USER_CANCEL_PAYMENT)
            OrderStatusCode.ORDER_PRODUCT_OWNED ->
                _paymentError.postValue(YOU_OWEN_PRODUCT)
            else ->
                _paymentError.postValue(PAY_FIELD)

        }
    }

    /**
     * Consume the unconsumed purchase with type 0 after successfully delivering the product,
     * then the Huawei payment server will update the order status and the user can purchase the product again.
     * @param inAppPurchaseData JSON string that contains purchase order details.
     */
    private fun consumeOwnedPurchase(ctx: Context = context, inAppPurchaseData: String) {
        val iapClient: IapClient = Iap.getIapClient(ctx)
        iapClient.consumeOwnedPurchase(createConsumeOwnedPurchaseReq(inAppPurchaseData))
            .addOnSuccessListener { _isPaymentSuccess.postValue(true) }
            .addOnFailureListener {
                Log.e(TAG, it.message.toString())
                if (it is IapApiException) {
                    val iapApiExp: IapApiException = it
                    Log.e(TAG, "consumeOwnedPurchase fail,returnCode: ${iapApiExp.statusCode}")
                    // handle error scenarios
                } else {
                    // Other external errors
                }
                _isPaymentSuccess.postValue(false)
            }

    }

    /**
     * Create a ConsumeOwnedPurchaseReq instance.
     * @param purchaseData JSON string that contains purchase order details.
     * @return ConsumeOwnedPurchaseReq
     */
    private fun createConsumeOwnedPurchaseReq(purchaseData: String): ConsumeOwnedPurchaseReq {
        val req = ConsumeOwnedPurchaseReq()
        try {
            val inAppPurchaseData = InAppPurchaseData(purchaseData)
            req.purchaseToken = inAppPurchaseData.purchaseToken
        } catch (ex: JSONException) {
            ex.printStackTrace()
            Log.e(TAG, "createConsumeOwnedPurchaseReq JSONExeption")
        }
        return req
    }

    companion object {
        const val GET_PRODUCT_INFO_ERROR = -1
        const val PAY_FOR_PRODUCT_ERROR = -2
        const val Pay_SUCCESSFUL_SIGN_FAILED = -3
        const val USER_CANCEL_PAYMENT = -4
        const val YOU_OWEN_PRODUCT = -5
        const val PAY_FIELD = -6
    }
}