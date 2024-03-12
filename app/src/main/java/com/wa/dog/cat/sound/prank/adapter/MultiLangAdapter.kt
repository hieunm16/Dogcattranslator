package com.wa.dog.cat.sound.prank.adapter

import androidx.recyclerview.widget.DiffUtil
import com.wa.dog.cat.sound.prank.data.model.LanguageUI
import com.base.app.base.ui.base.base.BaseBindingAdapterDiff
import com.wa.dog.cat.sound.prank.R
import com.wa.dog.cat.sound.prank.databinding.ItemMultiLangBinding
import com.wa.dog.cat.sound.prank.utils.device.setOnSafeClick

class MultiLangAdapter : BaseBindingAdapterDiff<LanguageUI, ItemMultiLangBinding>(
	object : DiffUtil.ItemCallback<LanguageUI>() {
		override fun areItemsTheSame(oldItem: LanguageUI, newItem: LanguageUI): Boolean {
			return oldItem.code == newItem.code
		}

		override fun areContentsTheSame(oldItem: LanguageUI, newItem: LanguageUI): Boolean {
			return oldItem == newItem
		}
	}
) {
	private var oldPosition: Int = -1
		set(value) {
			field = value
			notifyItemChanged(value)
		}

	var newPosition: Int = -1
		set(value) {
			oldPosition = field
			field = value
			notifyItemChanged(value)
		}

	var callBack: (Int, LanguageUI) -> Unit = { _, _ -> }
	override fun onBindViewHolderBase(holder: BaseHolder<ItemMultiLangBinding>, position: Int) {
		with(getItem(holder.adapterPosition)) {
			holder.binding.apply {
				if (holder.adapterPosition == newPosition) {
					imRadioButton.setImageResource(R.drawable.ic_language_selected)
					itemLanguage.setBackgroundResource(R.drawable.bg_language_selected)
				} else {
					imRadioButton.setImageResource(R.drawable.ic_language_unselected)
					itemLanguage.setBackgroundResource(R.drawable.bg_language_unselected)
				}
				tvCountry.text = name
				avatar?.let { imFlag.setImageResource(it) }
				root.setOnSafeClick {
					callBack(position, this@with)
					newPosition = holder.adapterPosition
				}
			}
		}
	}

	override val layoutIdItem: Int
		get() = R.layout.item_multi_lang
}