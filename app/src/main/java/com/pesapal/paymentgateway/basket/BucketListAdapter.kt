package com.pesapal.paymentgateway.basket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pesapal.paymentgateway.R
import com.squareup.picasso.Picasso
import com.pesapal.paymentgateway.model.CatalogueModel

class BucketListAdapter(val clickListener: clickedListener) : RecyclerView.Adapter<BucketListAdapter.BucketListAdapterVh>() {

    var bucketList = listOf<CatalogueModel.ProductsBean>()

    fun setData(wishList: List<CatalogueModel.ProductsBean>){
        this.bucketList = wishList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BucketListAdapterVh {
        return BucketListAdapterVh(
            LayoutInflater.from(parent.context).inflate(R.layout.row_backet,parent,false)
        )
    }

    override fun onBindViewHolder(holder: BucketListAdapterVh, position: Int) {
        var bucketResponse = bucketList[position]

        holder.tvName.text = bucketResponse.name
        holder.tvPrice.text = "Kes "+bucketResponse.price
        Picasso.get().load(bucketResponse.image).into(holder.ivCatalogue);

        holder.imageViewDelete.setOnClickListener {
            clickListener.Clicked(true,bucketResponse)
        }

    }

    override fun getItemCount(): Int {
        return bucketList.size
    }

     interface clickedListener {
        fun Clicked(isSwipe: Boolean, story: CatalogueModel.ProductsBean)
    }

    class BucketListAdapterVh(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivCatalogue = itemView.findViewById<ImageView>(R.id.ivCatalogue)
        var tvName = itemView.findViewById<TextView>(R.id.tvName)
        var tvPrice = itemView.findViewById<TextView>(R.id.tvPrice)
        var imageViewDelete = itemView.findViewById<ImageView>(R.id.imageViewDelete)
        var viewForeground = itemView.findViewById<LinearLayout>(R.id.view_foreground)
    }


}