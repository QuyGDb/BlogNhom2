package com.example.blognhom2.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blognhom2.Fragment.CategoriesFragment
import com.example.blognhom2.R
import com.example.blognhom2.model.Categories
import com.example.blognhom2.model.Post

class CategoriesAdapter(var categoriesList : List<Categories>, var posts : MutableList<Post>): RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>() {
    private  var postsByCategories = mutableListOf<Post>()
    private var categoriesFragment = CategoriesFragment()

    inner class CategoriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoriesTxt: TextView = itemView.findViewById(R.id.categoriesTxt)
        val categoriesImage: ImageView = itemView.findViewById(R.id.categoriesImage)
        val childRecyclerView : RecyclerView = itemView.findViewById(R.id.childRecyclerView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        return CategoriesViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.categories_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return categoriesList.size
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        holder.itemView.apply {
            holder.categoriesTxt.text = categoriesList[position].categories



            categoriesFragment.GetImageFromUnsplash(categoriesList[position].categories, holder.categoriesImage, holder.itemView.context)
            setOnClickListener {
                categoriesList[position].isExpandable = !categoriesList[position].isExpandable

                holder.childRecyclerView.visibility = if(categoriesList[position].isExpandable) View.VISIBLE else View.GONE

                FindPostByCategories(holder.categoriesTxt.text.toString(), posts )
                val adapter = PostAdapter(postsByCategories)
                holder.childRecyclerView.adapter = adapter
                holder.childRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL,false)

            }

        }
    }
    private fun FindPostByCategories(categories: String, posts: MutableList<Post>) {
        postsByCategories.clear()
        for (post in posts) {
            if (post.categories == categories) {
                var isDuplicate = false
                for (postInCategories in postsByCategories) {
                    if (post.postID == postInCategories.postID) {
                        isDuplicate = true
                        break
                    }
                }
                if (!isDuplicate) {
                    postsByCategories.add(post)
                }
            }
        }
    }

}
