package com.inspiredandroid.braincup.screenshots

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * iOS status bar and home indicator for the App Store screenshots.
 *
 * Paparazzi renders the shared Compose UI with no platform decoration, so this supplies both the
 * safe areas and the chrome iOS draws into them. Every number is a point measurement taken off
 * `xcrun simctl io screenshot` on iPhone 16 Pro Max (440x956pt) and iPad Pro 13" (1032x1376pt),
 * with the status bar overridden to 9:41, full signal and a charged battery. The screenshot tests
 * render at exactly those point sizes, so the measurements are used as absolute dp coordinates.
 *
 * A screenshot taken on a Dynamic Island iPhone contains the island as a solid black pill, so it
 * is drawn here too. The clock coordinates carry a sub-point nudge because the fallback sans-serif
 * has different side bearings than SF Pro; they were calibrated by re-measuring the rendered PNGs
 * against the simulator reference.
 */
internal enum class IosDevice(
    val statusBarHeight: Dp,
    val homeIndicatorAreaHeight: Dp,
    val homeIndicatorWidth: Dp,
    val homeIndicatorBottomMargin: Dp,
) {
    IPHONE(
        statusBarHeight = 62.dp,
        homeIndicatorAreaHeight = 34.dp,
        homeIndicatorWidth = 140.dp,
        homeIndicatorBottomMargin = 8.dp,
    ),
    IPAD(
        statusBarHeight = 24.dp,
        homeIndicatorAreaHeight = 20.dp,
        homeIndicatorWidth = 315.dp,
        homeIndicatorBottomMargin = 7.5.dp,
    ),
}

@Composable
internal fun IosDeviceChrome(
    device: IosDevice,
    content: @Composable () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        IosStatusBar(device)
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            content()
        }
        IosHomeIndicator(device)
    }
}

@Composable
private fun statusBarTint(): Color =
    if (MaterialTheme.colorScheme.surface.luminance() < 0.5f) Color.White else Color.Black

@Composable
private fun IosStatusBar(device: IosDevice) {
    val tint = statusBarTint()
    val textMeasurer = rememberTextMeasurer()
    Canvas(modifier = Modifier.fillMaxWidth().height(device.statusBarHeight)) {
        when (device) {
            IosDevice.IPHONE -> drawIphoneStatusBar(tint, textMeasurer)
            IosDevice.IPAD -> drawIpadStatusBar(tint, textMeasurer)
        }
    }
}

@Composable
private fun IosHomeIndicator(device: IosDevice) {
    val tint = statusBarTint()
    Box(
        modifier = Modifier.fillMaxWidth().height(device.homeIndicatorAreaHeight),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = device.homeIndicatorBottomMargin)
                .width(device.homeIndicatorWidth)
                .height(5.dp)
                .clip(RoundedCornerShape(50))
                .background(tint.copy(alpha = 0.6f)),
        )
    }
}

private fun DrawScope.drawIphoneStatusBar(
    tint: Color,
    textMeasurer: TextMeasurer,
) {
    drawRoundRect(
        color = Color.Black,
        topLeft = Offset(pt(157.33f), pt(14f)),
        size = Size(pt(125.33f), pt(36.67f)),
        cornerRadius = CornerRadius(pt(18.33f)),
    )
    drawStatusText(
        textMeasurer = textMeasurer,
        text = "9:41",
        fontSize = 18.3f,
        color = tint,
        baselineY = 39f,
        centerX = 84f,
    )
    drawCellularBars(
        color = tint,
        firstBarLeft = 312.33f,
        barWidth = 3.67f,
        barPitch = 6f,
        bottomY = 39f,
        barHeights = floatArrayOf(5.33f, 7.67f, 10.67f, 13.67f),
    )
    drawWifi(
        color = tint,
        apexX = 351.17f,
        apexY = 39f,
        outerRadius = 12.41f,
        outerStroke = 2.84f,
        middleRadius = 7.65f,
        middleStroke = 2.83f,
        wedgeRadius = 4.31f,
    )
    drawBattery(
        color = tint,
        left = 368.33f,
        top = 25f,
        bodyWidth = 28.33f,
        bodyHeight = 14.33f,
        stroke = 1.17f,
        bodyRadius = 4.3f,
        fillInset = 2.33f,
        fillRadius = 2.67f,
        nubGap = 1f,
        nubWidth = 1.67f,
        nubHeight = 5f,
    )
}

private fun DrawScope.drawIpadStatusBar(
    tint: Color,
    textMeasurer: TextMeasurer,
) {
    drawStatusText(
        textMeasurer = textMeasurer,
        text = "9:41 AM",
        fontSize = 12.1f,
        color = tint,
        baselineY = 16f,
        leftX = 25f,
    )
    drawStatusText(
        textMeasurer = textMeasurer,
        text = "Tue Jan 9",
        fontSize = 12.1f,
        color = tint,
        baselineY = 16f,
        leftX = 81f,
    )
    drawWifi(
        color = tint,
        apexX = 968.5f,
        apexY = 17f,
        outerRadius = 8.99f,
        outerStroke = 2f,
        middleRadius = 5.49f,
        middleStroke = 2f,
        wedgeRadius = 2.97f,
    )
    drawBattery(
        color = tint,
        left = 979.5f,
        top = 6f,
        bodyWidth = 24f,
        bodyHeight = 12f,
        stroke = 1f,
        bodyRadius = 3.6f,
        fillInset = 2f,
        fillRadius = 2f,
        nubGap = 1f,
        nubWidth = 1.16f,
        nubHeight = 4f,
    )
}

/**
 * Placed by baseline instead of by layout box, so the glyphs land on the measured coordinates
 * whatever ascent the fallback font reports.
 */
private fun DrawScope.drawStatusText(
    textMeasurer: TextMeasurer,
    text: String,
    fontSize: Float,
    color: Color,
    baselineY: Float,
    centerX: Float? = null,
    leftX: Float? = null,
) {
    val layout = textMeasurer.measure(
        text = AnnotatedString(text),
        style = TextStyle(
            color = color,
            fontSize = fontSize.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
        ),
    )
    val x = if (centerX != null) pt(centerX) - layout.size.width / 2f else pt(leftX ?: 0f)
    drawText(textLayoutResult = layout, topLeft = Offset(x, pt(baselineY) - layout.firstBaseline))
}

private fun DrawScope.drawCellularBars(
    color: Color,
    firstBarLeft: Float,
    barWidth: Float,
    barPitch: Float,
    bottomY: Float,
    barHeights: FloatArray,
) {
    val corner = CornerRadius(pt(1.2f))
    barHeights.forEachIndexed { index, barHeight ->
        drawRoundRect(
            color = color,
            topLeft = Offset(pt(firstBarLeft + index * barPitch), pt(bottomY - barHeight)),
            size = Size(pt(barWidth), pt(barHeight)),
            cornerRadius = corner,
        )
    }
}

/**
 * Two annular sectors and a filled wedge, all centred on the glyph's bottom apex and spanning 90
 * degrees. That is how the iOS glyph is built: the ends are radial cuts, not round caps.
 */
private fun DrawScope.drawWifi(
    color: Color,
    apexX: Float,
    apexY: Float,
    outerRadius: Float,
    outerStroke: Float,
    middleRadius: Float,
    middleStroke: Float,
    wedgeRadius: Float,
) {
    fun sector(
        radius: Float,
        stroke: Float?,
    ) {
        val r = pt(radius)
        drawArc(
            color = color,
            startAngle = 225f,
            sweepAngle = 90f,
            useCenter = stroke == null,
            topLeft = Offset(pt(apexX) - r, pt(apexY) - r),
            size = Size(r * 2f, r * 2f),
            style = if (stroke == null) Fill else Stroke(width = pt(stroke)),
        )
    }
    sector(outerRadius, outerStroke)
    sector(middleRadius, middleStroke)
    sector(wedgeRadius, null)
}

private fun DrawScope.drawBattery(
    color: Color,
    left: Float,
    top: Float,
    bodyWidth: Float,
    bodyHeight: Float,
    stroke: Float,
    bodyRadius: Float,
    fillInset: Float,
    fillRadius: Float,
    nubGap: Float,
    nubWidth: Float,
    nubHeight: Float,
) {
    val outline = color.copy(alpha = 0.4f)
    val terminal = color.copy(alpha = 0.5f)
    drawRoundRect(
        color = outline,
        topLeft = Offset(pt(left + stroke / 2f), pt(top + stroke / 2f)),
        size = Size(pt(bodyWidth - stroke), pt(bodyHeight - stroke)),
        cornerRadius = CornerRadius(pt(bodyRadius)),
        style = Stroke(width = pt(stroke)),
    )
    drawRoundRect(
        color = color,
        topLeft = Offset(pt(left + fillInset), pt(top + fillInset)),
        size = Size(pt(bodyWidth - fillInset * 2f), pt(bodyHeight - fillInset * 2f)),
        cornerRadius = CornerRadius(pt(fillRadius)),
    )
    drawRoundRect(
        color = terminal,
        topLeft = Offset(pt(left + bodyWidth + nubGap), pt(top + (bodyHeight - nubHeight) / 2f)),
        size = Size(pt(nubWidth), pt(nubHeight)),
        cornerRadius = CornerRadius(pt(nubWidth / 2f)),
    )
}

private fun DrawScope.pt(value: Float): Float = value.dp.toPx()
