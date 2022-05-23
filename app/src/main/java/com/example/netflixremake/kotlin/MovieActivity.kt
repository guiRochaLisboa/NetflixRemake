package com.example.netflixremake.kotlin

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.example.netflixremake.R
import com.example.netflixremake.model.Movie
import com.example.netflixremake.util.ImagerDonwloaderTask
import com.example.netflixremake.util.MovieDetailTask
import kotlinx.android.synthetic.main.activity_movie.*
import kotlinx.android.synthetic.main.activity_movie.view.*

class MovieActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        intent.extras?.let {
            val id = it.getInt("id")
            val task = MovieDetailTask(this)
            task.setMovieDetailLoader { movieDetail ->
                text_view_title.text = movieDetail.movie.title
                text_view_cast.text = movieDetail.movie.cast
                text_view_desc.text = movieDetail.movie.description

                ImagerDonwloaderTask(image_view_cover).apply {
                    setShadowEnabled(true)
                    execute(movieDetail.movie.coverUrl)
                }

            }
            task.execute("https://tiagoaguiar.co/api/netflix/$id")
        }

        setSupportActionBar(toolbar)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
            it.title = null
        }
    }

    private inner class MovieAdapter(private val movies: List<Movie>) :
        RecyclerView.Adapter<MovieHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder =
            MovieHolder(
                layoutInflater.inflate(R.layout.movie_item_similar, parent, false)

            )

        override fun onBindViewHolder(holder: MovieHolder, position: Int) =
            holder.bind(movies[position])

        override fun getItemCount(): Int = movies.size


    }

    private class MovieHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(movie: Movie) {
            with(itemView) {
                ImagerDonwloaderTask(image_view_cover).execute(movie.coverUrl)
            }
        }
    }
}