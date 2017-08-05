package com.example.milo.gmoofficer

import android.support.v7.app.AppCompatActivity
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.gms.vision.CameraSource
import android.view.SurfaceView

import android.view.SurfaceHolder
import com.google.android.gms.vision.Detector




class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btn = findViewById(R.id.button) as Button

        val detector = BarcodeDetector.Builder(applicationContext)
                .setBarcodeFormats(Barcode.DATA_MATRIX or Barcode.QR_CODE)
                .build()

        var cameraView = findViewById(R.id.surfaceView) as SurfaceView
        var cameraSource = CameraSource.Builder(this, detector)
                .setRequestedPreviewSize(640, 480)
                .build()

        cameraView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    cameraSource.start(cameraView.holder)
                } catch (ie: Exception) {

                }

            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop();
            }
        })

        // Is the detector working properly?
        if (!detector.isOperational) { // If not
            println("detector not operational")
            return
        }
        detector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {}
        })

        // This code was here before, but not inside setOnClickListener { }
        btn.setOnClickListener {
            // What happens when we click the button?
            val frame = Frame.Builder().setBitmap(MyBitmap).build()
            val barcodes = detector.detect(frame)
            val thisCode = barcodes.valueAt(0)
            val txtView = findViewById(R.id.txtContent) as TextView
            txtView.text = thisCode.rawValue
        }
    }

}


// testing version control
// a subtle change