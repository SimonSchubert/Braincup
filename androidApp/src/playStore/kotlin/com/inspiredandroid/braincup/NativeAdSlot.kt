package com.inspiredandroid.braincup

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.inspiredandroid.braincup.app.R

fun initMobileAds(context: android.content.Context) {
    MobileAds.initialize(context) {
        // Preload the first native ad as soon as the SDK is ready.
        NativeAdRepository.preload(
            context.applicationContext,
            context.getString(R.string.admob_native_ad_unit_id),
        )
    }
}

@Composable
fun NativeAdSlot(modifier: Modifier) {
    val context = LocalContext.current
    val adUnitId = stringResource(R.string.admob_native_ad_unit_id)
    var displayed by remember { mutableStateOf<NativeAd?>(null) }
    val cached = NativeAdRepository.cachedAd

    DisposableEffect(adUnitId) {
        // Claim any preloaded ad immediately, then ensure the pipeline keeps loading.
        if (displayed == null) {
            displayed = NativeAdRepository.consume()
        }
        NativeAdRepository.preload(context.applicationContext, adUnitId)
        onDispose {
            displayed?.destroy()
            displayed = null
        }
    }

    // If nothing was cached on entry, claim the next ad as soon as it arrives.
    LaunchedEffect(cached) {
        if (displayed == null && cached != null) {
            displayed = NativeAdRepository.consume()
            NativeAdRepository.preload(context.applicationContext, adUnitId)
        }
    }

    val ad = displayed ?: return
    val onSurface = MaterialTheme.colorScheme.onSurface.toArgb()
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    val primary = MaterialTheme.colorScheme.primary.toArgb()
    val onPrimary = MaterialTheme.colorScheme.onPrimary.toArgb()
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { ctx -> buildNativeAdView(ctx, onSurface, onSurfaceVariant, primary, onPrimary) },
            update = { adView -> bindNativeAd(adView, ad) },
        )
    }
}

private fun buildNativeAdView(
    context: Context,
    onSurface: Int,
    onSurfaceVariant: Int,
    primary: Int,
    onPrimary: Int,
): NativeAdView {
    val pad = context.dp(12)

    val adView = NativeAdView(context).apply {
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    }

    val root = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(pad, pad, pad, pad)
        layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    }

    val headerRow = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
    }

    val iconView = ImageView(context).apply {
        id = View.generateViewId()
        layoutParams = LinearLayout.LayoutParams(context.dp(40), context.dp(40)).apply {
            marginEnd = context.dp(8)
        }
    }

    val headlineCol = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        layoutParams = LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f)
    }

    val headlineView = TextView(context).apply {
        id = View.generateViewId()
        setTextColor(onSurface)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        setTypeface(typeface, Typeface.BOLD)
        maxLines = 2
        ellipsize = TextUtils.TruncateAt.END
    }
    val advertiserView = TextView(context).apply {
        id = View.generateViewId()
        setTextColor(onSurfaceVariant)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END
    }
    headlineCol.addView(headlineView)
    headlineCol.addView(advertiserView)

    val adBadge = TextView(context).apply {
        text = "Ad"
        setTextColor(onPrimary)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
        setTypeface(typeface, Typeface.BOLD)
        setPadding(context.dp(6), context.dp(2), context.dp(6), context.dp(2))
        background = roundedBackground(primary, context.dp(4).toFloat())
    }

    headerRow.addView(iconView)
    headerRow.addView(headlineCol)
    headerRow.addView(adBadge)

    val bodyView = TextView(context).apply {
        id = View.generateViewId()
        setTextColor(onSurfaceVariant)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
        maxLines = 2
        ellipsize = TextUtils.TruncateAt.END
        layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            topMargin = context.dp(8)
        }
    }

    val mediaView = MediaView(context).apply {
        id = View.generateViewId()
        setImageScaleType(ScaleType.CENTER_CROP)
        layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            topMargin = context.dp(8)
        }
    }

    val ctaView = Button(context).apply {
        id = View.generateViewId()
        setTextColor(onPrimary)
        background = roundedBackground(primary, context.dp(8).toFloat())
        isAllCaps = false
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            topMargin = context.dp(8)
        }
    }

    root.addView(headerRow)
    root.addView(bodyView)
    root.addView(mediaView)
    root.addView(ctaView)
    adView.addView(root)

    adView.iconView = iconView
    adView.headlineView = headlineView
    adView.advertiserView = advertiserView
    adView.bodyView = bodyView
    adView.mediaView = mediaView
    adView.callToActionView = ctaView

    return adView
}

private fun bindNativeAd(adView: NativeAdView, ad: NativeAd) {
    (adView.headlineView as TextView).text = ad.headline.orEmpty()

    (adView.bodyView as TextView).apply {
        val body = ad.body
        if (body.isNullOrBlank()) {
            visibility = View.GONE
        } else {
            visibility = View.VISIBLE
            text = body
        }
    }

    (adView.callToActionView as Button).apply {
        val cta = ad.callToAction
        if (cta.isNullOrBlank()) {
            visibility = View.GONE
        } else {
            visibility = View.VISIBLE
            text = cta
        }
    }

    (adView.iconView as ImageView).apply {
        val icon = ad.icon
        if (icon?.drawable != null) {
            visibility = View.VISIBLE
            setImageDrawable(icon.drawable)
        } else {
            visibility = View.GONE
        }
    }

    (adView.advertiserView as TextView).apply {
        val name = ad.advertiser ?: ad.store
        if (name.isNullOrBlank()) {
            visibility = View.GONE
        } else {
            visibility = View.VISIBLE
            text = name
        }
    }

    (adView.mediaView as MediaView).apply {
        val content = ad.mediaContent
        if (content != null) {
            visibility = View.VISIBLE
            mediaContent = content
            val maxHeightPx = context.dp(150)
            val aspectRatio = content.aspectRatio.takeIf { it > 0f }
            val params = layoutParams
            params.height = if (aspectRatio != null) {
                // Sized at layout time: width / aspectRatio, capped at maxHeightPx.
                val widthPx = (adView.width.takeIf { it > 0 } ?: resources.displayMetrics.widthPixels)
                (widthPx / aspectRatio).toInt().coerceAtMost(maxHeightPx)
            } else {
                maxHeightPx
            }
            layoutParams = params
        } else {
            visibility = View.GONE
        }
    }

    adView.setNativeAd(ad)
}

private fun Context.dp(value: Int): Int =
    (value * resources.displayMetrics.density).toInt()

private fun roundedBackground(color: Int, radius: Float): GradientDrawable =
    GradientDrawable().apply {
        setColor(color)
        cornerRadius = radius
    }
