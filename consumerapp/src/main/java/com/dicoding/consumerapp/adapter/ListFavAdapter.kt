package com.dicoding.consumerapp.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.consumerapp.R
import com.dicoding.consumerapp.data.Fav

class ListFavAdapter(private val activity: Activity) : RecyclerView.Adapter<ListFavAdapter.ListViewHolder>(){
    private lateinit var onItemClickCallback: OnItemClickCallback
    var listFav = ArrayList<Fav>()
        set(listFavorite) {
            if (listFavorite.size > 0) {
                this.listFav.clear()
            }
            this.listFav.addAll(listFavorite)

            notifyDataSetChanged()
        }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView = itemView.findViewById(R.id.name_list)
        var tvUsername: TextView = itemView.findViewById(R.id.username_list)
        var tvFollower: TextView = itemView.findViewById(R.id.followers_list)
        var tvFollowing: TextView = itemView.findViewById(R.id.following_list)
        var imgPhoto: ImageView = itemView.findViewById(R.id.avatar_list)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ListFavAdapter.ListViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_user, viewGroup, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListFavAdapter.ListViewHolder, position: Int) {
        val user = listFav[position]
        Glide.with(holder.itemView.context)
            .load(user.avatar)
            .apply(RequestOptions().override(55, 55))
            .into(holder.imgPhoto)
        holder.tvName.text = user.name
        holder.tvUsername.text = user.username
        holder.tvFollower.text = "Followers: ${user.followers}"
        holder.tvFollowing.text = "Following: ${user.following}"
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(listFav[holder.adapterPosition]) }
    }

    override fun getItemCount(): Int {
        return listFav.size
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Fav)
    }
}