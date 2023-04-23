package com.a7codes.menu25.Classes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloader {

    public static void downloadImage(String imageUrl, String fileName) {
        new DownloadImageTask(imageUrl, fileName).execute();
    }

    private static class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {

        private String imageUrl;
        private String fileName;

        DownloadImageTask(String imageUrl, String fileName) {
            this.imageUrl = imageUrl;
            this.fileName = fileName;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                Log.e("ImageDownloader", "Error downloading image", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                saveImage(bitmap, fileName);
            }
        }
    }

    private static void saveImage(Bitmap bitmap, String fileName) {
        try {
            File directory = new File(Environment.getExternalStorageDirectory() + "/DCIM/A7Menu_V25/Items");;
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, fileName + ".png");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            Log.e("ImageDownloader", "Error saving image", e);
        }
    }
}
