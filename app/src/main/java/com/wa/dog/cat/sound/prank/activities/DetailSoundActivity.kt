package com.wa.dog.cat.sound.prank.activities

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.SeekBar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.dog.cat.sound.prank.R
import com.wa.dog.cat.sound.prank.databinding.ActivityDetailSoundBinding
import com.wa.dog.cat.sound.prank.databinding.AdNativeVideoBinding
import com.wa.dog.cat.sound.prank.extension.gone
import com.wa.dog.cat.sound.prank.extension.setFullScreen
import com.wa.dog.cat.sound.prank.extension.setStatusBarColor
import com.wa.dog.cat.sound.prank.model.SoundItem
import com.wa.dog.cat.sound.prank.utils.LanguageUtil.setLocale
import com.wa.dog.cat.sound.prank.utils.RemoteConfigHelper
import com.wa.dog.cat.sound.prank.utils.RemoteConfigKey
import com.wa.dog.cat.sound.prank.utils.ads.NativeAdsUtils
import com.wa.dog.cat.sound.prank.utils.device.DeviceUtils
import timber.log.Timber
import java.io.IOException

class DetailSoundActivity : AppCompatActivity() {
    private lateinit var binding:ActivityDetailSoundBinding
    private var audioManager: AudioManager? = null
    var currentlyPlayingPosition: Int? = null
    private var mediaPlayer: MediaPlayer? = null
    private var isMediaPlayerPrepared = false
    private val remoteConfigHelper: RemoteConfigHelper by lazy { RemoteConfigHelper() }


    var soundItem: SoundItem? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailSoundBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setStatusBarColor("#ffffff")
        remoteConfigHelper.loadConfig()
        initControl()
        setUpLottieAnim()
    }

    override fun onStart() {
        super.onStart()
        loadAds()
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer?.stop()
    }

    private fun loadAds(){
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.SHOW_ADS_NATIVE_D_SOUND)) {
            val adConfig = FirebaseRemoteConfig.getInstance()
                .getString(RemoteConfigKey.KEY_SHOW_ADS_NATIVE_D_SOUND)
            if (adConfig.isNotEmpty()) {
                loadNativeAds(adConfig)
            } else {
                loadNativeAds(getString(R.string.banner_over_view))

            }
        }

    }

    private fun initControl(){
        binding.imvPlay.setImageResource(R.drawable.ic_play_result)
        if (intent.extras!!.containsKey("KEY_SOUND")) {
            soundItem = intent.extras!!.getSerializable("KEY_SOUND") as SoundItem
            binding.toolbar.title = soundItem!!.title.toString()
            soundItem!!.icon?.let { it1 -> binding.imvAnimal.setImageResource(it1) }
            binding.imvPlay.setOnClickListener {
                playSound(soundItem!!.sound)
                setUpSeekbarVolume()
            }
        }
        binding.toolbar.setNavigationOnClickListener { finish() }

    }


    private fun setUpLottieAnim(){
        binding.ltSoundDetail.setAnimation(R.raw.lt_sound_detail)
        binding.ltSoundDetail.loop(true)
        binding.ltSoundDetail.pauseAnimation()

    }

    private fun setUpSeekbarVolume() {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager?

        val maxVolume = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val currentVolume = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)

        binding.sbSoundDetail.max = maxVolume ?: 0
        binding.sbSoundDetail.progress = currentVolume ?: 0

        binding.sbSoundDetail.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }
        })
    }


    private fun playSound(soundFile: String?) {
        if (!soundFile.isNullOrEmpty()) {
            try {
                val soundUri = Uri.parse("android.resource://${this.packageName}/raw/$soundFile")
                Log.d("SoundAdapter", "Đường dẫn: $soundUri")

                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer()

                    mediaPlayer?.setDataSource(this, soundUri)
                    mediaPlayer?.prepareAsync()

                    mediaPlayer?.setOnPreparedListener {
                        Log.d("SoundAdapter", "MediaPlayer prepared successfully")
                        isMediaPlayerPrepared = true
                        mediaPlayer?.start()
                        binding.imvPlay.setImageResource(R.drawable.ic_pause_result)
                        binding.ltSoundDetail.playAnimation()
                    }

                    mediaPlayer?.setOnCompletionListener {
                        currentlyPlayingPosition = null
                        binding.imvPlay.setImageResource(R.drawable.ic_play_result)
                        binding.ltSoundDetail.pauseAnimation()
                        mediaPlayer?.release()
                        mediaPlayer = null
                    }

                    mediaPlayer?.setOnErrorListener { _, _, what ->
                        Timber.tag("SoundAdapter").e("Error during MediaPlayer preparation. What: %s", what)
                        false
                    }
                } else {
                    if (!isMediaPlayerPrepared) {
                        mediaPlayer?.reset()
                        mediaPlayer?.setDataSource(this, soundUri)
                        mediaPlayer?.prepareAsync()
                    } else {
                        if (mediaPlayer?.isPlaying == true) {
                            mediaPlayer?.pause()
                            binding.imvPlay.setImageResource(R.drawable.ic_play_result)
                            binding.ltSoundDetail.pauseAnimation()
                        } else {
                            mediaPlayer?.start()
                            binding.imvPlay.setImageResource(R.drawable.ic_pause_result)
                            binding.ltSoundDetail.playAnimation()
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("SoundAdapter", "Error setting data source: $e")
            }
        } else {
            Log.e("SoundAdapter", "Invalid sound file name")
        }
    }



    @SuppressLint("SuspiciousIndentation")
    private fun loadNativeAds(keyAds:String) {
        if (!DeviceUtils.checkInternetConnection(this@DetailSoundActivity)) binding.rlNative.gone()
            NativeAdsUtils.instance.loadNativeAds(
                this@DetailSoundActivity,
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
                    FirebaseAnalytics.getInstance(this@DetailSoundActivity).logEvent("v_load_native_ads_detail_sound_screen",null)
                } else {
                    FirebaseAnalytics.getInstance(this@DetailSoundActivity).logEvent("e_load_native_ads_detail_sound_screen",null)
                    binding.rlNative.gone()
                }
            }

    }



}