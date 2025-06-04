package cz.broc.productscanner.components

import cz.broc.productscanner.R

import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import cz.broc.productscanner.models.ListItem

import kotlinx.coroutines.delay

enum class QttyEditorAlertDialogType {
    ADD, EDIT, ADD_UPC
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun QttyEditorAlertDialog(
    type: QttyEditorAlertDialogType,
    initialValue: String,
    onNumberSelected: (Int, String) -> Unit,
    onDismissRequest: () -> Unit,
    showUpcField: Boolean = false,
    showCountField: Boolean = true,
    title: String = "",
    listItem: ListItem? = null
) {
    var numberText by remember { mutableStateOf(TextFieldValue(initialValue)) }
    var upcText by remember { mutableStateOf( "") }
    val number = numberText.text.toIntOrNull()
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val focusRequesterUpc = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        keyboard?.show()
        delay(100)
        if(showUpcField) {
            focusRequesterUpc.requestFocus()
        }else {
            focusRequester.requestFocus()
        }
    }
    val isFocused by interactionSource.collectIsFocusedAsState()
    LaunchedEffect(isFocused) {
        numberText = numberText.copy(
            selection = if (isFocused) {
                TextRange(
                    start = 0,
                    end = numberText.text.length
                )
            } else {
                TextRange.Zero
            }
        )
    }

    AlertDialog(
        modifier= Modifier.padding(0.dp),
        onDismissRequest = onDismissRequest,
        title = {
            Column {
                if(title.isNotEmpty()) {
                    Text(title)
                }
                if(listItem != null) {
                    Box(Modifier.padding(bottom = 1.dp)) {
                        ListItemItem(listItem, {})
                    }
                }
            }
        },
        text = {
            Column {
                if(showUpcField) {
                    Box(Modifier.padding(bottom = 20.dp)) {
                        OutlinedTextField(
                            textStyle = TextStyle(fontSize = TextUnit(3f, TextUnitType.Em)),
                            maxLines = 1,
                            value = upcText,
                            onValueChange = { upcText = it },
                            label = { Text(stringResource(id = R.string.ean)) },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusable(true).focusRequester(focusRequesterUpc)
                        )
                    }
                }
                if(showCountField) {
                    OutlinedTextField(
                        textStyle = TextStyle(fontSize = TextUnit(3f, TextUnitType.Em)),
                        maxLines = 1,
                        value = numberText,
                        onValueChange = { numberText = it },
                        label = {
                            Text(
                                when (type) {
                                    QttyEditorAlertDialogType.ADD -> {
                                        stringResource(id = R.string.add_qtty)
                                    }

                                    QttyEditorAlertDialogType.ADD_UPC -> {
                                        stringResource(id = R.string.add_qtty)
                                    }

                                    QttyEditorAlertDialogType.EDIT -> {
                                        stringResource(id = R.string.change_qtty)
                                    }

                                    else -> {
                                        ""
                                    }
                                } +
                                if(!listItem?.unit.isNullOrEmpty()) {
                                    " ("+ listItem?.unit + ")"
                                } else {
                                    ""
                                }
                            )
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusable(true).focusRequester(focusRequester),
                        interactionSource = interactionSource
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (number != null) {
                        onNumberSelected(number, upcText)
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(id = R.string.cancel))
            }
        },
        //contentPadding = PaddingValues(16.dp)
    )
}




@Preview
@Composable
fun QttyEditorAlertDialogPreview() {
    QttyEditorAlertDialog(QttyEditorAlertDialogType.ADD,"", {i,s -> }, {}, false)
}




