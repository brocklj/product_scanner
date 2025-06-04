package cz.broc.productscanner

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import cz.broc.productscanner.components.ListApp
import cz.broc.productscanner.ui.theme.ScannerTheme
import cz.broc.productscanner.viewmodel.ListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListActivity : ActivityBase() {
    val viewModel: ListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScannerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ListApp({onFileSelectClick()})
                }
            }
        }
    }

    fun onFileSelectClick() {
        viewModel.loadFileIntoDb()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(data?.data is Uri) {
            viewModel.sourceFilePath.value = data?.data
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}