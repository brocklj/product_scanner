package cz.broc.productscanner.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonElevation
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import cz.broc.productscanner.R
import cz.broc.productscanner.models.ListItem


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LazyList(
    items: List<ListItem>,
    listState: LazyListState,
    onItemClick: (item: ListItem) -> Unit
) {
    Box() {
        LazyColumn(modifier = Modifier.fillMaxWidth(), state = listState) {
            items(
            count = items.size,
            key = { it: Int -> items[it].ean},
            itemContent = {
            item: Int ->
                ListItemItem(items[item], onItemClick)
            })

        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListItemItem(listItem: ListItem, onItemClick: (item: ListItem) -> Unit, showUnits: Boolean = false) {
    val onClick = {
        onItemClick(listItem)
    }
    var backgroundColor = colorResource(R.color.colorWhite)
    val buttonColors = object : ButtonColors {
        @Composable
        override fun backgroundColor(enabled: Boolean): State<Color> {
            return rememberUpdatedState(backgroundColor)
        }

        @Composable
        override fun contentColor(enabled: Boolean): State<Color> {
            return rememberUpdatedState(Color.Black)
        }

    }
    ListItem(modifier = Modifier.fillMaxWidth(),
        text = {
            Column {
                Button(onClick,
                    colors = buttonColors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(Dp(0f))
                        .padding(Dp(0F)),
                    elevation = object : ButtonElevation {
                        @Composable
                        override fun elevation(
                            enabled: Boolean,
                            interactionSource: InteractionSource
                        ): State<Dp> {
                            return rememberUpdatedState(Dp(0f))
                        }
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.requiredWidthIn(max = Dp(170F))
                        ) {
                            Row(modifier = Modifier.padding(start = Dp(0f))) {
                                Text(listItem.name)
                            }
                            Row {
                                Text(listItem.upc)
                            }
                            Row {
                                Text(listItem.ean)
                            }
                        }
                        Box(modifier = Modifier.wrapContentWidth()) {
                            Row {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .background(colorResource(id = R.color.colorBlue50))
                                            .defaultMinSize(Dp(40f), Dp(40f))
                                            .padding(horizontal = Dp(8f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = listItem.count.toString() + if(showUnits) {" ${listItem.unit}"} else {""},
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Divider()
            }
        })
}