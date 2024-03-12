package com.wa.dog.cat.sound.prank.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.dog.cat.sound.prank.R
import com.wa.dog.cat.sound.prank.databinding.ActivityProcessingBinding
import com.wa.dog.cat.sound.prank.databinding.AdNativeVideoBinding
import com.wa.dog.cat.sound.prank.extension.gone
import com.wa.dog.cat.sound.prank.extension.setFullScreen
import com.wa.dog.cat.sound.prank.utils.RemoteConfigHelper
import com.wa.dog.cat.sound.prank.utils.RemoteConfigKey
import com.wa.dog.cat.sound.prank.utils.ads.NativeAdsUtils
import com.wa.dog.cat.sound.prank.utils.device.DeviceUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ProcessingActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProcessingBinding
    private var rewardedAd: RewardedAd? = null
    private val remoteConfigHelper: RemoteConfigHelper by lazy { RemoteConfigHelper() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProcessingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullScreen()
        remoteConfigHelper.loadConfig()
        setUpRewardedAds()
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
                loadNativeAds(getString(R.string.banner_over_view))

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
        finish()
    }


    private fun setUpRewardedAds(){
        var adRequest = AdRequest.Builder().build()
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.SHOW_ADS_REWARDED_PROCESS)) {
            val adConfig = FirebaseRemoteConfig.getInstance()
                .getString(RemoteConfigKey.KEY_SHOW_ADS_REWARDED_PROCESS)
            if (adConfig.isNotEmpty()) {
                RewardedAd.load(this,adConfig, adRequest, object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        adError.toString().let { Log.d("TAG", it) }
                        rewardedAd = null
                    }

                    override fun onAdLoaded(ad: RewardedAd) {
                        Log.d("ProcessingActivity", "Ad was loaded.")
                        rewardedAd = ad
                    }
                })
            } else {
                RewardedAd.load(this,getString(R.string.rewarded_processing), adRequest, object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        adError.toString().let { Log.d("TAG", it) }
                        rewardedAd = null
                    }

                    override fun onAdLoaded(ad: RewardedAd) {
                        Log.d("ProcessingActivity", "Ad was loaded.")
                        rewardedAd = ad
                    }
                })

            }
        }



    }


    private fun showAds(){
        binding.btnResult.setOnClickListener {

            if (rewardedAd != null){
                rewardedAd?.fullScreenContentCallback =
                    object : com.google.android.gms.ads.FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            rewardedAd!!.show(this@ProcessingActivity){
                                goToResult()
                            }
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                            rewardedAd!!.show(this@ProcessingActivity){
                                goToResult()
                            }
                        }
                    }
            }else {
                goToResult()
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