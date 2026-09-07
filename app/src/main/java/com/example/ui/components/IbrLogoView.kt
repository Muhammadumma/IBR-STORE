package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun IbrLogoView(
    size: Dp = 48.dp,
    showBorder: Boolean = true,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape((size.value * 0.25f).dp)
    val cyanColor = Color(0xFF00A8FF)
    val redColor = Color(0xFFFF1E42)
    val darkBg = Color(0xFF0D0E15)

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(darkBg)
            .then(
                if (showBorder) Modifier.border(1.dp, cyanColor.copy(alpha = 0.5f), shape)
                else Modifier
            )
            .padding((size.value * 0.08f).dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.85f)) {
            val w = this.size.width
            val h = this.size.height

            // Outer cyan orbit ring
            drawArc(
                color = cyanColor.copy(alpha = 0.7f),
                startAngle = 130f,
                sweepAngle = 280f,
                useCenter = false,
                topLeft = Offset(w * 0.05f, h * 0.1f),
                size = Size(w * 0.9f, h * 0.8f),
                style = Stroke(width = w * 0.04f)
            )

            // Main red swoosh ring
            drawArc(
                color = redColor,
                startAngle = -20f,
                sweepAngle = 300f,
                useCenter = false,
                topLeft = Offset(w * 0.12f, h * 0.15f),
                size = Size(w * 0.76f, h * 0.7f),
                style = Stroke(width = w * 0.08f)
            )
        }

        Text(
            text = "IBR",
            color = cyanColor,
            fontWeight = FontWeight.Black,
            fontSize = (size.value * 0.32f).sp,
            letterSpacing = (size.value * 0.02f).sp
        )
    }
}
