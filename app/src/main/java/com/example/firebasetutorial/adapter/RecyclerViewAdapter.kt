package com.example.firebasetutorial.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebasetutorial.R

class RecyclerViewAdapter(ctx: Context, list: ArrayList<String>) :
    RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>() {

    private val ctx: Context
    private val list: ArrayList<String>

    init {
        this.ctx = ctx
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {

        return RecyclerViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val uri: Uri = Uri.parse(list[position])

        Log.i("cvb", list[position])


        Glide.with(ctx)
            .load(uri)
            .into(holder.imgApi)

        holder.imgApi.setOnClickListener {

        }
    }

    class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imgApi: ImageView = itemView.findViewById(R.id.imgApi)
    }
}
