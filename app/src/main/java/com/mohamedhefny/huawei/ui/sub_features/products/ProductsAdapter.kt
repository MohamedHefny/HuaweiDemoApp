package com.mohamedhefny.huawei.ui.sub_features.products

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.huawei.hms.iap.entity.ProductInfo
import com.mohamedhefny.huawei.R
import kotlinx.android.synthetic.main.item_product.view.*

class ProductsAdapter(
    private val callback: ProductCallback,
    private val products: List<ProductInfo>
) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val productView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(
            productView
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bindViewData(products[position])
        holder.itemView.setOnClickListener {
            callback.onProductSelected(products[position])
        }
    }

    override fun getItemCount(): Int = products.size

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val productName = itemView.item_product_name

        fun bindViewData(productInfo: ProductInfo) {
            productName.text = layoutPosition.plus(1)
                .toString().plus("- ${productInfo.productName}")
        }
    }

    interface ProductCallback {
        /**
         * Callback for the selected product.
         * You can implement and use it to get the selected product info.
         * @param productInfo is the selected product object.
         */
        fun onProductSelected(productInfo: ProductInfo)
    }
}