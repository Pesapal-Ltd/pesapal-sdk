package com.pesapal.paymentgateway.catalogue

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pesapal.paymentgateway.R
import com.pesapal.paymentgateway.model.CatalogueModel
import com.pesapal.paymentgateway.product_details.ProductDetailsSheet
import com.pesapal.paymentgateway.utils.PrefManager
import com.pesapal.paymentgateway.viewmodel.AppViewModel
import java.math.BigDecimal
import java.util.*


class CatelogueFragment: Fragment() {

    private lateinit var catalogueModelList: MutableList<CatalogueModel.ProductsBean>
    private lateinit var customAdapter: CustomAdapter;
    private lateinit var gridView: GridView;

    //2
    companion object {
        fun newInstance(catalogueModel: MutableList<CatalogueModel.ProductsBean>): CatelogueFragment {
            val fragment = CatelogueFragment();
            fragment.catalogueModelList = catalogueModel
            return fragment;
        }
    }

    //3
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.frag_catalogue, container, false)
        findView(view)
        return view;
    }

    private fun findView(view: View){
        gridView = view.findViewById(R.id.gridView)
        initData();
    }

    private fun initData(){
        catalogueModelList = arrayListOf()

        catalogueModelList.addAll(
            listOf(
            CatalogueModel.ProductsBean("Blue Shirt",R.drawable.blue_shirt, BigDecimal(8).setScale(2), BigDecimal(3),"Tops",BigDecimal(9).setScale(2),"1"),
            CatalogueModel.ProductsBean("Red Shirt",R.drawable.red_shirt,BigDecimal(8).setScale(2),BigDecimal(5),"Tops",BigDecimal(9).setScale(2),"2"),
            CatalogueModel.ProductsBean("Blue Pants",R.drawable.bluepant,BigDecimal(9).setScale(2),BigDecimal(8),"Pants",BigDecimal(9).setScale(2),"5"),
            CatalogueModel.ProductsBean("Red Pants",R.drawable.redpant,BigDecimal(10).setScale(2),BigDecimal(2),"Pants",BigDecimal(11).setScale(2),"6"),
            CatalogueModel.ProductsBean("Blue Shoes",R.drawable.shoesblue,BigDecimal(10).setScale(2),BigDecimal(2),"Shoes",BigDecimal(11).setScale(2),"9"),
            CatalogueModel.ProductsBean("Red Shoes",R.drawable.shoesred,BigDecimal(10).setScale(2),BigDecimal(12),"Shoes",BigDecimal(11).setScale(2),"10"),
            )
        )

            customAdapter = CustomAdapter(catalogueModelList, requireContext())
            gridView.adapter = customAdapter;

        gridView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
           val product = catalogueModelList[position]
            showDialog(product)
        }

    }

    private fun showDialog(catalogueModel: CatalogueModel.ProductsBean){
        var bottomFragment = ProductDetailsSheet.newInstance(catalogueModel)
        bottomFragment.show(childFragmentManager, "TAG")
    }


    class CustomAdapter(var catalogueModel: List<CatalogueModel.ProductsBean>?, var context: Context) : BaseAdapter() {

        private var layoutInflater: LayoutInflater? = null


        override fun getCount(): Int {
            return catalogueModel!!.size
        }

        override fun getItem(position: Int): Any {
            return catalogueModel!![position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            var convertView = convertView
            if (layoutInflater == null) {
                layoutInflater =
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            }
            if (convertView == null) {
                convertView = layoutInflater!!.inflate(R.layout.row_catalogue, null)
            }

            val catalogueModel = catalogueModel!![position]
            val names = convertView!!.findViewById<TextView>(R.id.tvName)
            val price = convertView.findViewById<TextView>(R.id.tvPrice)
            val image = convertView.findViewById<ImageView>(R.id.ivCatalogue)

            names.text = catalogueModel.name
            price.text =  PrefManager.getCurrency()+" "+catalogueModel.price
            image.setImageResource(catalogueModel.image);

            return convertView
        }



    }


}