package cz.broc.productscanner.screens
import android.app.Activity
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices
import cz.broc.productscanner.R
import cz.broc.productscanner.components.ListTopBarComponent
import java.util.jar.Manifest


@Composable
fun ListStartScreen(onSelectFileClick: ()-> Unit) {
    val activity = (LocalContext.current as Activity)
    BackHandler(true){
        activity.finish()
    }
    Scaffold(
        topBar = { ListTopBarComponent(onSelectFileClick, {}) },
        content = { ListStartScreenContent(it, onSelectFileClick) },
    )
}


@Composable
fun ListStartScreenContent(padding: PaddingValues, onSelectFileClick: ()->Unit, context: Context = LocalContext.current) {
    Column(modifier = Modifier.fillMaxHeight().padding(padding), verticalArrangement = Arrangement.Center) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

            val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
            var locationText = remember { mutableStateOf("Načítám polohu...") }

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { granted ->
                    if (granted) {
                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                            location?.let {
                                locationText.value = "Lat: ${it.latitude}, Lng: ${it.longitude}"
                            } ?: run {
                                locationText.value = "Nepodařilo se zjistit polohu"
                            }
                        }
                    } else {
                        locationText.value = "Oprávnění zamítnuto"
                    }
                }
            )

            // Žádost o oprávnění při prvním zobrazení
            LaunchedEffect (Unit) {
                launcher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }

            // UI
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Aktuální poloha:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(locationText.value)

                Button(onClick=onSelectFileClick) {
                    Text(stringResource(R.string.select_csv_btn))
                }
            }

        }
    }
}

@Preview
@Composable
fun ListStartScreenPreview() {
    ListStartScreen({})
}