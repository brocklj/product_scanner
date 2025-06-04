package cz.broc.productscanner.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import cz.broc.productscanner.ListActivity

@Composable
fun ListApp(onSelectFileClick: ()-> Unit) {
    val viewModel = (LocalContext.current as ListActivity).viewModel
    val loading by viewModel.loading.collectAsState()
    val navController = rememberNavController()
    val listItems by viewModel.listItems.collectAsState().value.collectAsState(emptyList())
    ListAppRouter(navController,
        {
            navController.navigate("loading")
            onSelectFileClick()
        }
    )

    if(loading) {
        navController.navigate("loading")
    } else {
        if(listItems.count() > 0) {
            if(navController.currentDestination?.route != "listItems") {
                navController.navigate("listItems")
            }
        } else {
            navController.navigate("listStart")
        }
    }
}