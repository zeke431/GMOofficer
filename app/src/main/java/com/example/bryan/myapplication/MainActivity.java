package com.example.bryan.myapplication;

import android.app.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.support.design.widget.Snackbar;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView Scanner;
    static String ENDPOINT = "http://192.168.1.87/"; // usually it's 192.168.1.something for your computers IP
    //private ProductInterface Products = null;
    private Handler mhandler = null;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        Scanner = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        Scanner.setFlash(true);
        setContentView(Scanner);                // Set the scanner view as the content view

    }

    @Override
    public void onStart(){
        super.onStart();
        mhandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                // This is what happens when the handler gets called (aka, barcode gets scanned)
                Bundle resp = inputMessage.getData();
                Product p = (Product) resp.getParcelable("product");
                Snackbar snack1 = Snackbar.make(Scanner,
                        p.name, Snackbar.LENGTH_SHORT);
                snack1.show();
            }
        };
        getProductWrapper("11111"); // this is a test value I put into the server.py file to create a product with this UPC
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


    public void notifyHandler(Parcelable a) {
        final Message msg = new Message();
        final Bundle b = new Bundle();
        b.putParcelable("product", a);
        msg.setData(b);
        mhandler.sendMessage(msg);
    }

    public void getProductWrapper(String upc) {
        Log.d("NET", "SOMETHING");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ENDPOINT).addConverterFactory(GsonConverterFactory.create())
                .build();

        ProductInterface Products = retrofit.create(ProductInterface.class);

        // make request
        Call<Product> p = Products.getProduct(upc);
        if (p != null) {
            // valid p, asynchronously call function
            p.enqueue(new Callback<Product>() {
                @Override
                public void onResponse(Call<Product> p, Response<Product> response) {
                    // the request worked!!
                    if (response.isSuccessful()) {
                        Product prod = response.body();
                        notifyHandler(prod);

                        // logging...
                        Log.d("NET", p.toString());
                        Log.d(TAG, prod.name);
                    }
                    Log.d("NET", response.body().toString());
                    Log.d("NET", p.toString());
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

        // get the product
        getProductWrapper(rawResult.getText().toString());

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




