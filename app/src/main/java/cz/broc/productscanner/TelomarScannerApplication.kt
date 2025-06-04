package cz.broc.productscanner

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TelomarScannerApplication @Inject constructor(): Application(){
}