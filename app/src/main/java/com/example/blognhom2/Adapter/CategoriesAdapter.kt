package com.example.blognhom2.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blognhom2.API.PostApi
import com.example.blognhom2.Fragment.CategoriesFragment
import com.example.blognhom2.R
import com.example.blognhom2.model.Category
import com.example.blognhom2.model.PostInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CategoriesAdapter(var categoriesList : List<Category>, var posts : MutableList<PostInfo>): RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>() {
    private  var postsByCategories = mutableListOf<PostInfo>()
    private var categoriesFragment = CategoriesFragment()
    private var isLoading = false
    private var visibleThreshold = 5 // Number of items from the bottom of the list at which loading more is triggered
    private var offset = 0 // The offset for loading more posts
    inner class CategoriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoriesTxt: TextView = itemView.findViewById(R.id.categoriesTxt)
        val categoriesImage: ImageView = itemView.findViewById(R.id.categoriesImage)
        val childRecyclerView : RecyclerView = itemView.findViewById(R.id.childRecyclerView)

    }

    fun setData(categories: MutableList<Category>, posts: MutableList<PostInfo>) {
        this.categoriesList = categories
        this.posts = posts
        notifyDataSetChanged()
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
            holder.categoriesTxt.text = categoriesList[position].category

            categoriesFragment.GetImageFromUnsplash(categoriesList[position].category, holder.categoriesImage, holder.itemView.context)
            setOnClickListener {
                categoriesList[position].isExpandable = !categoriesList[position].isExpandable
                holder.childRecyclerView.visibility = if(categoriesList[position].isExpandable) View.VISIBLE else View.GONE
                FindPostByCategory(holder.categoriesTxt.text.toString(),holder)

            }

        }
    }
    private fun FindPostByCategory(category: String, holder: CategoriesViewHolder) {
        postsByCategories.clear()
        getPostsByCategory(category, 0) { postInfos ->
            if (postInfos != null) {
                val adapter = PostAdapter(postInfos)
                holder.childRecyclerView.adapter = adapter
                holder.childRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL,false)
                holder.childRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)

                        val totalItemCount = (holder.childRecyclerView.layoutManager as LinearLayoutManager).itemCount
                        val lastVisibleItem = (holder.childRecyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()

                        if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                            // End has been reached, load more items
                            loadMoreItems(category, holder.childRecyclerView.adapter as PostAdapter)
                            isLoading = true
                        }

                    }

                })
            }
        }

    }
    private fun loadMoreItems(category: String, adapter : PostAdapter) {
        // Increase your offset
        offset += 1

        // Call your API here
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8081/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(PostApi::class.java)
        val call = api.getPostsByCategory(category, offset);

        call.enqueue(object : Callback<List<PostInfo>> {
            override fun onResponse(call: Call<List<PostInfo>>, response: Response<List<PostInfo>>) {
                println(response)
                if (response.isSuccessful) {
                    val posts = response.body()
                    posts?.let {
                        postsByCategories.addAll(it)
                        adapter.notifyDataSetChanged()
                    }
                }
                isLoading = false
            }

            override fun onFailure(call: Call<List<PostInfo>>, t: Throwable) {
                // Handle the error
                isLoading = false
            }
        })
    }
    private fun getPostsByCategory(category: String, page: Int, onResult: (List<PostInfo>?) -> Unit){
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8081/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(PostApi::class.java)
        var call = api.getPostsByCategory(category, page);

        call.enqueue(object : Callback<List<PostInfo>> {
            override fun onResponse(call: Call<List<PostInfo>>, response: Response<List<PostInfo>>) {
                println("ResponsePost")
                println(response)
                if (!response.isSuccessful) {
                    println("Code: " + response.code())
                    onResult(null);
                    return
                }
                val posts = response.body()
                onResult(posts);
            }

            override fun onFailure(call: Call<List<PostInfo>>, t: Throwable) {
                println(t.message)
                onResult(null);
            }
        })
    }
}
