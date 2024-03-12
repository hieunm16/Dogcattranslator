package com.wa.dog.cat.sound.prank.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.wa.dog.cat.sound.prank.activities.ProcessingActivity
import java.io.File
import android.os.Handler
import com.wa.dog.cat.sound.prank.R
import com.wa.dog.cat.sound.prank.activities.HomeActivity
import com.wa.dog.cat.sound.prank.databinding.FragmentTranslatorBinding

@RequiresApi(Build.VERSION_CODES.S)
class TranslatorFragment : Fragment() {
    private lateinit var binding: FragmentTranslatorBinding

    private val RECORD_AUDIO_PERMISSION = arrayOf(Manifest.permission.RECORD_AUDIO)
    private val RECORD_AUDIO_REQUEST_CODE = 123
    private var elapsedTimeInSeconds = 0
    private var handler: Handler = Handler(Looper.getMainLooper())
    private var isTimerRunning = false
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false


    companion object {
        var isAnimal = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTranslatorBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initControl()
    }

    private fun goToHome() {
        val intent = Intent(requireContext(), HomeActivity::class.java)
        startActivity(intent)
    }

    private fun setupLottie() {
        val animal = HomeActivity.animal

        val animationResId = when (animal) {
            "dog" -> R.raw.lt_dog_translator
            "cat" -> R.raw.lt_cat_translator
            "hamster" -> R.raw.lt_hamster_translator
            else -> R.raw.lt_dog_translator  // Thêm giá trị mặc định hoặc xử lý nếu giá trị animal không khớp với bất kỳ giá trị nào đã được định nghĩa
        }

        if (animationResId != 0) {
            binding.ltTranslator.setAnimation(animationResId)
        }

        val imageResId = when (animal) {
            "dog" -> R.drawable.ic_dog
            "cat" -> R.drawable.ic_cat
            "hamster" -> R.drawable.ic_hamster
            else -> R.drawable.ic_dog  // Thêm giá trị mặc định hoặc xử lý nếu giá trị animal không khớp với bất kỳ giá trị nào đã được định nghĩa
        }

        if (imageResId != 0) {
            binding.imvAfter.setImageResource(imageResId)
        }

        binding.ltTranslator.loop(true)
        binding.ltTranslator.playAnimation()
    }


    private fun initControl() {
        isAnimal = false
        binding.toolbar.setNavigationOnClickListener { goToHome() }
        setupLottie()
        binding.icRecord.setOnClickListener {
            if (hasRecordAudioPermission()) {
                record()
            } else {
                // Nếu chưa có quyền, yêu cầu quyền
                requestRecordAudioPermission()
            }
        }

        binding.icCloseRecord.setOnClickListener {
            try {
                mediaRecorder?.stop()
                binding.imvTransfer.isEnabled = true
                mediaRecorder?.release()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
            binding.icDoneRecord.visibility = View.GONE
            binding.icCloseRecord.visibility = View.GONE
            stopTimer()
        }

        binding.icDoneRecord.setOnClickListener {
            try {
                mediaRecorder?.stop()
                binding.imvTransfer.isEnabled = true
                mediaRecorder?.release()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
            stopTimer()
            val intent = Intent(requireContext(), ProcessingActivity::class.java)
            startActivity(intent)

        }

        binding.imvTransfer.setOnClickListener {
            // Lấy Bitmap từ ImageView Before và After
            val bitmapBefore = (binding.imvBefore.drawable as? BitmapDrawable)?.bitmap
            val bitmapAfter = (binding.imvAfter.drawable as? BitmapDrawable)?.bitmap

            // Kiểm tra null để đảm bảo tính ổn định
            if (bitmapBefore != null && bitmapAfter != null) {
                // Đặt Bitmap cho ImageView Before và After
                binding.imvBefore.setImageBitmap(bitmapAfter)
                binding.imvAfter.setImageBitmap(bitmapBefore)

                // Đổi trạng thái của biến isAnimal
                isAnimal = !isAnimal

//                // Hiển thị hoặc ẩn ImageView tương ứng
//                if (isAnimal) {
//                    binding.imvDropDown.visibility = View.VISIBLE
//                    binding.imvDropDownAfter.visibility = View.GONE
//                } else {
//                    binding.imvDropDown.visibility = View.GONE
//                    binding.imvDropDownAfter.visibility = View.VISIBLE
//                }
            } else {
                // Xử lý khi không thể chuyển đổi Drawable thành Bitmap
                // Có thể hiển thị thông báo hoặc thực hiện các xử lý khác tùy thuộc vào yêu cầu của bạn
                Toast.makeText(requireContext(), "Error getting image", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun hasRecordAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestRecordAudioPermission() {
        requestPermissions(RECORD_AUDIO_PERMISSION, RECORD_AUDIO_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RECORD_AUDIO_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    record()
                } else {
                    // Người dùng từ chối cấp quyền ghi âm, bạn có thể thông báo cho họ
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.permission_record),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun record() {
        mediaRecorder = MediaRecorder()
        try {
            binding.icCloseRecord.visibility = View.VISIBLE
            binding.icDoneRecord.visibility = View.VISIBLE
            binding.imvTransfer.isEnabled = false
            startTimer()
            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mediaRecorder?.setOutputFile(getRecordingFilePath())
            mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            isRecording = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getRecordingFilePath(): String {
        try {
            val musicDirectory: File? = context?.getExternalFilesDir(Environment.DIRECTORY_MUSIC)

            // Kiểm tra xem thư mục đã được tạo chưa
            musicDirectory?.mkdirs()

            val file = File(musicDirectory, "record.mp3")
            return file.path
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    private fun startTimer() {
        handler.post(object : Runnable {
            override fun run() {
                // Cập nhật TextView với thời gian
                val minutes = elapsedTimeInSeconds / 60
                val seconds = elapsedTimeInSeconds % 60
                val formattedTime = String.format("%02d:%02d", minutes, seconds)
                binding.timeCountDown.text = formattedTime

                // Tăng thời gian lên mỗi giây
                elapsedTimeInSeconds++

                // Lặp lại mỗi giây
                handler.postDelayed(this, 1000)
            }
        })

        isTimerRunning = true
    }

    private fun stopTimer() {
        handler.removeCallbacksAndMessages(null)
        elapsedTimeInSeconds = 0
        binding.timeCountDown.text = "00:00"
        isTimerRunning = false
    }

    override fun onStop() {
        super.onStop()

        // Dừng ghi âm
        stopRecording()
    }

    private fun stopRecording() {
        if (isRecording) {
            try {
                // Dừng ghi âm
                mediaRecorder?.stop()
            } catch (e: IllegalStateException) {
                // Xử lý ngoại lệ IllegalStateException nếu cần
                e.printStackTrace()
            } finally {
                // Đặt biến isRecording về false
                isRecording = false

                // Dừng đếm thời gian hoặc các hoạt động khác liên quan đến việc ghi âm
                stopTimer()
            }
        }
    }


}