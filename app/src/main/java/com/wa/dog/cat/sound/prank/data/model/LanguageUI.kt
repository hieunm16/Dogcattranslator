package com.wa.dog.cat.sound.prank.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "language_table")
data class LanguageUI(
	@PrimaryKey(autoGenerate = true)
	var id: Int = 0,
	val name: String?,
	val code: String?,
	var isSelected: Boolean = false,
) {
	@Ignore
	var avatar: Int? = null
}
