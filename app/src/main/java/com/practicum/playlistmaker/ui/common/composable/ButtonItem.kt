package com.practicum.playlistmaker.ui.common.composable

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.ui.theme.AppTheme

@Composable
fun ButtonItem(
    text: String,
    onClickAction: () -> Unit
) {
    Button(
        modifier = Modifier.height(36.dp),
        onClick = onClickAction,
        shape = RoundedCornerShape(54.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onPrimary,
        )
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Preview(showSystemUi = false, showBackground = true)
@Composable
private fun ButtonItemPreview() {
    AppTheme {
        ButtonItem(
            text = stringResource(R.string.renew), {}
        )
    }
}