package com.mohamedhefny.huawei.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.huawei.hms.iap.entity.ProductInfo
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.mohamedhefny.huawei.utils.PaymentHelper
import com.mohamedhefny.huawei.R
import com.mohamedhefny.huawei.ui.signin.SignInActivity
import com.mohamedhefny.huawei.ui.sub_features.products.ProductCallback
import com.mohamedhefny.huawei.ui.sub_features.products.ProductsSheet
import com.mohamedhefny.huawei.utils.REQ_CODE_BUY
import com.mohamedhefny.huawei.utils.showToast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.sheet_products_layout.*
import kotlinx.android.synthetic.main.toolbar_home.*

class HomeActivity : AppCompatActivity(), ProductCallback {

    private val paymentHelper: PaymentHelper by lazy { PaymentHelper() }
    private val TAG: String = HomeActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (HuaweiIdAuthManager.getAuthResult() == null) {
            Log.e(TAG, "User didn't logged in yet!")
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        bindUserData()

        observePaymentErrors()
        getAvailableProducts()
    }

    private fun bindUserData() {
        home_username.text = HuaweiIdAuthManager.getAuthResult()
            .familyName.plus(" ${HuaweiIdAuthManager.getAuthResult().givenName}")

        Picasso.get().load(HuaweiIdAuthManager.getAuthResult().avatarUri)
            .placeholder(R.drawable.ic_user).error(R.mipmap.ic_launcher)
            .into(home_user_pic)
    }

    /**
     * Use PaymentHelper to get the product for parches.
     */
    private fun getAvailableProducts() {
        paymentHelper.loadProducts(this)
            .observe(this, Observer {
                if (it.isNotEmpty())
                    ProductsSheet().apply {
                        setProductList(it)
                        setProductCallback(this@HomeActivity)
                        show(supportFragmentManager, "ProductsFragment")
                    }
                else
                    showToast(R.string.not_products_available, Toast.LENGTH_LONG)
            })
    }

    /**
     * Handel payment success or failed.
     */
    private fun observePaymentStatus() {
        paymentHelper.isPaymentSuccess.observe(this, Observer {
            if (it) {
                showToast("Payed Done!")
            } else {
                //Show error
                showToast("Paying Error!")
            }
        })
    }

    /**
     * Handel different error cases during the payment flow.
     */
    private fun observePaymentErrors() {
        paymentHelper.paymentError.observe(this, Observer {
            when (it) {
                PaymentHelper.GET_PRODUCT_INFO_ERROR ->
                    showToast(R.string.cant_load_products)
                PaymentHelper.PAY_FOR_PRODUCT_ERROR ->
                    showToast(R.string.pay_api_error, Toast.LENGTH_LONG)
                PaymentHelper.Pay_SUCCESSFUL_SIGN_FAILED ->
                    showToast(R.string.cant_verify_payment, Toast.LENGTH_LONG)
                PaymentHelper.USER_CANCEL_PAYMENT ->
                    showToast(R.string.payment_canceled)
                PaymentHelper.YOU_OWEN_PRODUCT ->
                    showToast(R.string.product_owned)
                PaymentHelper.PAY_FIELD ->
                    showToast(R.string.product_owned)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_CODE_BUY) {
            if (data != null) {
                observePaymentStatus()
                paymentHelper.onActivityResult(this, data)
            } else
                Toast.makeText(this, "PaymentError", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Callback for the selected product.
     * You can implement and use it to get the selected product info.
     * @param productInfo is the selected product object.
     */
    override fun onProductSelected(productInfo: ProductInfo) {
        paymentHelper.goToPay(this, productInfo.productId)
    }
}