package com.wa.dog.cat.sound.prank.activities

import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.View
import androidx.annotation.RequiresApi
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.dog.cat.sound.prank.R
import com.wa.dog.cat.sound.prank.databinding.ActivityResultBinding
import com.wa.dog.cat.sound.prank.databinding.AdNativeVideoBinding
import com.wa.dog.cat.sound.prank.extension.gone
import com.wa.dog.cat.sound.prank.extension.setFullScreen
import com.wa.dog.cat.sound.prank.fragment.TranslatorFragment
import com.wa.dog.cat.sound.prank.utils.RemoteConfigHelper
import com.wa.dog.cat.sound.prank.utils.RemoteConfigKey
import com.wa.dog.cat.sound.prank.utils.ads.NativeAdsUtils
import com.wa.dog.cat.sound.prank.utils.device.DeviceUtils
import java.io.File
import java.util.Random

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private val mediaPlayer: MediaPlayer by lazy {
        MediaPlayer()
    }
    private var lastPlayedRawResourceId: Int? = null
    private var isPlaying: Boolean = false
    private val remoteConfigHelper: RemoteConfigHelper by lazy { RemoteConfigHelper() }



    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullScreen()
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

        binding.llVoiceBefore.icPlay.setOnClickListener {
            Handler(mainLooper).post {
                if (isPlaying) {
                    // Nếu đang phát, tạm dừng và cập nhật UI
                    binding.llVoiceBefore.icPlay.setBackgroundResource(R.drawable.ic_play_result)
                    mediaPlayer.pause()
                    binding.llVoiceBefore.ltLoading.cancelAnimation()
                } else {
                    // Nếu đang tạm dừng hoặc chưa bắt đầu phát
                    binding.llVoiceBefore.icPlay.setBackgroundResource(R.drawable.ic_pause_result)
                    if (!mediaPlayer.isPlaying) {
                        mediaPlayer.reset()
                        mediaPlayer.setDataSource(getRecordingFilePath())
                        mediaPlayer.prepareAsync()
                        binding.llVoiceBefore.ltLoading.cancelAnimation()

                        mediaPlayer.setOnPreparedListener {
                            // Khi MediaPlayer đã chuẩn bị xong, bắt đầu phát
                            mediaPlayer.start()
                            setupLottie()
                        }

                        mediaPlayer.setOnCompletionListener {
                            // Đảm bảo giải phóng MediaPlayer sau khi phát xong
                            mediaPlayer.reset()
                            binding.llVoiceBefore.ltLoading.cancelAnimation()


                            // Cập nhật UI khi âm thanh kết thúc
                            binding.llVoiceBefore.icPlay.setBackgroundResource(R.drawable.ic_play_result)
                            isPlaying = false
                        }
                    } else {
                        // Nếu đang phát, tiếp tục phát và cập nhật UI
                        mediaPlayer.start()
                        setupLottie()
                    }
                }
                isPlaying = !isPlaying
            }
        }

        binding.llVoiceAfter.icPlay.setOnClickListener {
            playRandomFile()
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

//    private fun playRecording() {
//        val filePath = getRecordingFilePath()
//
//        if (filePath.isNotEmpty()) {
//            try {
//                mediaPlayer.setDataSource(filePath)
//
//                // Sử dụng prepareAsync để chuẩn bị đồng bộ
//                mediaPlayer.prepareAsync()
//
//                mediaPlayer.setOnPreparedListener {
//                    // Khi MediaPlayer đã chuẩn bị xong, bắt đầu phát
//                    mediaPlayer.start()
//                }
//
//                mediaPlayer.setOnCompletionListener {
//                    // Đảm bảo giải phóng MediaPlayer sau khi phát xong
//                    mediaPlayer.release()
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//
//                // In thông báo lỗi vào Logcat
//                Log.e("MediaPlayerError", "Error while playing recording: ${e.message}")
//
//                // Hiển thị thông báo lỗi cho người dùng (có thể thay đổi tùy ý)
//                Toast.makeText(this, "Error playing recording", Toast.LENGTH_SHORT).show()
//            }
//        } else {
//            // Hiển thị thông báo khi không tìm thấy tệp ghi âm
//            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show()
//        }
//    }


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