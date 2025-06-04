package cz.broc.productscanner.components

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cz.broc.productscanner.screens.ListItemsScreen
import cz.broc.productscanner.screens.ListStartScreen

@Composable
fun ListAppRouter(navController: NavHostController, onSelectFileClick: () -> Unit) {
    NavHost(navController = navController, startDestination = "loading") {
        composable("listStart") {
            ListStartScreen(onSelectFileClick = onSelectFileClick )
        }
        composable("listItems") {
            ListItemsScreen(onSelectFileClick)
        }
        composable("loading") {
            val activity = (LocalContext.current as Activity)
            BackHandler(true){
                activity.finish()
            }
            LoadingIndicator()
        }
    }
}