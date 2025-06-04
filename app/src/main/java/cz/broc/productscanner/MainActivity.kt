package cz.broc.productscanner

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cz.broc.productscanner.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ActivityBase() {
    private val viewModel: MainViewModel by viewModels()

    private val REQUEST_PERMISSION_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        this.initBarcodeScanner()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()
    }

    override fun onResume() {
        super.onResume()
        checkPermission()
    }

    private fun checkPermission() {
        if(isPermissionGranted()) {
            onActivityStop()
            this.startActivity(Intent(this, ListActivity::class.java))
            this.finish()
        }
        val button = findViewById<Button>(R.id.button)
        // Check if permission is granted
        if (isPermissionGranted()) {
            // Permission is already granted
            button.isEnabled = true
        } else {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE ),
                REQUEST_PERMISSION_CODE
            )
        }

        button.setOnClickListener {
            if (isPermissionGranted()) {
                // Permission is granted, you can access internal storage here
                // For example, you can access the external directory
                val storagePath = Environment.getExternalStorageDirectory().absolutePath
                Toast.makeText(
                    this@MainActivity,
                    "Internal Storage Path: $storagePath",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                // Permission is not granted, show a message or take appropriate action
                Toast.makeText(
                    this@MainActivity,
                    "Permission denied. Cannot access internal storage.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun isPermissionGranted(): Boolean {
        // Check if permission is granted
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, enable the button
                val button = findViewById<Button>(R.id.button)
                button.isEnabled = true
            } else {
                // Permission denied, show a message or take appropriate action
                Toast.makeText(
                    this,
                    "Permission denied. Cannot access internal storage.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}