package com.wa.dog.cat.sound.prank.utils

import android.content.Context
import android.content.res.Configuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

object LanguageUtil {
    private var myLocale: Locale? = null
    const val KEY_LANGUAGE = "KEY_LANGUAGE"

    fun Context.setLocale() {
        kotlin.runCatching {
            val language = getPreLanguage()
            if (language == "") {
                Locale.getDefault().apply {
                    Locale.setDefault(this)
                    Configuration().let { config ->
                        config.locale = this
                        resources.updateConfiguration(config, resources.displayMetrics)
                    }
                }
            } else {
                this.changeLang(language)
            }
        }.onFailure { it.printStackTrace() }
    }

    fun Context.changeLang(lang: String) {
        kotlin.runCatching {
            if (lang.equals("", ignoreCase = true)) return
            myLocale = Locale(lang)
            lang.saveKeyLanguageToSharedPrefrerences()
            myLocale?.let { Locale.setDefault(it) }
            Configuration().apply {
                locale = myLocale
                resources.updateConfiguration(this, resources.displayMetrics)
            }

        }.onFailure { it.printStackTrace() }
    }

    fun getPreLanguage() =
        SharedPreferenceHelper.getStringWithDefault(KEY_LANGUAGE,"en")

    fun String.saveKeyLanguageToSharedPrefrerences() {
        kotlin.runCatching {
            if (this == "") return
            CoroutineScope(Dispatchers.IO).launch {
                kotlin.runCatching {
                    SharedPreferenceHelper.storeString(
                        KEY_LANGUAGE,
                        this@saveKeyLanguageToSharedPrefrerences
                    )
                }.onFailure { it.printStackTrace() }
            }
        }.onFailure { it.printStackTrace() }
    }
}