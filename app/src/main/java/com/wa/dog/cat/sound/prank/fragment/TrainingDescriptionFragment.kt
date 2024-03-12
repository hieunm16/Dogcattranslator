package com.wa.dog.cat.sound.prank.fragment

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wa.dog.cat.sound.prank.databinding.FragmentDesTrainingBinding
import com.wa.dog.cat.sound.prank.databinding.FragmentSoundBinding
import com.wa.dog.cat.sound.prank.databinding.FragmentTrainingBinding
import com.wa.dog.cat.sound.prank.model.TrainingItem

class TrainingDescriptionFragment:Fragment() {

    private lateinit var binding: FragmentDesTrainingBinding
    private var trainingItem: TrainingItem? = null

    companion object{
        val TAG = "TrainingDescriptionFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDesTrainingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initControl()
    }

    private fun initControl() {
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        if (arguments?.containsKey(TrainingFragment.KEY_TRAINING_ITEM) == true) {
            trainingItem = arguments?.getSerializable(TrainingFragment.KEY_TRAINING_ITEM) as TrainingItem?
            binding.toolbar.title = trainingItem?.title
            trainingItem?.icon?.let { binding.imgDesTraining.setImageResource(it) }
            binding.desTraining.text = Html.fromHtml(trainingItem?.des)
        }
    }


}