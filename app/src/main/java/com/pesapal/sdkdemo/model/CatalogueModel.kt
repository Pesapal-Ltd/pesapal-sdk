package com.pesapal.sdkdemo.model

import java.math.BigDecimal

class CatalogueModel {
    /**
     * name : Blue Shirt
     * image : drawable
     * price : 7.99
     */
    var products: List<ProductsBean>? = null

    data class ProductsBean(
        var name: String? = null,
        var image: Int,
        var price: BigDecimal = BigDecimal.ZERO,
    )
}