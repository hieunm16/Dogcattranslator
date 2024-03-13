package com.wa.dog.cat.sound.prank.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdapterResponseInfo
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.dog.cat.sound.prank.R
import com.wa.dog.cat.sound.prank.databinding.ActivityHomeBinding
import com.wa.dog.cat.sound.prank.extension.gone
import com.wa.dog.cat.sound.prank.extension.setFullScreen
import com.wa.dog.cat.sound.prank.utils.NotificationWorker
import com.wa.dog.cat.sound.prank.utils.RemoteConfigHelper
import com.wa.dog.cat.sound.prank.utils.RemoteConfigKey
import com.wa.dog.cat.sound.prank.utils.ads.BannerUtils
import com.wa.dog.cat.sound.prank.utils.device.DeviceUtils
import java.util.concurrent.TimeUnit


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private var mInterstitialAd: InterstitialAd? = null
    private var analytics : FirebaseAnalytics? = null

    private  var mFirebaseAnalytics: FirebaseAnalytics? = null



    companion object {
        var animal = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullScreen()
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        analytics =  Firebase.analytics
        MobileAds.initialize(this)
        initNotificationWorker()
        initControl()

    }

    private fun initNotificationWorker() {
        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(true)
            .build()

        val myRequest =
            PeriodicWorkRequest.Builder(NotificationWorker::class.java, 12, TimeUnit.HOURS)
                .setConstraints(constraints).build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork("my_id", ExistingPeriodicWorkPolicy.KEEP, myRequest)
    }

    private fun initControl() {
        binding.buttonDog.setOnClickListener {
            animal = "dog"
            goToOverviewActivity()
        }

        binding.buttonCat.setOnClickListener {
            animal = "cat"
            goToOverviewActivity()
        }

        binding.buttonHamster.setOnClickListener {
            animal = "hamster"
            goToOverviewActivity()
        }
    }

    private fun goToOverviewActivity() {
        val intent = Intent(this, OverviewActivity::class.java)
        // Kiểm tra xem quảng cáo đã tải xong chưa
        if (mInterstitialAd != null) {
            // Nếu quảng cáo đã tải xong, hiển thị quảng cáo và chuyển đến Activity mới sau khi quảng cáo kết thúc
            mInterstitialAd?.fullScreenContentCallback =
                object : com.google.android.gms.ads.FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        startActivity(intent)
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                        startActivity(intent)
                    }
                }
            mInterstitialAd?.show(this)

            mFirebaseAnalytics?.logEvent("v_inter_ads_home_screen", null)
        } else {
            // Nếu quảng cáo chưa tải xong, chuyển đến Activity mới ngay lập tức
            startActivity(intent)
        }
    }



    override fun onStart() {
        super.onStart()
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.SHOW_ADS_BANNER_HOME)) {
            val adConfig = FirebaseRemoteConfig.getInstance()
                .getString(RemoteConfigKey.KEY_SHOW_ADS_BANNER_HOME)
            if (adConfig.isNotEmpty()) {
                loadBanner(adConfig)
            } else {
                loadBanner(getString(R.string.collap_banner_anim_home))
            }
        }

        if (FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.SHOW_ADS_INTER_OVERVIEW)
        ) {
            val adConfig = FirebaseRemoteConfig.getInstance()
                .getString(RemoteConfigKey.KEY_SHOW_ADS_INTER_OVERVIEW)
            if (adConfig.isNotEmpty()) {
                loadInterAds(adConfig)
            } else {
                loadInterAds(getString(R.string.inter_anim_overview))
            }
        }
    }


    override fun onBackPressed() {
    }

    override fun onResume() {
        super.onResume()
        Adjust.onResume()
    }

    override fun onPause() {
        super.onPause()
        Adjust.onPause()
    }


    private fun loadBanner(keyAds: String) {
        if (!DeviceUtils.checkInternetConnection(this)) binding.rlBanner.gone()
        BannerUtils.instance?.loadCollapsibleBanner(this, keyAds)
    }


    private fun loadInterAds(keyAdsInter: String) {
        InterstitialAd.load(
            this,
            keyAdsInter,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    mFirebaseAnalytics?.logEvent("d_load_inter_ads_home_screen", null)

                    mInterstitialAd?.onPaidEventListener =
                        OnPaidEventListener { adValue -> // Lấy thông tin về nhà cung cấp quảng cáo
                            val loadedAdapterResponseInfo : AdapterResponseInfo? =
                                interstitialAd?.responseInfo?.loadedAdapterResponseInfo
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
                    mFirebaseAnalytics?.logEvent("e_load_inter_ads_home_screen", null)

                }
            })
    }


}