package com.wa.dog.cat.sound.prank.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioManager
import android.media.MediaPlayer

class MediaPlayerUtils(
    private val context: Context
) {

    private var mediaPlayer: MediaPlayer? = null
    var uriSound = ""

    var onCompleteMusic: () -> Unit = {}

    fun isPlaying() = try {
        mediaPlayer?.isPlaying ?: false
    } catch (e: Exception) {
        false
    }


    fun seekToMusic(progress: Int) {
        kotlin.runCatching {
            mediaPlayer?.seekTo(progress)
        }.onFailure { it.printStackTrace() }
    }


    @SuppressLint("ServiceCast")
    fun initMusicWithAsset() {
        stopMusic()
        kotlin.runCatching {
            val assetFileDescriptor: AssetFileDescriptor = context.assets.openFd(uriSound)
            mediaPlayer = MediaPlayer().apply {
                setDataSource(
                    assetFileDescriptor.fileDescriptor,
                    assetFileDescriptor.startOffset,
                    assetFileDescriptor.length
                )
                prepare()
                setOnPreparedListener {
                    kotlin.runCatching {
                        start()
                        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                        val audioFocusChangeListener =
                            AudioManager.OnAudioFocusChangeListener { focusChange ->
                                when (focusChange) {
                                    AudioManager.AUDIOFOCUS_GAIN -> {

                                    }

                                    AudioManager.AUDIOFOCUS_LOSS -> {

                                    }

                                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                                    }
                                }
                            }

                        val result = audioManager.requestAudioFocus(
                            audioFocusChangeListener,
                            AudioManager.STREAM_MUSIC,
                            AudioManager.AUDIOFOCUS_GAIN
                        )
                    }.onFailure { it.printStackTrace() }
                }

                setOnCompletionListener {
                    onCompleteMusic()
                }
            }
        }.onFailure { it.printStackTrace() }
    }

    fun stopMusic() {

        kotlin.runCatching {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null

        }.onFailure { it.printStackTrace() }
    }

}