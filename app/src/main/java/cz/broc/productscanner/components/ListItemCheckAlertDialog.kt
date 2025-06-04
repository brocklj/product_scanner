package cz.broc.productscanner.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import cz.broc.productscanner.models.ListItem
import cz.broc.productscanner.R
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ListItemCheckAlertDialog(listItem: ListItem?, onDismissRequest:()-> Unit) {
    AlertDialog(
        title = {
        },
        text = {
                Column {
                    Row {
                        Box(contentAlignment = Alignment.Center) {
                            if(listItem != null) {
                                ListItemItem(listItem, {}, showUnits = true)
                            } else {
                                Column {
                                    Text(stringResource(R.string.app_bar_check))
                                    Text(text="Naskenujte EAN", textAlign = TextAlign.Center)
                                }
                            }
                        }

                    }
                }
        },
        onDismissRequest = { onDismissRequest() },
        confirmButton = {Button(onClick = {onDismissRequest()}, content = { Text(stringResource(R.string.close)) } ) }
    )
}