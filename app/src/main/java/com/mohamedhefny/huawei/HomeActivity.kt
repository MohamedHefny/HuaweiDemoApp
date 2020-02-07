package com.mohamedhefny.huawei

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.huawei.hms.iap.entity.ProductInfo
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.mohamedhefny.huawei.home.ProductsAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.toolbar_home.*

class HomeActivity : AppCompatActivity(), ProductsAdapter.ProductCallback {

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

        getAvailableProducts()
    }

    private fun bindUserData() {
        home_username.text = HuaweiIdAuthManager.getAuthResult()
            .familyName.plus(" ${HuaweiIdAuthManager.getAuthResult().givenName}")

        Picasso.get().load(HuaweiIdAuthManager.getAuthResult().avatarUri)
            .placeholder(R.drawable.ic_user)
            .error(R.mipmap.ic_launcher)
            .into(home_user_pic)
    }


    private fun getAvailableProducts() {
        paymentHelper.loadProducts(this).observe(this, Observer {
            //Set products adapter here.
        })
    }

    /**
     * Callback for the selected product.
     * You can implement and use it to get the selected product info.
     * @param productInfo is the selected product object.
     */
    override fun onProductSelected(productInfo: ProductInfo) {

    }
}