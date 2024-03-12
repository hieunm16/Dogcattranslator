package com.wa.dog.cat.sound.prank.data

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.room.Room
import com.wa.dog.cat.sound.prank.data.local.room.dao.LanguageDao
import com.base.app.data.repository.language.LanguageRepoImpl
import com.wa.dog.cat.sound.prank.data.local.room.AppDatabase
import com.wa.dog.cat.sound.prank.data.repository.language.LanguageRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
	@Provides
	@Singleton
	fun provideSharedPreference(context: Application?): SharedPreferences {
		return PreferenceManager.getDefaultSharedPreferences(context)
	}


	@Provides
	@Singleton
	fun provideRoomDb3(context: Application): AppDatabase {
		return Room.databaseBuilder(
			context,
			AppDatabase::class.java,
			AppDatabase.DATABASE_NAME
		)
			.allowMainThreadQueries()
			.build()
	}

	@Provides
	@Singleton
	fun provideLanguageDao(database: AppDatabase): LanguageDao {
		return database.languageDao()
	}


	@Provides
	@Singleton
	fun provideLanguageRepo(languageDao: LanguageDao): LanguageRepo {
		return LanguageRepoImpl(languageDao)
	}

}