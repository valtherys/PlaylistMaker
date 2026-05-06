package com.practicum.playlistmaker.ui.search.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.ui.theme.AppFontFamily

@Composable
fun CustomTextField(
    text: String,
    onTextChange: (String) -> Unit,
    onClear: () -> Unit,
    onFocusAction: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = isFocused, key2 = text) {
        if (isFocused && text.isEmpty()) {
            onFocusAction()
        }
    }

    Row(
        modifier = Modifier
            .height(36.dp)
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(8.dp)
            )
            .background(color = MaterialTheme.colorScheme.background)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            modifier = Modifier.size(16.dp), contentDescription = null,
            painter = painterResource(R.drawable.ic_search_24),
            tint = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.width(8.dp))
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            singleLine = true,
            modifier = Modifier
                .weight(1F)
                .onFocusChanged {
                    isFocused = it.isFocused
                },
            textStyle = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                fontFamily = AppFontFamily,
                letterSpacing = 0.sp
            ),
            decorationBox = { innerTextField ->
                Box {
                    if (text.isEmpty()) {
                        Text(
                            text = stringResource(R.string.search),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    innerTextField()
                }
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        if (text.isNotEmpty()) {
            Icon(
                modifier = Modifier
                    .size(32.dp)
                    .padding(8.dp)
                    .clickable(
                        enabled = true,
                        onClick = {
                            onClear()
                            onTextChange("")
                        }
                    ),
                contentDescription = null,
                painter = painterResource(R.drawable.ic_close_24),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

