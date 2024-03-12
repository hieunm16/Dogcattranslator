package com.wa.dog.cat.sound.prank.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.base.app.base.ui.base.base.observeWithCatch
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.dog.cat.sound.prank.R
import com.wa.dog.cat.sound.prank.adapter.MultiLangAdapter
import com.wa.dog.cat.sound.prank.databinding.ActivityChooseLanguageBinding
import com.wa.dog.cat.sound.prank.databinding.AdNativeVideoBinding
import com.wa.dog.cat.sound.prank.extension.gone
import com.wa.dog.cat.sound.prank.extension.invisible
import com.wa.dog.cat.sound.prank.extension.setFullScreen
import com.wa.dog.cat.sound.prank.extension.setOnSafeClick
import com.wa.dog.cat.sound.prank.extension.setStatusBarColor
import com.wa.dog.cat.sound.prank.extension.visible
import com.wa.dog.cat.sound.prank.utils.Constant
import com.wa.dog.cat.sound.prank.utils.Constant.TYPE_LANGUAGE_SPLASH
import com.wa.dog.cat.sound.prank.utils.LanguageUtil.changeLang
import com.wa.dog.cat.sound.prank.utils.LanguageUtil.getPreLanguage
import com.wa.dog.cat.sound.prank.utils.RemoteConfigKey
import com.wa.dog.cat.sound.prank.utils.ads.NativeAdsUtils
import com.wa.dog.cat.sound.prank.utils.device.DeviceUtils
import com.wa.dog.cat.sound.prank.viewModel.MultiLangVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class ChooseLanguageActivity : BaseBindingActivity<ActivityChooseLanguageBinding, MultiLangVM>() {

    private var type: Int = 0
    private var currentPosLanguage = 0
    private var oldCode = "en"
    private var code = ""

    private val multiLangAdapter: MultiLangAdapter by lazy {
        MultiLangAdapter().apply {
            callBack = { pos, item ->
                code = item.code ?: code
                currentPosLanguage = pos
            }
            binding.rvLang.adapter = this
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_choose_language

    override fun getViewModel(): Class<MultiLangVM> = MultiLangVM::class.java


    override fun setupView(savedInstanceState: Bundle?) {
        setFullScreen()
        intent?.let {
            type = it.getIntExtra(Constant.TYPE_LANG, 0)
            updateUIForType(type)
        }

        lifecycleScope.launch(Dispatchers.IO) {
            code = getPreLanguage()
            code = oldCode
        }
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.SHOW_ADS_NATIVE_LANGUAGE)) {
            val adConfig = FirebaseRemoteConfig.getInstance()
                .getString(RemoteConfigKey.KEY_SHOW_ADS_NATIVE_LANGUAGE)
            if (adConfig.isNotEmpty()) {
                loadNativeAds(adConfig)
            } else {
                loadNativeAds(getString(R.string.native_choose_lang))

            }
        }
    }


    override fun setupData() {
        viewModel.getListLanguage()
        viewModel.languageLiveData.observeWithCatch(this) { data ->
            multiLangAdapter.submitList(data)
            data.indexOfFirst { it.code == getPreLanguage() }.let { pos ->
                currentPosLanguage = pos
                multiLangAdapter.newPosition = pos
            }
        }
    }


    private fun updateUIForType(type: Int) {
        when (type) {
            TYPE_LANGUAGE_SPLASH -> {
                binding.tvTitleSetting.invisible()
                binding.imBack.invisible()
                binding.imBack.isEnabled = false

                binding.tvTitleSplash.visible()
                binding.imgChooseLang.visible()
                binding.imgChooseLang.setOnSafeClick {
                    viewModel.saveFirstKeyIntro()
                    this.changeLang(code.ifEmpty { oldCode })
                    startActivity(Intent(this, IntroActivity::class.java))
                    finish()
                }
            }

        }
    }

    private fun loadNativeAds(keyAds:String) {
        if (!DeviceUtils.checkInternetConnection(this@ChooseLanguageActivity)) binding.rlNative.gone()

        NativeAdsUtils.instance.loadNativeAds(
            this@ChooseLanguageActivity,
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
                FirebaseAnalytics.getInstance(this@ChooseLanguageActivity).logEvent("d_load_native_ads_choose_lang",null)
            } else {
                binding.rlNative.gone()
                FirebaseAnalytics.getInstance(this@ChooseLanguageActivity).logEvent("e_load_native_ads_choose_lang",null)
            }
        }
    }
}