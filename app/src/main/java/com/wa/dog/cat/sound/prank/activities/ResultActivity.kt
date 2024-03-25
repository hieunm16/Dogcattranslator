package com.wa.dog.cat.sound.prank.activities

import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.dog.cat.sound.prank.R
import com.wa.dog.cat.sound.prank.databinding.ActivityResultBinding
import com.wa.dog.cat.sound.prank.databinding.AdNativeVideoBinding
import com.wa.dog.cat.sound.prank.extension.gone
import com.wa.dog.cat.sound.prank.extension.setFullScreen
import com.wa.dog.cat.sound.prank.extension.setStatusBarColor
import com.wa.dog.cat.sound.prank.fragment.TranslatorFragment
import com.wa.dog.cat.sound.prank.utils.RemoteConfigHelper
import com.wa.dog.cat.sound.prank.utils.RemoteConfigKey
import com.wa.dog.cat.sound.prank.utils.ads.NativeAdsUtils
import com.wa.dog.cat.sound.prank.utils.device.DeviceUtils
import java.io.File
import java.io.IOException
import java.util.Random

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private val mediaPlayer: MediaPlayer by lazy {
        MediaPlayer()
    }
    private var lastPlayedRawResourceId: Int? = null
    private var isPlaying: Boolean = false
    private var isPlayingAfter = false
    private var isPlayingBefore = false

    private val remoteConfigHelper: RemoteConfigHelper by lazy { RemoteConfigHelper() }



    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setStatusBarColor("#ffffff")
        setContentView(binding.root)
        remoteConfigHelper.loadConfig()
        initControl()

    }



    override fun onStart() {
        super.onStart()
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.SHOW_ADS_NATIVE_RESULT)) {
            val adConfig = FirebaseRemoteConfig.getInstance()
                .getString(RemoteConfigKey.KEY_SHOW_ADS_NATIVE_RESULT)
            if (adConfig.isNotEmpty()) {
                loadNativeAds(adConfig)
            } else {
                loadNativeAds(getString(R.string.native_result))
            }
        }
    }
    // Function to stop llVoiceBefore playback
    private fun stopBeforePlayback() {
        binding.llVoiceBefore.icPlay.setBackgroundResource(R.drawable.ic_play_result)
        mediaPlayer.pause()
        binding.llVoiceBefore.ltLoading.cancelAnimation()
        isPlayingBefore = false
    }

    // Function to stop llVoiceAfter playback
    private fun stopAfterPlayback() {
        binding.llVoiceAfter.icPlay.setBackgroundResource(R.drawable.ic_play_result)
        mediaPlayer.pause()
        binding.llVoiceAfter.ltLoading.cancelAnimation()
        isPlayingAfter = false
    }

    // Function to play llVoiceBefore
    private fun playBefore() {
        binding.llVoiceBefore.icPlay.setBackgroundResource(R.drawable.ic_pause_result)
        try {
            val filePath = getRecordingFilePath()

            mediaPlayer.reset()
            mediaPlayer.setDataSource(filePath)
            mediaPlayer.prepareAsync()
            binding.llVoiceBefore.ltLoading.cancelAnimation()

            mediaPlayer.setOnPreparedListener {
                mediaPlayer.start()
                setupLottie()
            }

            mediaPlayer.setOnCompletionListener {
                mediaPlayer.reset()
                binding.llVoiceBefore.ltLoading.cancelAnimation()
                binding.llVoiceBefore.icPlay.setBackgroundResource(R.drawable.ic_play_result)
                isPlayingBefore = false
            }
            isPlayingBefore = true
        } catch (e: IOException) {
            // Handle IOException
            Log.e("playBefore", "IOException: ${e.message}", e)
            // Display an error message to the user or handle it gracefully
        }
    }


    // Function to play llVoiceAfter
    private fun playAfter() {
        val rawResourceIds = getSoundResourceIds()
        if (rawResourceIds.isNotEmpty()) {
            val randomRawResourceId = getRandomResourceId(rawResourceIds)
            val playResourceId = lastPlayedRawResourceId ?: randomRawResourceId

            binding.llVoiceAfter.icPlay.setBackgroundResource(R.drawable.ic_pause_result)
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.reset()
                mediaPlayer.setDataSource(resources.openRawResourceFd(playResourceId))
                mediaPlayer.prepareAsync()
                binding.llVoiceAfter.ltLoading.cancelAnimation()

                mediaPlayer.setOnPreparedListener {
                    mediaPlayer.start()
                    lastPlayedRawResourceId = playResourceId
                    binding.llVoiceAfter.ltLoading.setAnimation(R.raw.lt_record_loading)
                    binding.llVoiceAfter.ltLoading.loop(true)
                    binding.llVoiceAfter.ltLoading.playAnimation()
                }

                mediaPlayer.setOnCompletionListener {
                    mediaPlayer.reset()
                    binding.llVoiceAfter.ltLoading.cancelAnimation()
                    binding.llVoiceAfter.icPlay.setBackgroundResource(R.drawable.ic_play_result)
                    isPlayingAfter = false
                }
            } else {
                mediaPlayer.start()
                lastPlayedRawResourceId = playResourceId
                binding.llVoiceAfter.ltLoading.setAnimation(R.raw.lt_record_loading)
                binding.llVoiceAfter.ltLoading.loop(true)
                binding.llVoiceAfter.ltLoading.playAnimation()
            }
            isPlayingAfter = true
        }
    }



    @RequiresApi(Build.VERSION_CODES.S)
    private fun initControl() {
        checkAnimal()
        setupLottieMain()
        binding.llVoiceBefore.icPlay.setBackgroundResource(R.drawable.ic_play_result)
        binding.llVoiceAfter.icPlay.setBackgroundResource(R.drawable.ic_play_result)
        binding.btnBackTranslating.setOnClickListener {
            val intent = Intent(this, OverviewActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Click listener for llVoiceBefore
        binding.llVoiceBefore.icPlay.setOnClickListener {
            if (isPlayingBefore) {
                stopBeforePlayback()
            } else {
                stopAfterPlayback()
                playBefore()
            }
        }

// Click listener for llVoiceAfter
        binding.llVoiceAfter.icPlay.setOnClickListener {
            if (isPlayingAfter) {
                stopAfterPlayback()
            } else {
                stopBeforePlayback()
                playAfter()
            }
        }


    }

    private fun setupLottie() {
        binding.llVoiceBefore.ltLoading.loop(true)
        binding.llVoiceBefore.ltLoading.playAnimation()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun checkAnimal() {
        if (TranslatorFragment.isAnimal) {
            binding.llVoiceAfter.root.visibility = View.GONE
            binding.llMessAfter.root.visibility = View.VISIBLE
            binding.llVoiceBefore.icResult.setBackgroundResource(R.drawable.ic_animal)
            binding.llMessAfter.icResult.setBackgroundResource(R.drawable.ic_human)
            setUpListAnswer()
        } else {
            binding.llVoiceAfter.root.visibility = View.VISIBLE
            binding.llMessAfter.root.visibility = View.GONE
            binding.llVoiceBefore.icResult.setBackgroundResource(R.drawable.ic_human)
            binding.llVoiceAfter.icResult.setBackgroundResource(R.drawable.ic_animal)
        }
    }


    private fun getRecordingFilePath(): String {
        try {
            val musicDirectory: File? = this.getExternalFilesDir(Environment.DIRECTORY_MUSIC)

            // Kiểm tra xem thư mục đã được tạo chưa
            musicDirectory?.mkdirs()

            val file = File(musicDirectory, "record.mp3")
            return file.path
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer.stop()
    }

    fun playRandomFile() {
        val rawResourceIds = getSoundResourceIds()
        if (rawResourceIds.isNotEmpty()) {
            val randomRawResourceId = getRandomResourceId(rawResourceIds)

            // Sử dụng ID của file trước đó nếu có
            val playResourceId = lastPlayedRawResourceId ?: randomRawResourceId

            if (isPlaying) {
                // Nếu đang phát, tạm dừng và cập nhật UI
                binding.llVoiceAfter.icPlay.setBackgroundResource(R.drawable.ic_play_result)
                mediaPlayer.pause()
                binding.llVoiceAfter.ltLoading.cancelAnimation()

            } else {
                // Nếu đang tạm dừng hoặc chưa bắt đầu phát
                binding.llVoiceAfter.icPlay.setBackgroundResource(R.drawable.ic_pause_result)
                if (!mediaPlayer.isPlaying) {
                    mediaPlayer.reset()
                    mediaPlayer.setDataSource(resources.openRawResourceFd(playResourceId))
                    mediaPlayer.prepareAsync()
                    binding.llVoiceAfter.ltLoading.cancelAnimation()


                    mediaPlayer.setOnPreparedListener {
                        // Khi MediaPlayer đã chuẩn bị xong, bắt đầu phát
                        mediaPlayer.start()
                        lastPlayedRawResourceId = playResourceId
                        binding.llVoiceAfter.ltLoading.setAnimation(R.raw.lt_record_loading)
                        binding.llVoiceAfter.ltLoading.loop(true)
                        binding.llVoiceAfter.ltLoading.playAnimation()
                    }

                    mediaPlayer.setOnCompletionListener {
                        // Đảm bảo giải phóng MediaPlayer sau khi phát xong
                        mediaPlayer.reset()
                        binding.llVoiceAfter.ltLoading.cancelAnimation()

                        // Cập nhật UI khi âm thanh kết thúc
                        binding.llVoiceAfter.icPlay.setBackgroundResource(R.drawable.ic_play_result)
                        isPlaying = false
                    }
                } else {
                    // Nếu đang phát, tiếp tục phát và cập nhật UI
                    mediaPlayer.start()
                    lastPlayedRawResourceId = playResourceId
                    binding.llVoiceAfter.ltLoading.setAnimation(R.raw.lt_record_loading)
                    binding.llVoiceAfter.ltLoading.loop(true)
                    binding.llVoiceAfter.ltLoading.playAnimation()
                }
            }
            isPlaying = !isPlaying
        }
    }

    private fun setupLottieMain() {
        val animal = HomeActivity.animal

        val animationResId = when (animal) {
            "dog" -> R.raw.lt_dog_translator
            "cat" -> R.raw.lt_cat_translator
            "hamster" -> R.raw.lt_hamster_translator
            else -> R.raw.lt_dog_translator // Thêm giá trị mặc định hoặc xử lý nếu giá trị animal không khớp với bất kỳ giá trị nào đã được định nghĩa
        }

        if (animationResId != 0) {
            binding.ltResult.setAnimation(animationResId)
        }

        binding.ltResult.loop(true)
        binding.ltResult.playAnimation()
    }


    private fun getRandomResourceId(rawResourceIds: List<Int>): Int {
        val randomIndex = Random().nextInt(rawResourceIds.size)
        return rawResourceIds[randomIndex]
    }

    private fun getRawResourceIds(): List<Int> {
        val fieldArray = R.raw::class.java.fields
        return fieldArray.mapNotNull {
            try {
                it.getInt(null)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun getSoundResourceIds(): List<Int> {
        val fieldArray = R.raw::class.java.fields
        return fieldArray.filter { field ->
            when (HomeActivity.animal) {
                "dog" -> field.name.startsWith("dog_")
                "cat" -> field.name.startsWith("cat_")
                "hamster" -> field.name.startsWith("hamster_")
                else -> false
            }
        }.mapNotNull { field ->
            try {
                field.getInt(null)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
                null
            }
        }
    }


    private fun setUpListAnswer() {
        binding.llMessAfter.tvMess.text = getRandomText()

    }

    private fun getRandomText(): String {
        val textList = arrayListOf(
            "I want something to eat",
            "Come and play with me",
            "It makes me nervous",
            "I love you",
            "I'm hungry, mealtime!",
            "I did not like the silence, are you with me?",
            "I want to smell clean, let's take a shower",
            "I'm so bored, I want to take a tour",
            "Don't you think I'm cute enough?",
            "Please listen to me, It's my turn to talk",
            "Shall we play hide and seek?",
            "I'm very tired, will you be quiet?",
            "I would appreciate it if you show some love",
            "I'm starting to get hungry, is my milk ready?",
            "Can you tell a funny story?",
            "My tooth is itching, can you help me?",
            "Change my diaper because it's so wet",
            "My stomach is swollen, so I have to burp",
            "I want you to take care of me",
            "Give me simple toys to entertain me",
            "It's time to sleep for me",
            "I want you to take me on your lap",
            "I just want to cry"
        )
        val random = Random()
        val index = random.nextInt(textList.size)
        return textList[index]
    }


    override fun onBackPressed() {
        finish()
    }

    private fun loadNativeAds(keyAds:String) {
        if (!DeviceUtils.checkInternetConnection(this@ResultActivity)) binding.rlNative.gone()
        NativeAdsUtils.instance.loadNativeAds(
            this@ResultActivity,
            keyAds
        ) { nativeAds ->
            if (nativeAds != null) {
                binding.frNativeAds.removeAllViews()
                val adNativeVideoBinding = AdNativeVideoBinding.inflate(layoutInflater)
                NativeAdsUtils.instance.populateNativeAdVideoView(
                    nativeAds,
                    adNativeVideoBinding.root
                )
                binding.frNativeAds.addView(adNativeVideoBinding.root)
            } else {
                binding.rlNative.gone()
            }
        }
    }


}