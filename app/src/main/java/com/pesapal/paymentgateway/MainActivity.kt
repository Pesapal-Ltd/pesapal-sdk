package com.pesapal.paymentgateway

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pesapal.paymentgateway.basket.BasketFragment
import com.pesapal.paymentgateway.catalogue.CatelogueFragment
import com.pesapal.paymentgateway.model.CatalogueModel
import com.pesapal.paymentgateway.viewmodel.AppViewModel


class MainActivity : AppCompatActivity() {


    private lateinit var navigationView: BottomNavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var catalogueList: MutableList<CatalogueModel.ProductsBean>
    private lateinit var wishList: MutableList<CatalogueModel.ProductsBean>
    private lateinit var basketListModel: MutableList<CatalogueModel.ProductsBean>
    private val viewModel: AppViewModel by viewModels()
    private lateinit var catalogueModel: CatalogueModel;
    private var PAYMENT_REQUEST = 100001



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData()
    }

    private fun initData(){
        catalogueList = arrayListOf()
        wishList = arrayListOf()
        basketListModel = arrayListOf()
        navigationView = findViewById(R.id.activity_main_bottom_navigation_view)
        toolbar = findViewById(R.id.toolbar)
        navigationView.setOnItemSelectedListener(selectedListener)

        handleViewModel()
        seearchCatalogue();
        setToolBar()
    }

    private fun setToolBar(){
        this.setSupportActionBar(toolbar);
        this.supportActionBar!!.title = "Catalogue"
    }

    private fun seearchCatalogue(){
        navigationView.selectedItemId = R.id.miCatalogue
    }



    private fun handleViewModel(){
        viewModel.selectedCategory.observe(this) {
            this.catalogueModel = catalogueModel
        }

        viewModel.searchCatalogueMessage.observe(this){
            if(it != null){
//                handleProgressBar(false)
                showMessage(it)
            }
        }

        viewModel.catalogueResponse.observe(this){
            //handleProgressBar(false)
            setCatalogueResponse(it)
        }

        viewModel.addCatalogueAddWishList.observe(this){
            if(it != null) {
                wishList.add(it)
                updateBasketList()
            }
        }
        viewModel.addCatalogueBucketList.observe(this){
            if(it != null) {
                basketListModel.add(it)
                updateBasketList()
            }
        }

        viewModel.removeCatalogueAddWishList.observe(this){
            if(it != null){
                wishList.remove(it)
                updateBasketList()
            }
        }

        viewModel.removeCatalogueBucketList.observe(this){
            if(it != null){
                removeFromBucket(it)
                updateBasketList()
            }
        }

        viewModel.checkOutCatalogue.observe(this){
            if(it){
                basketListModel.clear()
                updateBasketList()
            }
        }

    }

    private fun updateBasketList(){
        if(basketListModel.size > 0) {
            navigationView.getOrCreateBadge(R.id.miBasket).isVisible = true
            navigationView.getOrCreateBadge(R.id.miBasket).number = basketListModel.size;
        }else{
            navigationView.getOrCreateBadge(R.id.miBasket).isVisible = false
        }
    }



    private fun removeFromBucket(catalogueModel: CatalogueModel.ProductsBean){
        basketListModel.remove(catalogueModel)
    }

    private fun setCatalogueResponse(catalogueModel: CatalogueModel){
        catalogueList.addAll(catalogueModel.products!!)
    }

    private val selectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.miCatalogue -> {
                    val categoryFragment = CatelogueFragment.newInstance(catalogueList)
                    loadFragment(categoryFragment)
                    if(this.supportActionBar != null) {
                        this.supportActionBar!!.title = "Catalogue"
                    }
                    return@OnNavigationItemSelectedListener true
                }

                R.id.miBasket -> {
                    val basketFragment = BasketFragment.newInstance(basketListModel)
                    loadFragment(basketFragment)

                    if(this.supportActionBar != null) {
                        this.supportActionBar!!.title = "Bucket"
                    }

                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }


    private fun loadFragment(fragment: Fragment) {

        val fragmnt: FragmentManager = supportFragmentManager
        val fragmentTransaction = fragmnt.beginTransaction()
        fragmentTransaction.replace(R.id.mainFrag,fragment)
        fragmentTransaction.commitAllowingStateLoss()

    }


    private fun showMessage(message: String){
        Toast.makeText(this@MainActivity,message, Toast.LENGTH_LONG).show()

    }


    override fun onResume() {
        super.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PAYMENT_REQUEST) {
            if (resultCode == RESULT_OK) {
                val result = data?.getStringExtra("result")
                showMessage(result!!)
                Log.e(" result ", " ==> result"+result)
            }
            if (resultCode == RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    }


}