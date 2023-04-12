package com.pesapal.sdkdemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.pesapal.sdkdemo.R
import com.squareup.picasso.Picasso
import com.pesapal.sdkdemo.model.CatalogueModel
import com.pesapal.sdkdemo.utils.PrefManager

class DemoCartAdapter(val clickListener: clickedListener) : RecyclerView.Adapter<DemoCartAdapter.BucketListAdapterVh>() {

    var bucketList = listOf<CatalogueModel.ProductsBean>()
    private var context: Context? = null

    fun setData(wishList: List<CatalogueModel.ProductsBean>){
        this.bucketList = wishList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BucketListAdapterVh {
        context=parent.context

        return BucketListAdapterVh(
            LayoutInflater.from(context).inflate(R.layout.row_catalogue,parent,false)
        )
    }

    override fun onBindViewHolder(holder: BucketListAdapterVh, position: Int) {
        var bucketResponse = bucketList[position]

        holder.tvName.text = bucketResponse.name
        holder.tvPrice.text =  PrefManager.getCurrency()+" "+bucketResponse.price
        Picasso.get().load(bucketResponse.image).into(holder.ivCatalogue);


        holder.plusBtn.setOnClickListener {
            val count:Int = Integer.parseInt(holder.qtyNumber.text.toString())
            val newCount =count + 1
            holder.qtyNumber.text = newCount.toString()
            clickListener.Clicked(true,bucketResponse)

        }

        holder.minusBtn.setOnClickListener{
            val count:Int = Integer.parseInt(holder.qtyNumber.text.toString())
            if(count <= 0){
                showMessage("This item does not exist in cart")
            }else {
                val newCount = count - 1
                holder.qtyNumber.text = newCount.toString()
                clickListener.Clicked(false,bucketResponse)
            }
        }


    }

    private fun showMessage(message: String){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }


    override fun getItemCount(): Int {
        return bucketList.size
    }

     interface clickedListener {
        fun Clicked(isAdd: Boolean, catalogueModel: CatalogueModel.ProductsBean)
    }

    class BucketListAdapterVh(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivCatalogue = itemView.findViewById<ImageView>(R.id.ivCatalogue)
        var tvName = itemView.findViewById<TextView>(R.id.tvName)
        var tvPrice = itemView.findViewById<TextView>(R.id.tvPrice)
        val plusBtn = itemView.findViewById<ImageView>(R.id.iv_detail_plus)
        val minusBtn = itemView.findViewById<ImageView>(R.id.iv_detail_minus)
        val qtyNumber = itemView.findViewById<TextView>(R.id.tv_detail_qty)

    }


}