package com.base.app.data.repository.language

import com.wa.dog.cat.sound.prank.data.local.room.dao.LanguageDao
import com.wa.dog.cat.sound.prank.data.model.LanguageUI
import com.wa.dog.cat.sound.prank.data.repository.language.LanguageRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LanguageRepoImpl @Inject constructor(
    private val languageDao: LanguageDao,
) : LanguageRepo {
    override suspend fun getAllLanguage(): Flow<List<LanguageUI>> {
        return flow {
            emit(languageDao.getAllLanguage() ?: mutableListOf())
        }
    }

    override suspend fun insertLanguageToDb(languageUI: LanguageUI): Flow<Long> {
        return flow {
            emit( languageDao.insertLanguage(languageUI) )
           }
    }
}