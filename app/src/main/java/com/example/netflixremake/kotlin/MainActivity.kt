package com.example.netflixremake.kotlin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.netflixremake.R
import com.example.netflixremake.model.Categories
import com.example.netflixremake.model.Category
import com.example.netflixremake.model.Movie
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_movie.view.*
import kotlinx.android.synthetic.main.category_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var mainAdapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val categories = arrayListOf<Category>()
        mainAdapter = MainAdapter(categories)
        recycler_view_main.adapter = mainAdapter
        recycler_view_main.layoutManager = LinearLayoutManager(this)


        retrofit().create(NetflixAPI::class.java)
            .listCategories()
            .enqueue(object : Callback<Categories>{
                override fun onResponse(call: Call<Categories>, response: Response<Categories>) {
                    if(response.isSuccessful){
                        response.body()?.let {
                            mainAdapter.categories.clear()
                            mainAdapter.categories.addAll(it.categories)
                            mainAdapter.notifyDataSetChanged()
                        }
                    }
                }

                override fun onFailure(call: Call<Categories>, t: Throwable) {
                    Toast.makeText(this@MainActivity,t.message,Toast.LENGTH_SHORT).show()
                }

            })
    }

    private inner class MainAdapter(val categories: MutableList<Category>) :
        RecyclerView.Adapter<CategoryHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder =
            CategoryHolder(layoutInflater.inflate(R.layout.category_item, parent, false))


        override fun onBindViewHolder(holder: CategoryHolder, position: Int) =
            holder.bind(categories[position])

        override fun getItemCount(): Int = categories.size
    }

    private inner class CategoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(category: Category) = with(itemView) {
            text_view_item.text = category.name
            recycler_view_movie.adapter = MovieAdapter(category.movies){ movie ->
                if (movie.id > 3) {
                   Toast.makeText(this@MainActivity,"Não foi implementado essa funcionalidade",Toast.LENGTH_LONG).show()
                }else{
                    val intent = Intent(this@MainActivity, MovieActivity::class.java)
                    intent.putExtra("id",movie.id)
                    startActivity(intent)
                }
            }
            recycler_view_movie.layoutManager =
                LinearLayoutManager(this@MainActivity, RecyclerView.HORIZONTAL, false)
        }
    }

    private class MovieHolder(itemView: View, val onClick: ((Movie) -> Unit)?) :
        RecyclerView.ViewHolder(itemView) {
        fun bind(movie: Movie) = with(itemView) {
            Glide.with(context)
                .load(movie.coverUrl)
                .placeholder(R.drawable.place_holder_bg)
                .into(image_view_cover)
            image_view_cover.setOnClickListener {
                onClick?.invoke(movie)
            }
        }
    }

    private inner class MovieAdapter(val movies: List<Movie>, private val listener :((Movie) -> Unit)?) :
        RecyclerView.Adapter<MovieHolder>() {

        /*val onClick: ((Int) -> Unit)? = { position ->
            if (movies[position].id <= 3) {
                val intent = Intent(this@MainActivity, MovieActivity::class.java)
                startActivity(intent)
            }
        }*/


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder =
            MovieHolder(
                layoutInflater.inflate(R.layout.movie_item, parent, false),
                listener
            )


        override fun onBindViewHolder(holder: MovieHolder, position: Int) =
            holder.bind(movies[position])

        override fun getItemCount(): Int = movies.size
    }
}