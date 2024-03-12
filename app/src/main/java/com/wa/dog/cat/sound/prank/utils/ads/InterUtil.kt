package com.wa.dog.cat.sound.prank.utils.ads

import android.content.Context
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterUtil private constructor() {
    companion object {
        val instance: InterUtil by lazy { InterUtil() }
        var interSplash: InterstitialAd? = null
    }

    var onInterLoadSuccess: (InterstitialAd) -> Unit = {}
    var onInterLoadFailed: () -> Unit = {}

    fun loadInterAds(context: Context, keyAds: String) {
        InterstitialAd.load(
            context,
            keyAds,
            getAdsRequest(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    onInterLoadFailed()
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    super.onAdLoaded(interstitialAd)
                    onInterLoadSuccess(interstitialAd)
                }
            })
    }
}