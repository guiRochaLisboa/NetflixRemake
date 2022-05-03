package com.example.netflixremake.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.example.netflixremake.model.Category;
import com.example.netflixremake.model.Movie;
import com.example.netflixremake.model.MovieDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MovieDetailTask extends AsyncTask<String,Void, MovieDetail> {

    private final WeakReference<Context> context;
    private MovieDetailLoader movieDetailLoader;
    private ProgressDialog dialog;

    public MovieDetailTask(Context context) {
        this.context = new WeakReference<>(context);
    }

    public void setMovieDetailLoader(MovieDetailLoader movieDetailLoader) {
        this.movieDetailLoader = movieDetailLoader;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Context context = this.context.get();

        if (context != null) {
            dialog = ProgressDialog.show(context, "Carregando", "carregando o filme aguarde...", true);
        }
    }

    @Override
    protected MovieDetail doInBackground(String... params) {
        String url = params[0];

        try {
            URL requestUrl = new URL(url);

            HttpsURLConnection urlConnection = (HttpsURLConnection) requestUrl.openConnection();
            urlConnection.setReadTimeout(2000);
            urlConnection.setConnectTimeout(2000);

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == 400) {
                throw new IOException("Erro na comunicação do servidor");
            }

            InputStream inputStream = urlConnection.getInputStream();

            BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());

            String jsonAsString = toString(in);

            MovieDetail movieDetail = getMovieDetail(new JSONObject(jsonAsString));
            in.close();
            return movieDetail;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }

    private MovieDetail getMovieDetail(JSONObject jsonObject) throws JSONException {
        int id = jsonObject.getInt("id");
        String title = jsonObject.getString("title");
        String desc = jsonObject.getString("desc");
        String cast = jsonObject.getString("cast");
        String coverUrl = jsonObject.getString("cover_url");

        List<Movie> movies = new ArrayList<>();
        JSONArray movieArray = jsonObject.getJSONArray("movie");

        for (int i = 0; i < movieArray.length();i++){
            JSONObject movie = movieArray.getJSONObject(i);
            String c = movie.getString("cover_url");
            int idSimilar = movie.getInt("id");

            Movie similar = new Movie();
            similar.setId(idSimilar);
            similar.setCoverUrl(c);

            movies.add(similar);
        }

        Movie movie = new Movie();
        movie.setId(id);
        movie.setTitle(title);
        movie.setDescription(desc);
        movie.setCast(cast);
        movie.setCoverUrl(coverUrl);

        return new MovieDetail(movie,movies);

    }

    @Override
    protected void onPostExecute(MovieDetail movieDetail) {
        super.onPostExecute(movieDetail);

        dialog.dismiss();

        if(movieDetailLoader !=null){
            movieDetailLoader.onResult(movieDetail);
        }
    }


    private String toString(InputStream is) throws IOException {
        byte[] bytes = new byte[1024];

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int lidos;
        while ((lidos = is.read(bytes)) > 0) {
            baos.write(bytes, 0, lidos);
        }

        return new String(baos.toByteArray());
    }

    public interface MovieDetailLoader {
        void onResult(MovieDetail movieDetail);
    }


}
