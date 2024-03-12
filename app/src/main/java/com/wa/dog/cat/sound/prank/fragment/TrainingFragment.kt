package com.wa.dog.cat.sound.prank.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
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
import com.wa.dog.cat.sound.prank.activities.HomeActivity
import com.wa.dog.cat.sound.prank.adapter.TrainingAdapter
import com.wa.dog.cat.sound.prank.databinding.FragmentTrainingBinding
import com.wa.dog.cat.sound.prank.model.TrainingItem
import com.wa.dog.cat.sound.prank.utils.RemoteConfigKey

class TrainingFragment: Fragment() {
    private lateinit var binding: FragmentTrainingBinding
    private lateinit var adapter: TrainingAdapter
    private val listTraining = ArrayList<TrainingItem>()
    private var analytics : FirebaseAnalytics? = null
    private var mInterstitialAd: InterstitialAd? = null


    companion object {
        const val KEY_TRAINING_ITEM = "KEY_TRAINING_ITEM"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrainingBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun goToHome(){
        val intent = Intent(requireContext(), HomeActivity::class.java)
        startActivity(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        analytics =  Firebase.analytics
        initControl()
    }

    private fun initControl() {
        binding.toolbar.setNavigationOnClickListener { goToHome() }
        setUpListTraining()
    }

    override fun onStart() {
        super.onStart()
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.SHOW_ADS_INTER_D_TRAINING)) {
            val adConfig = FirebaseRemoteConfig.getInstance()
                .getString(RemoteConfigKey.KEY_SHOW_ADS_INTER_D_TRAINING)
            if (adConfig.isNotEmpty()) {
                loadInterAds(adConfig)
            } else {
                loadInterAds(getString(R.string.inter_anim_detail_training))

            }
        }
    }

    private fun setUpListTraining() {
        adapter = TrainingAdapter(requireContext(), listTraining)
        val layoutManager =
            LinearLayoutManager(requireContext())
        binding.rvTraining.layoutManager = layoutManager
        binding.rvTraining.adapter = adapter
        adapter.apply {
            onClickItem = { _, item ->
                val bundle = Bundle().apply {
                    putSerializable(KEY_TRAINING_ITEM, item)
                }
                val fragment = TrainingDescriptionFragment().apply {
                    arguments = bundle
                }
                if (mInterstitialAd != null) {
                    // Nếu quảng cáo đã tải xong, hiển thị quảng cáo và chuyển đến Activity mới sau khi quảng cáo kết thúc
                    mInterstitialAd?.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            replaceFragment(fragment, TrainingDescriptionFragment.TAG)
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                            replaceFragment(fragment, TrainingDescriptionFragment.TAG)
                        }
                    }
                    activity?.let { mInterstitialAd?.show(it)
                        FirebaseAnalytics.getInstance(it).logEvent("v_load_inter_ads_training_screen",null)
                    }
                } else {
                    // Nếu quảng cáo chưa tải xong, chuyển đến Activity mới ngay lập tức
                    replaceFragment(fragment, TrainingDescriptionFragment.TAG)
                }
            }
        }

        prepareItemTraining()
    }

    override fun onResume() {
        super.onResume()
        Adjust.onResume()
    }

    override fun onPause() {
        super.onPause()
        Adjust.onPause()
    }

    private fun replaceFragment(f: Fragment, tag: String = "", addToBackStack: Boolean = true) {
        val ft = parentFragmentManager.beginTransaction()
        if (tag.isEmpty()) {
            ft.replace(R.id.frameFragment, f)
        } else {
            ft.replace(R.id.frameFragment, f, tag)
        }

        if (addToBackStack) {
            ft.addToBackStack(tag)
        }

        ft.commitAllowingStateLoss()
    }


    private fun prepareItemTraining() {
        listTraining.clear()

        listTraining.add(TrainingItem().apply {
            icon = R.drawable.dog_sit
            title = getString(R.string.how_to_train_a_dog_to_sit)
            des = getString(R.string.des_1)
        })

        listTraining.add(TrainingItem().apply {
            icon = R.drawable.dog_stay
            title = getString(R.string.how_to_train_a_dog_to_stay)
            des = getString(R.string.des_2)
        })

        listTraining.add(TrainingItem().apply {
            icon = R.drawable.dog_lie_to_down
            title = getString(R.string.how_to_train_your_dog_to_lie_down)
            des = getString(R.string.des_3)
        })

        listTraining.add(TrainingItem().apply {
            icon = R.drawable.how_to_keep_cat_happy
            title = getString(R.string.how_to_keep_your_cat_healthy_and_happy)
            des = getString(R.string.des_4)

        })

        listTraining.add(TrainingItem().apply {
            icon = R.drawable.neuter_my_cat
            title = getString(R.string.should_i_spay_or_neuter_my_cat)
            des =  getString(R.string.des_5) })

        adapter.notifyDataSetChanged()
    }

    private fun loadInterAds(keyAds:String){

        context?.let {
            InterstitialAd.load(
                it,
                keyAds,
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        mInterstitialAd = interstitialAd
                        FirebaseAnalytics.getInstance(it).logEvent("d_load_inter_ads_training_screen",null)

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
                                analytics = FirebaseAnalytics.getInstance(it)
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
                        FirebaseAnalytics.getInstance(it).logEvent("e_load_inter_ads_training_screen",null)

                    }
                })
        }
    }

}