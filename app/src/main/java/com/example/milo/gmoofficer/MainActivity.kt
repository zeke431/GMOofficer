package com.example.milo.gmoofficer

import android.support.v7.app.AppCompatActivity
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btn = findViewById(R.id.button) as Button
        btn.setOnClickListener { }
        val myImageView = findViewById(R.id.imgview) as ImageView

        val myBitmap = BitmapFactory.decodeResource(
                applicationContext.resources,
                R.drawable.puppy)
        myImageView.setImageBitmap(myBitmap)
        val detector = BarcodeDetector.Builder(applicationContext)
                .setBarcodeFormats(Barcode.DATA_MATRIX or Barcode.QR_CODE)
                .build()

        if (!detector.isOperational) {
            println("detector not operational")
            return
            val frame = Frame.Builder().setBitmap(myBitmap).build()
            val barcodes = detector.detect(frame)
            val thisCode = barcodes.valueAt(0)
            val txtView = findViewById(R.id.txtContent) as TextView
            txtView.text = thisCode.rawValue

        }
    }
}
// testing version control
// a subtle change