package com.wa.dog.cat.sound.prank.data.repository.language

import com.wa.dog.cat.sound.prank.data.model.LanguageUI
import kotlinx.coroutines.flow.Flow

interface LanguageRepo {
    suspend fun getAllLanguage(): Flow<List<LanguageUI>>
    suspend fun insertLanguageToDb(languageUI: LanguageUI): Flow<Long>
}