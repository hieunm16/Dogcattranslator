package com.wa.dog.cat.sound.prank.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdapterResponseInfo
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.dog.cat.sound.prank.R
import com.wa.dog.cat.sound.prank.activities.DetailSoundActivity
import com.wa.dog.cat.sound.prank.activities.HomeActivity
import com.wa.dog.cat.sound.prank.activities.OverviewActivity
import com.wa.dog.cat.sound.prank.adapter.SoundAdapter
import com.wa.dog.cat.sound.prank.databinding.AdNativeVideoBinding
import com.wa.dog.cat.sound.prank.databinding.FragmentSoundBinding
import com.wa.dog.cat.sound.prank.extension.gone
import com.wa.dog.cat.sound.prank.model.SoundItem
import com.wa.dog.cat.sound.prank.utils.MediaPlayerUtils
import com.wa.dog.cat.sound.prank.utils.RemoteConfigKey
import com.wa.dog.cat.sound.prank.utils.ads.NativeAdsUtils
import com.wa.dog.cat.sound.prank.utils.device.DeviceUtils

class SoundFragment : Fragment() {
    private lateinit var binding: FragmentSoundBinding
    private lateinit var adapter: SoundAdapter
    private var mediaPlayUtil: MediaPlayerUtils? = null
    private val listSound = ArrayList<SoundItem>()
    private var analytics: FirebaseAnalytics? = null
    private var mInterstitialAd: InterstitialAd? = null
    var overViewActivity: OverviewActivity? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OverviewActivity) {
            overViewActivity = context
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analytics = Firebase.analytics
        mediaPlayUtil = context?.let { MediaPlayerUtils(it) }
    }

    private fun goToHome() {
        val intent = Intent(requireContext(), HomeActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSoundBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initControl()

    }

    private fun initControl() {
        binding.toolbar.setNavigationOnClickListener { goToHome() }
        setUpListSound()
    }

    override fun onResume() {
        super.onResume()
        Adjust.onResume()
    }

    override fun onPause() {
        super.onPause()
        Adjust.onPause()
    }

    private fun setUpListSound() {
        adapter = SoundAdapter(requireContext(), listSound)
        val layoutManager =
            GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false)
        binding.rvSound.layoutManager = layoutManager
        binding.rvSound.adapter = adapter
        adapter.apply {
            onClickItem = { _, item ->
                val intent = Intent(requireContext(), DetailSoundActivity::class.java)
                intent.putExtra("KEY_SOUND", item)
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
                    activity?.let {
                        mInterstitialAd?.show(it)
                        FirebaseAnalytics.getInstance(it)
                            .logEvent("v_load_inter_ads_sound_screen", null)
                    }
                } else {
                    // Nếu quảng cáo chưa tải xong, chuyển đến Activity mới ngay lập tức
                    startActivity(intent)
                }
            }
        }
        prepareItemSound()
    }

    override fun onStart() {
        super.onStart()
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.SHOW_ADS_NATIVE_SOUND)) {
            val adConfig = FirebaseRemoteConfig.getInstance()
                .getString(RemoteConfigKey.KEY_SHOW_ADS_NATIVE_SOUND)
            if (adConfig.isNotEmpty()) {
                loadNativeAds(adConfig)
            } else {
                loadNativeAds(getString(R.string.native_sound))

            }
        }

        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.SHOW_ADS_INTER_D_SOUND)) {
            val adConfig = FirebaseRemoteConfig.getInstance()
                .getString(RemoteConfigKey.KEY_SHOW_ADS_INTER_D_SOUND)
            if (adConfig.isNotEmpty()) {
                loadInterAds(adConfig)
            } else {
                loadInterAds(getString(R.string.inter_anim_detail_sound))

            }
        }


    }

    private fun addListSoundDog() {
        listSound.add(SoundItem().apply {
            icon = R.drawable.angry
            title = getString(R.string.s_angry)
            sound = "dog_angry"
        })

        listSound.add(SoundItem().apply {
            icon = R.drawable.happy
            title = getString(R.string.s_enjoyed)
            sound = "dog_enjoyed"
        })

        listSound.add(SoundItem().apply {
            icon = R.drawable.sad
            title = getString(R.string.s_sad)
            sound = "dog_crying"
        })

        listSound.add(SoundItem().apply {
            icon = R.drawable.enjoyed
            title = getString(R.string.s_happy)
            sound = "dog_happy"
        })

        listSound.add(SoundItem().apply {
            icon = R.drawable.cute
            title = getString(R.string.s_cutie)
            sound = "dog_cutie"
        })
        listSound.add(SoundItem().apply {
            icon = R.drawable.sileneed
            title = getString(R.string.s_sileneed)
            sound = "dog_sileneed"

        })

        listSound.add(SoundItem().apply {
            icon = R.drawable.elated
            title = getString(R.string.s_elated)
            sound = "dog_elated"
        })

        listSound.add(SoundItem().apply {
            icon = R.drawable.tired
            title = getString(R.string.s_tired)
            sound = "dog_tired"
        })

        listSound.add(SoundItem().apply {
            icon = R.drawable.excited
            title = getString(R.string.s_excited)
            sound = "dog_excited"
        })

        listSound.add(SoundItem().apply {
            icon = R.drawable.relaxed
            title = getString(R.string.s_relaxed)
            sound = "dog_relaxed"
        })
    }

    private fun addListSoundCat() {
        listSound.add(SoundItem().apply {
            icon = R.drawable.cat_angry
            title = getString(R.string.s_angry)
            sound = "cat_angry"
        })

        listSound.add(SoundItem().apply {
            icon = R.drawable.cat_enjoyed
            title = getString(R.string.s_enjoyed)
            sound = "cat_enjoyed"
        })

        listSound.add(SoundItem().apply {
            icon = R.drawable.cat_sad
            title = getString(R.string.s_sad)
            sound = "cat_crying"
        })

        listSound.add(SoundItem().apply {
            icon = R.drawable.cat_happy
            title = getString(R.string.s_happy)
            sound = "cat_happy"
        })

        listSound.add(SoundItem().apply {
            icon = R.drawable.cat_cutie
            title = getString(R.string.s_cutie)
            sound = "cat_cutie"
        })
        listSound.add(SoundItem().apply {
            icon = R.drawable.cat_sileneed
            title = getString(R.string.s_sileneed)
            sound = "cat_sileneed"

        })

        listSound.add(SoundItem().apply {
            icon = R.drawable.cat_elated
            title = getString(R.string.s_elated)
            sound = "cat_elated"
        })

        listSound.add(SoundItem().apply {
            icon = R.drawable.cat_tired
            title = getString(R.string.s_tired)
            sound = "cat_tired"
        })

        listSound.add(SoundItem().apply {
            icon = R.drawable.cat_excited
            title = getString(R.string.s_excited)
            sound = "cat_excited"
        })

        listSound.add(SoundItem().apply {
            icon = R.drawable.cat_relaxed
            title = getString(R.string.s_relaxed)
            sound = "cat_relaxed"
        })
    }

    private fun addListSoundHamster() {
        listSound.add(SoundItem().apply {
            icon = R.drawable.hamster_angry
            title = getString(R.string.s_angry)
            sound = "hamster_angry"
        })

        listSound.add(SoundItem().apply {
            icon = R.drawable.hamster_enjoyed
            title = getString(R.string.s_enjoyed)
            sound = "hamster_enjoyed"
        })

        listSound.add(SoundItem().apply {
            icon = R.drawable.hamster_sad
            title = getString(R.string.s_sad)
            sound = "hamster_crying"
        })

        listSound.add(SoundItem().apply {
            icon = R.drawable.hamster_happy
            title = getString(R.string.s_happy)
            sound = "hamster_happy"
        })

        listSound.add(SoundItem().apply {
            icon = R.drawable.hamster_cutie
            title = getString(R.string.s_cutie)
            sound = "hamster_cutie"
        })
        listSound.add(SoundItem().apply {
            icon = R.drawable.hamster_sileneed
            title = getString(R.string.s_sileneed)
            sound = "hamster_sileneed"

        })

        listSound.add(SoundItem().apply {
            icon = R.drawable.hamster_elated
            title = getString(R.string.s_elated)
            sound = "hamster_elated"
        })

        listSound.add(SoundItem().apply {
            icon = R.drawable.hamster_tired
            title = getString(R.string.s_tired)
            sound = "hamster_tired"
        })

        listSound.add(SoundItem().apply {
            icon = R.drawable.hamster_excited
            title = getString(R.string.s_excited)
            sound = "hamster_excited"
        })

        listSound.add(SoundItem().apply {
            icon = R.drawable.hamster_relaxed
            title = getString(R.string.s_relaxed)
            sound = "hamster_relaxed"
        })
    }

    private fun prepareItemSound() {
        listSound.clear()

        when (HomeActivity.animal) {
            "dog" -> addListSoundDog()
            "cat" -> addListSoundCat()
            "hamster" -> addListSoundHamster()
        }

        adapter.notifyDataSetChanged()
    }

    private fun loadNativeAds(keyAds: String) {
        if (!DeviceUtils.checkInternetConnection(requireContext())) {
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
                            .logEvent("d_load_native_ads_sound_screen", null)
                    } else {
                        binding.rlNative.gone()
                        FirebaseAnalytics.getInstance(context)
                            .logEvent("e_load_native_ads_sound_screen", null)
                    }
                }
            }
        }
    }



    private fun loadInterAds(keyAds: String) {
        context?.let {
            InterstitialAd.load(
                it,
                keyAds,
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        mInterstitialAd = interstitialAd
                        FirebaseAnalytics.getInstance(it)
                            .logEvent("d_load_inter_ads_sound_screen", null)

                        mInterstitialAd?.onPaidEventListener =
                            OnPaidEventListener { adValue -> // Lấy thông tin về nhà cung cấp quảng cáo
                                val loadedAdapterResponseInfo: AdapterResponseInfo? =
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
                                analytics = FirebaseAnalytics.getInstance(it)
                                val params = Bundle()
                                params.putString(
                                    FirebaseAnalytics.Param.AD_PLATFORM,
                                    "admob mediation"
                                )
                                params.putString(FirebaseAnalytics.Param.AD_SOURCE, "AdMob")
                                params.putString(FirebaseAnalytics.Param.AD_FORMAT, "Interstitial")
                                params.putDouble(FirebaseAnalytics.Param.VALUE, revenue)
                                params.putString(FirebaseAnalytics.Param.CURRENCY, "USD")
                                analytics?.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, params)
                            }
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        FirebaseAnalytics.getInstance(it)
                            .logEvent("e_load_inter_ads_sound_screen", null)
                        mInterstitialAd = null
                    }
                })
        }
    }
}