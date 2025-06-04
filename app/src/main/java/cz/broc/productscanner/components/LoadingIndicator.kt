package cz.broc.productscanner.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun LoadingIndicator(progress: Int? = null, text: String? = null) {
    val floatProgress = if (progress != null) {
        progress.toFloat() / 100
    } else null

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()){
            if (floatProgress != null && floatProgress > 0) {

                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f), progress = floatProgress
                )

            } else {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text ?: "Načítání", textAlign = TextAlign.Center)
            (progress?.takeIf { it > 0 })?.let {
                Text("$it %", textAlign = TextAlign.Center)
            }

        }

    }

}
@Preview
@Composable
fun LoadingIndicatorPreview() {
    LoadingIndicator()
}