package com.mohamedhefny.huawei.ui.sub_features.products


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.huawei.hms.iap.entity.ProductInfo

import com.mohamedhefny.huawei.R
import kotlinx.android.synthetic.main.sheet_products_layout.*

class ProductsSheet : BottomSheetDialogFragment(), ProductsAdapter.ProductCallback {

    private lateinit var productCallback: ProductCallback
    private lateinit var productsList: List<ProductInfo>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sheet_products_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        products_sheet_recycler.adapter =
            ProductsAdapter(
                this,
                productsList
            )
    }

    fun setProductCallback(callback: ProductCallback) {
        this.productCallback = callback
    }

    fun setProductList(productsList: List<ProductInfo>) {
        this.productsList = productsList
    }

    override fun onProductSelected(productInfo: ProductInfo) {
        productCallback.onProductSelected(productInfo)
        dismiss()
    }
}
