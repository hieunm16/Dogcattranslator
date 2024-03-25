package com.wa.dog.cat.sound.prank.fragment

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.dog.cat.sound.prank.R
import com.wa.dog.cat.sound.prank.activities.HomeActivity
import com.wa.dog.cat.sound.prank.activities.OverviewActivity
import com.wa.dog.cat.sound.prank.activities.SplashActivity
import com.wa.dog.cat.sound.prank.databinding.AdNativeVideoBinding
import com.wa.dog.cat.sound.prank.databinding.FragmentWhistleBinding
import com.wa.dog.cat.sound.prank.extension.gone
import com.wa.dog.cat.sound.prank.extension.visible
import com.wa.dog.cat.sound.prank.utils.RemoteConfigKey
import com.wa.dog.cat.sound.prank.utils.ads.BannerUtils
import com.wa.dog.cat.sound.prank.utils.ads.NativeAdsUtils
import com.wa.dog.cat.sound.prank.utils.device.DeviceUtils

class WhistleFragment : Fragment() {
    private lateinit var binding: FragmentWhistleBinding
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var audioManager: AudioManager
    var overViewActivity: OverviewActivity? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OverviewActivity) {
            overViewActivity = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWhistleBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun goToHome() {
        val intent = Intent(requireContext(), HomeActivity::class.java)
        startActivity(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initControl()
    }

    override fun onStart() {
        super.onStart()
        if (FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.SHOW_ADS_NATIVE_WHISTLE)
        ) {
            binding.rlNative.visible()
            val adConfig = FirebaseRemoteConfig.getInstance()
                .getString(RemoteConfigKey.KEY_SHOW_ADS_NATIVE_WHISTLE)
            if (adConfig.isNotEmpty()) {
                loadNativeAds(adConfig)
            } else {
                loadNativeAds(getString(R.string.native_whistle))
            }
        }else{
            binding.rlNative.gone()

        }
    }

    private fun initControl() {
        binding.toolbar.setNavigationOnClickListener { goToHome() }
        audioManager = requireContext().getSystemService(AudioManager::class.java)
        binding.imvPlay.setBackgroundResource(R.drawable.ic_play)
        var isPlaying = false

        binding.imvPlay.setOnClickListener {
            if (isPlaying) {
                binding.imvPlay.setBackgroundResource(R.drawable.ic_play)
                mediaPlayer.pause()
            } else {
                binding.imvPlay.setBackgroundResource(R.drawable.ic_pause)
                mediaPlayer.start()
            }
            isPlaying = !isPlaying
        }

        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        binding.sbWhistle.max = maxVolume
        binding.sbWhistle.progress = currentVolume
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.whistle)
        binding.sbWhistle.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }
        })

    }
    /**/

    private fun loadNativeAds(keyAds: String) {
        if (!DeviceUtils.checkInternetConnection(requireContext()) || FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.SHOW_ADS_NATIVE_WHISTLE) ) {
            binding.rlNative.gone()
            return
        }

        // Kiểm tra xem Fragment đã gắn kết và hiển thị trên màn hình chưa
        view?.post {
            // Using view?.post to ensure the fragment is attached and has a view
            context?.let { context ->
                NativeAdsUtils.instance.loadNativeAds(
                    context,
                    keyAds
                ) { nativeAds ->
                    if (nativeAds != null && isAdded && isVisible) {
                        binding.frNativeAds.removeAllViews()
                        val adNativeVideoBinding = AdNativeVideoBinding.inflate(layoutInflater)
                        NativeAdsUtils.instance.populateNativeAdVideoView(
                            nativeAds,
                            adNativeVideoBinding.root
                        )
                        binding.frNativeAds.addView(adNativeVideoBinding.root)
                        FirebaseAnalytics.getInstance(context)
                            .logEvent("d_load_native_ads_whistle_screen", null)

                    } else {
                        binding.rlNative.gone()
                        FirebaseAnalytics.getInstance(context)
                            .logEvent("e_load_native_ads_whistle_screen", null)

                    }
                }
            }
        }
    }


    override fun onStop() {
        super.onStop()
        mediaPlayer.stop()
    }
}