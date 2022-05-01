package com.example.netflixremake.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImagerDonwloaderTask extends AsyncTask<String, Void, Bitmap> {


    private final WeakReference<ImageView> imageViewWeakReference;

    public ImagerDonwloaderTask(ImageView imageView) {
        this.imageViewWeakReference = new WeakReference<>(imageView);
    }


    @Override
    protected Bitmap doInBackground(String... params) {
        String urlImg = params[0];
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(urlImg);

            urlConnection = (HttpURLConnection) url.openConnection();

            int statusCode = urlConnection.getResponseCode();
            if (statusCode != 200)
                return null;

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null)
                return BitmapFactory.decodeStream(inputStream);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled())
            bitmap = null;

        ImageView imageView = imageViewWeakReference.get();
        if (imageView != null && bitmap != null) {
            if (bitmap.getWidth() < imageView.getWidth() || bitmap.getHeight() < imageView.getHeight()) {
                Matrix matrix = new Matrix();
                matrix.postScale((float) imageView.getWidth() / (float) bitmap.getWidth(),
                        (float) imageView.getHeight() / (float) bitmap.getHeight());

                bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,false);
            }

            imageView.setImageBitmap(bitmap);
        }
    }
}
