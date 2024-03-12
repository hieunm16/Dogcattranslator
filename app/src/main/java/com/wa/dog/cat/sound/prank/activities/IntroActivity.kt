package com.wa.dog.cat.sound.prank.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.google.android.material.tabs.TabLayout
import com.wa.dog.cat.sound.prank.R
import com.wa.dog.cat.sound.prank.adapter.IntroAdapter
import com.wa.dog.cat.sound.prank.databinding.ActivityIntroBinding
import com.wa.dog.cat.sound.prank.extension.setFullScreen
import com.wa.dog.cat.sound.prank.model.ScreenItem
import com.wa.dog.cat.sound.prank.utils.Constant

class IntroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding
    private lateinit var introViewPagerAdapter: IntroAdapter
    private var position = 0
    private lateinit var btnAnim: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (restorePrefData()) {
            val intent = Intent(this@IntroActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        btnAnim = AnimationUtils.loadAnimation(applicationContext, R.anim.button_animation)

        val mList = ArrayList<ScreenItem>()
        mList.add(ScreenItem(getString(R.string.translator), getString(R.string.intro_1), R.drawable.ic_intro_1))
        mList.add(ScreenItem(getString(R.string.sound), getString(R.string.intro_2), R.drawable.ic_intro_2))
        mList.add(ScreenItem(getString(R.string.training), getString(R.string.intro_3) , R.drawable.ic_intro_3))

        // setup viewpager
        introViewPagerAdapter = IntroAdapter(this, mList)
        binding.screenViewpager.adapter = introViewPagerAdapter

        // setup tablayout with viewpager
        binding.tabIndicator.setupWithViewPager(binding.screenViewpager)
        binding.btnNext.setOnClickListener {
            position = binding.screenViewpager.currentItem
            if (position < mList.size) {
                position++
                binding.screenViewpager.currentItem = position
            }

            if (position == mList.size - 1) { // when we reach the last screen
                loadLastScreen()
            }
        }

        // tablayout add change listener
        binding.tabIndicator.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == mList.size - 1) {
                    loadLastScreen()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // Get Started button click listener
        binding.btnGetStarted.setOnClickListener {
            // open main activity
            val mainActivity = Intent(applicationContext, HomeActivity::class.java)
            startActivity(mainActivity)
            // also we need to save a boolean value to storage so next time when the user runs the app
            // we could know that he has already checked the intro screen activity
            // I'm going to use shared preferences for that process
            savePrefsData()
            finish()
        }

        // skip button click listener
        binding.tvSkip.setOnClickListener { binding.screenViewpager.currentItem = mList.size }
    }

    private fun restorePrefData(): Boolean {
        val pref = applicationContext.getSharedPreferences("myPrefs", MODE_PRIVATE)
        return pref.getBoolean("isIntroOpend", false)
    }

    private fun savePrefsData() {
        val pref = applicationContext.getSharedPreferences("myPrefs", MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean("isIntroOpend", true)
        editor.apply()
    }

    // show the GETSTARTED Button and hide the indicator and the next button
    private fun loadLastScreen() {
        binding.btnNext.visibility = View.INVISIBLE
        binding.btnGetStarted.visibility = View.VISIBLE
        binding.tvSkip.visibility = View.INVISIBLE
        binding.tabIndicator.visibility = View.INVISIBLE
        // TODO: ADD an animation to the getstarted button
        // setup animation
        binding.btnGetStarted.startAnimation(btnAnim)
    }
}