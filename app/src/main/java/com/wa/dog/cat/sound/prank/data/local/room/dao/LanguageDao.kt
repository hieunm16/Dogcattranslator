package com.wa.dog.cat.sound.prank.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.wa.dog.cat.sound.prank.data.model.LanguageUI

@Dao
interface LanguageDao {

	@Query("SELECT * FROM language_table ORDER BY id ASC")
	fun getAllLanguage(): List<LanguageUI>?

	@Insert
	suspend fun insertLanguage(languageUI: LanguageUI): Long
}