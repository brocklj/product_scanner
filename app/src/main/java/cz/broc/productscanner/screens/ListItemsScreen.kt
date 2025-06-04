package cz.broc.productscanner.screens
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import cz.broc.productscanner.ListActivity
import cz.broc.productscanner.components.LazyList
import cz.broc.productscanner.components.ListItemCheckAlertDialog
import cz.broc.productscanner.components.ListTopBarComponent
import cz.broc.productscanner.components.QttyEditorAlertDialog
import cz.broc.productscanner.components.QttyEditorAlertDialogType


@Composable
fun ListItemsScreen(onSelectFileClick: ()-> Unit) {
    val viewModel = (LocalContext.current as ListActivity).viewModel
    //When touch back do noting
    val activity = (LocalContext.current as Activity)
    BackHandler(true){
        activity.finish()
    }
    Scaffold(
        topBar = { ListTopBarComponent(onSelectFileClick, {viewModel.onExportClick(activity)}) },
        content = { ListItemsScreenContent(it) },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.upcAddDialogOpen.value = true },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary,
                //modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Filled.Add, "")
            }
        }
    )
}


@Composable
fun ListItemsScreenContent(padding: PaddingValues) {
    val viewModel = (LocalContext.current as ListActivity).viewModel
    val listItems by viewModel.listItems.collectAsState().value.collectAsState(emptyList())
    val qttyDialogOpen by viewModel.qttyDialogOpen.collectAsState()
    val qttyAddDialogOpen by viewModel.qttyAddDialogOpen.collectAsState()
    val upcAddDialogOpen by viewModel.upcAddDialogOpen.collectAsState()
    val selectedItem by viewModel.selectedItem.collectAsState()
    val isCheckDialogOpen by viewModel.isCheckDialogOpen.collectAsState()

    val lazyListState by viewModel.listState.collectAsState()

    Column(modifier = Modifier
        .fillMaxHeight()
        .padding(PaddingValues(top = padding.calculateTopPadding())), verticalArrangement = Arrangement.Top) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

            LazyList(listItems, lazyListState, { viewModel.onItemClick(it) } )
        }
    }

    if(qttyDialogOpen) {
        QttyEditorAlertDialog(
            type = QttyEditorAlertDialogType.EDIT,
            selectedItem?.count.toString()!!,
            onNumberSelected = { i: Int, j: String ->  viewModel.onNumberSelected(i, j) },
            onDismissRequest = { viewModel.onDismissRequest() },
            listItem = selectedItem
        )
    }
    if(qttyAddDialogOpen) {
        QttyEditorAlertDialog(
            type = QttyEditorAlertDialogType.ADD,
            "1",
            onNumberSelected = { i: Int, j: String ->  viewModel.onNumberAdded(i, j) },
            onDismissRequest = { viewModel.qttyAddDialogOpen.value = false },
            listItem = selectedItem
        )
    }

    if(upcAddDialogOpen) {
        QttyEditorAlertDialog(
            type = QttyEditorAlertDialogType.ADD_UPC,
            initialValue = "1",
            onNumberSelected = { i: Int, j: String ->  viewModel.onNumberUpcAdded(i, j) },
            onDismissRequest = { viewModel.upcAddDialogOpen.value = false},
            showUpcField = true
        )
    }
    if(isCheckDialogOpen) {
        ListItemCheckAlertDialog(
            selectedItem,
            {
                viewModel.isCheckDialogOpen.value = false
                viewModel.selectedItem.value = null
            }
        )
    }


}

@Preview
@Composable
fun ListItemsScreenPreview() {
    ListItemsScreen({})
}