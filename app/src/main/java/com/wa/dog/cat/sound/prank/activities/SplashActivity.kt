package com.wa.dog.cat.sound.prank.activities

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.google.firebase.analytics.FirebaseAnalytics
import com.wa.dog.cat.sound.prank.databinding.ActivitySplashBinding
import com.wa.dog.cat.sound.prank.extension.setFullScreen
import com.wa.dog.cat.sound.prank.utils.Constant
import com.wa.dog.cat.sound.prank.utils.RemoteConfigHelper
import com.wa.dog.cat.sound.prank.utils.ads.MyApplication
import kotlin.math.log


class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val SPLASH_DELAY: Long = 10000
    private val COUNT_DOWN_INTERVAL: Long = 1000
    val bundle = Bundle()
    private val remoteConfigHelper: RemoteConfigHelper by lazy { RemoteConfigHelper() }
    private lateinit var consentInformation: ConsentInformation



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullScreen()
        remoteConfigHelper.loadConfig()
        MobileAds.initialize(this)
//        setUpCMPAds()

        val application: Application = application
        (application as MyApplication).loadAd(this)
        loadOpenAds()

    }


    private fun setUpCMPAds(){
        // testing device
        val debugSettings = ConsentDebugSettings.Builder(this)
            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
            .addTestDeviceHashedId("3B726488464F3BBF0AAC50A5EBC919C8")
            .build()
        val params = ConsentRequestParameters
            .Builder()
            .setConsentDebugSettings(debugSettings)
            .build()
        consentInformation = UserMessagingPlatform.getConsentInformation(this)
        consentInformation.requestConsentInfoUpdate(
            this,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(this){loadAndShowError ->
                    if(loadAndShowError != null){
                        Log.d("HIEUNM", "loadAndShowError: "+loadAndShowError.message)

                    }
                    if (consentInformation.canRequestAds()){
                        //Load Ad


                    }

                }
            },
            {
                    requestConsentError ->
                // Consent gathering failed.
                Log.w("HIEUNM", String.format("%s: %s",
                    requestConsentError.errorCode,
                    requestConsentError.message
                ))
            }
        )
    }


    private fun openChooseLanguageActivity(){
        val intent = Intent(this@SplashActivity, ChooseLanguageActivity::class.java)
        intent.putExtra(Constant.TYPE_LANG, Constant.TYPE_LANGUAGE_SPLASH)
        startActivity(intent)
        finish()
    }


    private fun loadOpenAds() {
        val countDownTimer: CountDownTimer = object : CountDownTimer(SPLASH_DELAY, COUNT_DOWN_INTERVAL) {
            override fun onTick(l: Long) {}
            override fun onFinish() {
                FirebaseAnalytics.getInstance(this@SplashActivity).logEvent("d_load_open_ads",bundle)
                val application = application
                (application as MyApplication).showAdIfAvailable(
                    this@SplashActivity,
                    object : MyApplication.OnShowAdCompleteListener {
                        override fun onAdShown() {
                            openChooseLanguageActivity()
                        }
                    })
            }
        }
        countDownTimer.start()
    }
}