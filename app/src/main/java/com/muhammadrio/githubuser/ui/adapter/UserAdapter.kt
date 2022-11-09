package com.muhammadrio.githubuser.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.muhammadrio.githubuser.R
import com.muhammadrio.githubuser.databinding.UserItemBinding
import com.muhammadrio.githubuser.model.User

class UserAdapter : ListAdapter<User, UserAdapter.UserViewHolder>(DiffCallback()) {

    private var clickListener: ((User) -> Unit)? = null

    inner class UserViewHolder(private val binding: UserItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item:User){
            binding.tvUserName.text = item.login
            binding.ivProfilePicture.load(item.avatarUrl, builder = {
                placeholder(R.drawable.image_placeholder)
                transformations(CircleCropTransformation())
                error(R.drawable.ic_broken_image)
            })
            binding.root.setOnClickListener {
                clickListener?.invoke(item)
            }
        }
    }

    fun setOnItemClickListener(listener:(User) -> Unit){
        clickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UserItemBinding.inflate(LayoutInflater.from(parent.context))
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: User, newItem: User) =
            oldItem == newItem
    }
}