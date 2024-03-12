package com.wa.dog.cat.sound.prank.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.wa.dog.cat.sound.prank.R
import com.wa.dog.cat.sound.prank.databinding.ActivityOverviewBinding
import com.wa.dog.cat.sound.prank.extension.gone
import com.wa.dog.cat.sound.prank.extension.setFullScreen
import com.wa.dog.cat.sound.prank.extension.setStatusBarColor
import com.wa.dog.cat.sound.prank.fragment.SoundFragment
import com.wa.dog.cat.sound.prank.fragment.TrainingFragment
import com.wa.dog.cat.sound.prank.fragment.TranslatorFragment
import com.wa.dog.cat.sound.prank.fragment.WhistleFragment
import com.wa.dog.cat.sound.prank.utils.RemoteConfigHelper
import com.wa.dog.cat.sound.prank.utils.RemoteConfigKey
import com.wa.dog.cat.sound.prank.utils.ads.AdsConsentManager
import com.wa.dog.cat.sound.prank.utils.ads.BannerUtils
import com.wa.dog.cat.sound.prank.utils.device.DeviceUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class OverviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOverviewBinding
    private val remoteConfigHelper: RemoteConfigHelper by lazy { RemoteConfigHelper() }


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setStatusBarColor("#ffffff")
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        remoteConfigHelper.loadConfig()
        val soundFragment = SoundFragment()
        val translatorFragment = TranslatorFragment()
        val trainingFragment = TrainingFragment()
        val whistleFragment = WhistleFragment()

        setCurrentFragment(TranslatorFragment())

        binding.bottomNar.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.sound-> {
                    setCurrentFragment(soundFragment)
                    FirebaseAnalytics.getInstance(this).logEvent("c_sound_screen",null)
                }
                R.id.translator -> {
                    setCurrentFragment(translatorFragment)
                    FirebaseAnalytics.getInstance(this).logEvent("c_translator_screen",null)

                }
                R.id.training -> {
                    setCurrentFragment(trainingFragment)
                    FirebaseAnalytics.getInstance(this).logEvent("c_training_screen",null)
                }
                R.id.whistle ->{
                    setCurrentFragment(whistleFragment)
                    FirebaseAnalytics.getInstance(this).logEvent("c_whistle_screen",null)

                }
            }
            true
        }
        loadAds()

    }


    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameFragment,fragment)
            commit()
        }

    private fun loadAds(){
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.SHOW_ADS_BANNER_OVERVIEW)) {
            val adConfig = FirebaseRemoteConfig.getInstance()
                .getString(RemoteConfigKey.KEY_SHOW_ADS_BANNER_OVERVIEW)
            if (adConfig.isNotEmpty()) {
                loadBanner(adConfig)
            } else {
                loadBanner(getString(R.string.banner_over_view))
            }
        }

    }

    private fun loadBanner(keyAdsBanner:String) {
        if (!DeviceUtils.checkInternetConnection(this)) binding.rlBanner.gone()
        BannerUtils.instance?.loadBanner(this, keyAdsBanner)
        FirebaseAnalytics.getInstance(this).logEvent("v_load_banner_ads_overview",null)

    }
}