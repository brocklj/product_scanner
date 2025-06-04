package cz.broc.productscanner.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.broc.productscanner.ListActivity
import cz.broc.productscanner.R

@Composable
fun ListTopBarComponent(onSelectCsvClick: ()->Unit, onExportClick: ()-> Unit) {
    val viewModel = (LocalContext.current as ListActivity).viewModel
    var showMenu by remember { mutableStateOf(false) }
    val userIdDialogOpen by viewModel.userIdDialogOpen.collectAsState()
    val userId by viewModel.userId.collectAsState()
    val isQttyEditEnabled by viewModel.isQttyEditEnabled.collectAsState()



    if(userIdDialogOpen) {
        QttyEditorAlertDialog(
            QttyEditorAlertDialogType.EDIT,
            userId,
            {i: Int, j:String -> viewModel.setUserId(i) },
            {viewModel.userIdDialogOpen.value = false},
            title = stringResource(id = R.string.change_user_id)
        )
    }

    TopAppBar(
        title = { Text(stringResource(id = R.string.telomar_scanner)) },
        actions = {
            Button(modifier = Modifier.shadow(0.dp), onClick = { viewModel.userIdDialogOpen.value = true  }) {
                Text(text = "ID:${if(userId.isEmpty()){"ⁿ/ₐ"}else{userId}}" )
            }
            IconButton(onClick = {
                showMenu = !showMenu
            }) {
                Icon(Icons.Default.MoreVert, "More")
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
            DropdownMenuItem(onClick = {
                    viewModel.onCheckTopBarMenuItemClick()
                    showMenu = false
            }) {
                    Text(text = stringResource(id = R.string.app_bar_check))
            }
            DropdownMenuItem(onClick = {
                onExportClick()
                showMenu = false
            }) {
                Text(text = stringResource(id = R.string.export_list))
            }
            DropdownMenuItem({ 
                viewModel.onCheckTopBarMenuItemClick()
                showMenu = false
            }) {
                Text(text =  stringResource(id = R.string.select_csv_btn))
            }
                DropdownMenuItem(onClick = { viewModel.isQttyEditEnabled.value = !isQttyEditEnabled }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = stringResource(id = R.string.auto_edit_qtty))
                        Checkbox(
                            checked = isQttyEditEnabled!!,
                            onCheckedChange = { viewModel.isQttyEditEnabled.value = !isQttyEditEnabled }
                        )
                    }
                }
                DropdownMenuItem({}, enabled = false) {
                    Text(text =  "${stringResource(id = R.string.version)}: ${stringResource(id = R.string.app_version)}")
                }
            }
        }
    )
}

@Preview
@Composable
fun TopBarComponentReview() {
    ListTopBarComponent({}, {})
}