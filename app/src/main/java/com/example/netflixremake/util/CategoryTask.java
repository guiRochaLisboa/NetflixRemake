package com.example.netflixremake.util;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.netflixremake.model.Category;
import com.example.netflixremake.model.Movie;

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
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class CategoryTask extends AsyncTask<String, Void, List<Category>> {


    private final WeakReference<Context> context;
    private ProgressDialog dialog;
    private CategoryLoader categotyLoader;

    public CategoryTask(Context context) {
        this.context = new WeakReference<>(context);
    }

    public void setCategoryLoader(CategoryLoader categoryLoader){
        this.categotyLoader = categoryLoader;
    }

    /**
     * Main - thread
     */

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Context context = this.context.get();

        if(context != null){
            dialog = ProgressDialog.show(context, "Carregando", "TA CARREGANDO", true);
        }
    }

    /**
     * thread - background
     */
    @Override
    protected List<Category> doInBackground(String... params) {
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

            List<Category> category = getCategories(new JSONObject(jsonAsString));
            in.close();
            return category;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }

    private List<Category> getCategories(JSONObject json) throws JSONException {
        List<Category> categories = new ArrayList<>();

        JSONArray categoryArray = json.getJSONArray("category");
        for (int i = 0; i < categoryArray.length(); i++) {
            JSONObject category = categoryArray.getJSONObject(i);
            String title = category.getString("title");
            List<Movie> movies = new ArrayList<>();

            JSONArray movieArray = category.getJSONArray("movie");
            for (int j =0; j < movieArray.length(); j++){
                JSONObject movie = movieArray.getJSONObject(j);
                String coverUrl = movie.getString("cover_url");

                Movie movieObj = new Movie();
                movieObj.setCoverUrl(coverUrl);

                movies.add(movieObj);
            }

            Category categoryObj = new Category();
            categoryObj.setName(title);
            categoryObj.setMovies(movies);

            categories.add(categoryObj);
        }

        return categories;
    }

    /**
     * main - thread
     */

    @Override
    protected void onPostExecute(List<Category> categories) {
        super.onPostExecute(categories);
        dialog.dismiss();

        //Delegar o objeto pro Listener
        if(categotyLoader != null){
            categotyLoader.onResult(categories);
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

    public interface CategoryLoader {
        void onResult(List<Category> categories);
    }

}
