package com.wa.dog.cat.sound.prank.activities

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import com.wa.dog.cat.sound.prank.databinding.ActivityMainBinding
import com.wa.dog.cat.sound.prank.extension.setFullScreen
import com.wa.dog.cat.sound.prank.utils.Constant
import com.wa.dog.cat.sound.prank.utils.ads.MyApplication



class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val SPLASH_DELAY: Long = 10000
    private val COUNT_DOWN_INTERVAL: Long = 1000
    val bundle = Bundle()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullScreen()
        MobileAds.initialize(this)
        val application: Application = application
        (application as MyApplication).loadAd(this)

        loadOpenAds()
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