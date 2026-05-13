package com.inspiredandroid.braincup

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd

object NativeAdRepository {

    var cachedAd by mutableStateOf<NativeAd?>(null)
        private set

    private var loading = false

    fun preload(context: Context, adUnitId: String) {
        if (cachedAd != null || loading) return
        loading = true
        AdLoader.Builder(context.applicationContext, adUnitId)
            .forNativeAd { ad ->
                Log.d("NativeAdRepository", "Preloaded ad: ${ad.headline}")
                cachedAd?.destroy()
                cachedAd = ad
                loading = false
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.w(
                        "NativeAdRepository",
                        "Preload failed: code=${error.code} domain=${error.domain} message=${error.message}",
                    )
                    loading = false
                }
            })
            .build()
            .loadAd(AdRequest.Builder().build())
    }

    fun consume(): NativeAd? {
        val ad = cachedAd
        cachedAd = null
        return ad
    }
}
