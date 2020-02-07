package com.mohamedhefny.huawei

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.huawei.hmf.tasks.Task
import com.huawei.hms.iap.Iap
import com.huawei.hms.iap.IapClient
import com.huawei.hms.iap.entity.ProductInfo
import com.huawei.hms.iap.entity.ProductInfoReq
import com.huawei.hms.iap.entity.ProductInfoResult

class PaymentHelper {

    val paymentError: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    private val TAG: String = PaymentHelper::class.java.simpleName

    /**
     * load the available products that user can purchase for.
     * @param activity the calling activity or context.
     */
    fun loadProducts(activity: Activity): LiveData<List<ProductInfo>> {
        val productInfoList = MutableLiveData<List<ProductInfo>>()
        val iapClient: IapClient = Iap.getIapClient(activity)
        iapClient.obtainProductInfo(createProductInfoReq())
            .addOnSuccessListener {
                productInfoList.postValue(it.productInfoList)
            }
            .addOnFailureListener {
                paymentError.postValue("GetProductInfo")
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
            priceType = IapClient.PriceType.IN_APP_CONSUMABLE
            productIds = arrayListOf("VODDemoProduct")
        }

}