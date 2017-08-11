package com.example.bryan.myapplication;

import android.app.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView Scanner;
    static String ENDPOINT = "http://127.0.0.1:5000/";
    //private ProductInterface Products = null;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        Scanner = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        Scanner.setFlash(true);
        setContentView(Scanner);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        Scanner.setResultHandler(this); // Register ourselves as a handler for scan results.
        Scanner.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        Scanner.stopCamera();           // Stop camera on pause
    }

    public void connectToServer(Result rawResult) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ENDPOINT).addConverterFactory(GsonConverterFactory.create())
                .build();
        ProductInterface Products = retrofit.create(ProductInterface.class);

        // Call AsyncTask here...
        Call<Product> p = Products.getProduct("11111"); //rawResult.getText().toString()
        if (p != null) {
            // valid p, asynchronously call function
            p.enqueue(new Callback<Product>() {
                @Override
                public void onResponse(Call<Product> p, Response<Product> response) {
                    // the request worked!!
                    Log.d(TAG, response.body().toString());
                    Log.d(TAG, p.toString());
                }

                @Override
                public void onFailure(Call<Product> p, Throwable e) {
                    // the request failed!!
                    Log.d(TAG, e.getLocalizedMessage());
                }
            });
        }
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v(TAG, rawResult.getText()); // Prints scan results
        Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)


        // If you would like to resume scanning, call this method below:
        Scanner.resumeCameraPreview(this);
    }

    private class CheckServer extends AsyncTask<Result, Integer, Long> {
        protected Long doInBackground(Result... results) {
            int count = results.length;
            long totalSize = 0;
            for (int i = 0; i < count; i++) {
                // process results

                if (isCancelled()) break;
            }
            return totalSize;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(Long result) {
            //showDialog("Downloaded " + result + " bytes");
        }
    }

}




