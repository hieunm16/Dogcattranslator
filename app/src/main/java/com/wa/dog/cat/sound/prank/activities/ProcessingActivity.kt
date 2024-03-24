package com.wa.dog.cat.sound.prank.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdapterResponseInfo
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.dog.cat.sound.prank.R
import com.wa.dog.cat.sound.prank.databinding.ActivityProcessingBinding
import com.wa.dog.cat.sound.prank.databinding.AdNativeVideoBinding
import com.wa.dog.cat.sound.prank.extension.gone
import com.wa.dog.cat.sound.prank.extension.setFullScreen
import com.wa.dog.cat.sound.prank.extension.setStatusBarColor
import com.wa.dog.cat.sound.prank.utils.RemoteConfigHelper
import com.wa.dog.cat.sound.prank.utils.RemoteConfigKey
import com.wa.dog.cat.sound.prank.utils.ads.NativeAdsUtils
import com.wa.dog.cat.sound.prank.utils.device.DeviceUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ProcessingActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProcessingBinding
    private var mInterstitialAd: InterstitialAd? = null
    private  var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var analytics : FirebaseAnalytics? = null


    private val remoteConfigHelper: RemoteConfigHelper by lazy { RemoteConfigHelper() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProcessingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setStatusBarColor("#ffffff")
        remoteConfigHelper.loadConfig()
        initControl()
    }

    private  fun initControl(){
        showButtonResult()
        showAds()
    }

    override fun onStart() {
        super.onStart()
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.SHOW_ADS_NATIVE_PROCESS)) {
            val adConfig = FirebaseRemoteConfig.getInstance()
                .getString(RemoteConfigKey.KEY_SHOW_ADS_NATIVE_PROCESS)
            if (adConfig.isNotEmpty()) {
                loadNativeAds(adConfig)
            } else {
                loadNativeAds(getString(R.string.native_process))

            }
        }

        if (FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.SHOW_ADS_INTER_PROCESS)
        ) {
            val adConfig = FirebaseRemoteConfig.getInstance()
                .getString(RemoteConfigKey.KEY_SHOW_ADS_INTER_PROCESS)
            if (adConfig.isNotEmpty()) {
                loadInterAds(adConfig)
            } else {
                loadInterAds(getString(R.string.inter_anim_process))
            }
        }
    }

    private fun showButtonResult(){
        lifecycleScope.launch {
            delay(7000)
            binding.btnResult.visibility = View.VISIBLE
        }
    }

    private fun goToResult(){
        val intent = Intent (this, ResultActivity::class.java)
        startActivity(intent)
    }


    private fun loadInterAds(keyAdsInter: String) {
        InterstitialAd.load(
            this,
            keyAdsInter,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    mFirebaseAnalytics?.logEvent("d_load_inter_ads_process_screen", null)

                    mInterstitialAd?.onPaidEventListener =
                        OnPaidEventListener { adValue -> // Lấy thông tin về nhà cung cấp quảng cáo
                            val loadedAdapterResponseInfo : AdapterResponseInfo? =
                                interstitialAd.responseInfo.loadedAdapterResponseInfo
                            // Gửi thông tin doanh thu quảng cáo đến Adjust
                            val adRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB)
                            val revenue = adValue.valueMicros.toDouble() / 1000000.0
                            adRevenue.setRevenue(
                                revenue,
                                adValue.currencyCode
                            )
                            adRevenue.setAdRevenueNetwork(loadedAdapterResponseInfo?.adSourceName)
                            Adjust.trackAdRevenue(adRevenue)
                            analytics = FirebaseAnalytics.getInstance(applicationContext)
                            val params = Bundle()
                            params.putString(FirebaseAnalytics.Param.AD_PLATFORM, "admob mediation")
                            params.putString(FirebaseAnalytics.Param.AD_SOURCE, "AdMob")
                            params.putString(FirebaseAnalytics.Param.AD_FORMAT, "Interstitial")
                            params.putDouble(FirebaseAnalytics.Param.VALUE, revenue )
                            params.putString(FirebaseAnalytics.Param.CURRENCY, "USD")
                            analytics?.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, params)
                        }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    mInterstitialAd = null
                    mFirebaseAnalytics?.logEvent("e_load_inter_ads_process_screen", null)

                }
            })
    }

    private fun showAds() {
        binding.btnResult.setOnClickListener {
            val intent = Intent(this, ResultActivity::class.java)
            if (mInterstitialAd != null) {
                // Nếu quảng cáo đã tải xong, hiển thị quảng cáo và chuyển đến Activity mới sau khi quảng cáo kết thúc
                mInterstitialAd?.fullScreenContentCallback =
                    object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            startActivity(intent)
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            startActivity(intent)
                        }
                    }
                mInterstitialAd?.show(this)

                mFirebaseAnalytics?.logEvent("v_inter_ads_result_screen", null)
            } else {
                // Nếu quảng cáo chưa tải xong, chuyển đến Activity mới ngay lập tức
                startActivity(intent)
            }
        }
    }





    private fun loadNativeAds(keyAds:String) {
        if (!DeviceUtils.checkInternetConnection(this@ProcessingActivity)) binding.rlNative.gone()
            NativeAdsUtils.instance.loadNativeAds(
                this@ProcessingActivity,
                keyAds
            ) { nativeAds ->
                if (nativeAds != null) {
                    binding.frNativeAds.removeAllViews()
                    val adNativeVideoBinding = AdNativeVideoBinding.inflate(layoutInflater)
                    NativeAdsUtils.instance.populateNativeAdVideoView(
                        nativeAds,
                        adNativeVideoBinding.root
                    )
                    binding.frNativeAds.addView(adNativeVideoBinding.root)
                } else {
                    binding.rlNative.gone()
                }
            }

    }

}