package cz.broc.productscanner

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@SuppressLint("Registered")
abstract class ActivityBase : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onActivityStart()
    }


    fun onActivityStart() {
        this.startBarcodeScanner()

    }

    fun onActivityStop() {
        this.stopBarcodeScanner()
    }

    protected fun initBarcodeScanner() {
        //mBarcodeScanner
    }


    private fun startBarcodeScanner() {

    }

    private fun stopBarcodeScanner() {

    }

    companion object {
     //   lateinit var mBarcodeScanner:
    }
}