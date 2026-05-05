package com.practicum.playlistmaker.ui.theme


import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R

val AppFontFamily = FontFamily(
    Font(R.font.ys_display_bold, FontWeight.Bold),
    Font(R.font.ys_display_medium, FontWeight.Medium),
    Font(R.font.ys_display_regular, FontWeight.Normal)
)
val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontSize = 22.sp,
        fontFamily = AppFontFamily,
        letterSpacing = 0.sp,
        fontWeight = FontWeight.Medium
    ),
    titleLarge = TextStyle(
        fontSize = 19.sp,
        fontFamily = AppFontFamily,
        letterSpacing = 0.sp,
        fontWeight = FontWeight.Medium
    ),
    titleMedium = TextStyle(
        fontSize = 19.sp,
        fontFamily = AppFontFamily,
        letterSpacing = 0.sp,
        fontWeight = FontWeight.Normal
    ),
    titleSmall = TextStyle(
        fontSize = 11.sp,
        fontFamily = AppFontFamily,
        letterSpacing = 0.sp,
        fontWeight = FontWeight.Normal
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        fontFamily = AppFontFamily,
        letterSpacing = 0.sp,
        fontWeight = FontWeight.Normal
    ),
    bodyMedium = TextStyle(
        fontSize = 12.sp,
        fontFamily = AppFontFamily,
        letterSpacing = 0.sp,
        fontWeight = FontWeight.Normal
    ),
    labelLarge = TextStyle(
        fontSize = 14.sp,
        fontFamily = AppFontFamily,
        letterSpacing = 0.sp,
        fontWeight = FontWeight.Medium
    ),

    )