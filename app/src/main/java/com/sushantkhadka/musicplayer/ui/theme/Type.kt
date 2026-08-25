@file:OptIn(ExperimentalTextApi::class)

package com.sushantkhadka.musicplayer.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sushantkhadka.musicplayer.R

val AppFontFamily = FontFamily(
    Font(
        R.font.manrope,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400))
    ),
    Font(
        R.font.manrope,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(FontVariation.weight(500))
    ),
    Font(
        R.font.manrope,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(600))
    ),
    Font(
        R.font.manrope,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(FontVariation.weight(700))
    ),
    Font(
        R.font.manrope,
        weight = FontWeight.ExtraBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(800))
    ),
    Font(
        R.font.noto_sans_devanagari,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400))
    ),
    Font(
        R.font.noto_sans_devanagari,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(FontVariation.weight(500))
    ),
    Font(
        R.font.noto_sans_devanagari,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(600))
    ),
    Font(
        R.font.noto_sans_devanagari,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(FontVariation.weight(700))
    ),
    Font(
        R.font.noto_sans_devanagari,
        weight = FontWeight.ExtraBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(800))
    )
)

val AppTypography = Typography(
    headlineSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    titleMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
)
