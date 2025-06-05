package cz.broc.productscanner

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import cz.broc.productscanner.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ActivityBase() {
    private val viewModel: MainViewModel by viewModels()

    // Storage Access Framework launcher
    private val openDocumentTreeLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
            if (uri != null) {
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                contentResolver.takePersistableUriPermission(uri, takeFlags)

                Toast.makeText(this, "Vybraná složka: $uri", Toast.LENGTH_LONG).show()

                // Zde můžeš volat funkci, která bude zapisovat do složky (např. CSV export)
                // Příklad: exportCsvToFolder(uri)
            } else {
                Toast.makeText(this, "Žádná složka nebyla vybrána.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        this.initBarcodeScanner()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermission() // Call SAF místo


         this.startActivity(Intent(this, ListActivity::class.java))
         this.finish()
    }

    private fun checkPermission() {
        val button = findViewById<Button>(R.id.button)
        button?.setOnClickListener {

            openDocumentTreeLauncher.launch(null)
        }
    }
}
