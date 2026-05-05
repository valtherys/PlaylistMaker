package com.practicum.playlistmaker.ui.common.composable_items


import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun Placeholder(
    @DrawableRes imageRes: Int,
    placeholderText: String,
    @StringRes btnText: Int? = null,
    onClickAction: (() -> Unit) = { }
) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            modifier = Modifier
                .size(198.dp)
                .padding(top = 78.dp),
            painter = painterResource(
                imageRes
            ),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.widthIn(max = 280.dp),
            text = placeholderText,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary, textAlign = TextAlign.Center
        )
        btnText?.let {
            Spacer(modifier = Modifier.height(24.dp))
            ButtonItem(
                text = stringResource(it),
                onClickAction = onClickAction
            )
        }
    }
}
