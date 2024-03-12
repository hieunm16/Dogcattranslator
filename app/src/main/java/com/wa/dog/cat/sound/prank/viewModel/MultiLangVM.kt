package com.wa.dog.cat.sound.prank.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.base.app.base.ui.base.base.BaseViewModel
import com.wa.dog.cat.sound.prank.data.model.LanguageUI
import com.wa.dog.cat.sound.prank.data.repository.language.LanguageRepo
import com.base.app.base.ui.base.base.toMutableListLiveData
import com.wa.dog.cat.sound.prank.utils.Constant
import com.wa.dog.cat.sound.prank.utils.SharedPreferenceHelper
import com.wa.dog.cat.sound.prank.utils.setAvatar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MultiLangVM @Inject constructor(
    private val languageRepository: LanguageRepo,
) : BaseViewModel() {
    private val _languageLiveData: MutableLiveData<List<LanguageUI>> = MutableLiveData()
    private fun getValueLanguage() = _languageLiveData.toMutableListLiveData { it.copy() }
    val languageLiveData: LiveData<List<LanguageUI>>
        get() = _languageLiveData

    fun getListLanguage() = viewModelScope.launch(Dispatchers.IO) {
        languageRepository.getAllLanguage()
            .flowOn(Dispatchers.IO)
            .collect { allLanguage ->
                if (allLanguage.isEmpty()) {
                    val listLanguageDefault = mutableListOf<LanguageUI>()
                    val language1 = LanguageUI(
                        name = "English",
                        code = "en",
                    )
                    val language2 = LanguageUI(
                        name = "Spanish",
                        code = "es",
                    )
                    val language3 = LanguageUI(
                        name = "French",
                        code = "fr",
                    )

                    val language4 = LanguageUI(
                        name = "Hindi",
                        code = "hi",
                    )

                    val language5 = LanguageUI(
                        name = "Portuguese",
                        code = "pt",
                    )

                    val language6 = LanguageUI(
                        name = "Indonesia",
                        code = "in",
                    )


                    insertLanguageToDB(language1)
                    insertLanguageToDB(language2)
                    insertLanguageToDB(language3)
                    insertLanguageToDB(language4)
                    insertLanguageToDB(language5)
                    insertLanguageToDB(language6)

                    listLanguageDefault.add(language1)
                    listLanguageDefault.add(language2)
                    listLanguageDefault.add(language3)
                    listLanguageDefault.add(language4)
                    listLanguageDefault.add(language5)
                    listLanguageDefault.add(language6)

                    listLanguageDefault.postValueLanguage()
                } else {
                    allLanguage.toMutableList().postValueLanguage()
                }
            }
    }

    private fun insertLanguageToDB(languageUI: LanguageUI) = viewModelScope.launch(Dispatchers.IO) {
        languageRepository.insertLanguageToDb(languageUI).flowOn(Dispatchers.IO)
            .collectLatest { idLang ->
                languageUI.apply {
                    id = idLang.toInt()
                }
            }
    }

    fun saveFirstKeyIntro() =
        viewModelScope.launch(Dispatchers.IO) {
            SharedPreferenceHelper.storeInt(
                Constant.KEY_FIRST_SHOW_INTRO,
                Constant.TYPE_SHOW_INTRO_ACT
            )
        }

    private fun MutableList<LanguageUI>.postValueLanguage() {
        this.forEach {
            it.avatar = (it.code ?: "vi").setAvatar()
        }
        this.sortBy { it.name }
        _languageLiveData.postValue(this)
    }
}