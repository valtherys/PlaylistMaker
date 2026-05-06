package com.practicum.playlistmaker.ui.common.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.ui.theme.Blue

@Composable
fun Loader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 116.dp), contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Blue
        )
    }
}
