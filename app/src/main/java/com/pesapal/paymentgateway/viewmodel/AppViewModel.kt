package com.pesapal.paymentgateway.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pesapal.paymentgateway.model.CatalogueModel

class AppViewModel: ViewModel() {

    private val mutableSelectedCatalogue = MutableLiveData<CatalogueModel>()
    val selectedCategory: LiveData<CatalogueModel> get() = mutableSelectedCatalogue

    private val mutableSearchedCatalogue = MutableLiveData<CatalogueModel>()
    val catalogueResponse: LiveData<CatalogueModel> get() =mutableSearchedCatalogue;

    private val mutableAddCatalogueAddWishList = MutableLiveData<CatalogueModel.ProductsBean>()
    val addCatalogueAddWishList: LiveData<CatalogueModel.ProductsBean> get() =mutableAddCatalogueAddWishList;

    private val mutableAddCatalogueAddBucketList = MutableLiveData<CatalogueModel.ProductsBean>()
    val addCatalogueBucketList: LiveData<CatalogueModel.ProductsBean> get() =mutableAddCatalogueAddBucketList;

    private val mutableRemoveCatalogueAddWishList = MutableLiveData<CatalogueModel.ProductsBean>()
    val removeCatalogueAddWishList: LiveData<CatalogueModel.ProductsBean> get() =mutableRemoveCatalogueAddWishList;

    private val mutableRemoveCatalogueAddBucketList = MutableLiveData<CatalogueModel.ProductsBean>()
    val removeCatalogueBucketList: LiveData<CatalogueModel.ProductsBean> get() =mutableRemoveCatalogueAddBucketList;

    private val catalogueResponseMessage = MutableLiveData<String>()
    val searchCatalogueMessage: LiveData<String> get() = catalogueResponseMessage;

    private val catalogueCheckOut = MutableLiveData<Boolean>()
    val checkOutCatalogue: LiveData<Boolean> get() = catalogueCheckOut;


    fun addToBucket(catalogueModel: CatalogueModel.ProductsBean){
        mutableAddCatalogueAddBucketList.postValue(catalogueModel)
    }


    fun addToWishList(catalogueModel: CatalogueModel.ProductsBean){
        mutableAddCatalogueAddWishList.postValue(catalogueModel)
    }


    fun removeToBucket(catalogueModel: CatalogueModel.ProductsBean){
        mutableRemoveCatalogueAddBucketList.postValue(catalogueModel)
    }


    fun removeToWishList(catalogueModel: CatalogueModel.ProductsBean){
        mutableRemoveCatalogueAddWishList.postValue(catalogueModel)
    }

}