package com.example.netflixremake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.netflixremake.model.Category;
import com.example.netflixremake.model.Movie;
import com.example.netflixremake.model.MovieDetail;
import com.example.netflixremake.util.ImagerDonwloaderTask;
import com.example.netflixremake.util.MovieDetailTask;

import java.util.ArrayList;
import java.util.List;

public class MovieActivity extends AppCompatActivity implements MovieDetailTask.MovieDetailLoader {

    private TextView txtTitle;
    private TextView txtDesc;
    private TextView txtCast;
    private RecyclerView recyclerView;
    private MoviewAdapter movieAdapter;
    private ImageView imageCover;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        txtTitle = findViewById(R.id.text_view_title);
        txtDesc = findViewById(R.id.text_view_desc);
        txtCast = findViewById(R.id.text_view_cast);
        recyclerView = findViewById(R.id.recycler_view_similar);
        imageCover = findViewById(R.id.image_view_cover);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
            getSupportActionBar().setTitle(null);
        }


        List<Movie> movies = new ArrayList<>();

        movieAdapter = new MoviewAdapter(movies);
        recyclerView.setAdapter(movieAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            int id = extras.getInt("id");
            MovieDetailTask movieDetailTask = new MovieDetailTask(this);
            movieDetailTask.setMovieDetailLoader(this);
            movieDetailTask.execute("https://tiagoaguiar.co/api/netflix/" + id);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResult(MovieDetail movieDetail) {
        txtTitle.setText(movieDetail.getMovie().getTitle());
        txtDesc.setText(movieDetail.getMovie().getDescription());
        txtCast.setText(movieDetail.getMovie().getCast());

        ImagerDonwloaderTask imagerDonwloaderTask = new ImagerDonwloaderTask(imageCover);
        imagerDonwloaderTask.setShadowEnabled(true);
        imagerDonwloaderTask.execute(movieDetail.getMovie().getCoverUrl());


        movieAdapter.setMovies(movieDetail.getMovieSimilar());
        movieAdapter.notifyDataSetChanged();
    }

    private static class MovieHolder extends RecyclerView.ViewHolder {

        final ImageView imageViewCover;

        public MovieHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCover = itemView.findViewById(R.id.image_view_cover);

        }
    }


    private class MoviewAdapter extends RecyclerView.Adapter<MovieActivity.MovieHolder> {

        private final List<Movie> movies;

        private MoviewAdapter(List<Movie> movies) {
            this.movies = movies;
        }

        public void setMovies(List<Movie> movies){
            this.movies.clear();
            this.movies.addAll(movies);
        }

        @NonNull
        @Override
        public MovieActivity.MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View inflate = getLayoutInflater().inflate(R.layout.movie_item_similar, parent, false);
            return new MovieActivity.MovieHolder(inflate);
        }

        @Override
        public void onBindViewHolder(@NonNull MovieActivity.MovieHolder holder, int position) {
            Movie movie = movies.get(position);
            new ImagerDonwloaderTask(holder.imageViewCover).execute(movie.getCoverUrl());
        }

        @Override
        public int getItemCount() {
            return movies.size();
        }


    }


}