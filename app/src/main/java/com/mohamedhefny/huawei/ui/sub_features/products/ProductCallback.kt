package com.mohamedhefny.huawei.ui.sub_features.products

import com.huawei.hms.iap.entity.ProductInfo

interface ProductCallback {
    /**
     * Callback for the selected product.
     * You can implement and use it to get the selected product info.
     * @param productInfo is the selected product object.
     */
    fun onProductSelected(productInfo: ProductInfo)
}