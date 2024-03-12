package com.wa.dog.cat.sound.prank.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.base.app.base.ui.base.base.BaseViewModel
import com.wa.dog.cat.sound.prank.extension.getHeightStatusBar
import com.wa.dog.cat.sound.prank.extension.lockPortraitOrientation
import com.wa.dog.cat.sound.prank.utils.device.getScreenHeight

abstract class BaseBindingActivity<B : ViewDataBinding, VM : BaseViewModel> : BaseActivity() {
    lateinit var binding: B
    lateinit var viewModel: VM
    var isDispatchTouchEvent = true

    abstract val layoutId: Int
    abstract fun getViewModel(): Class<VM>
    abstract fun setupView(savedInstanceState: Bundle?)
    abstract fun setupData()

    private var toast: Toast? = null
    private var handlerToast = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        this.lockPortraitOrientation()
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        viewModel = ViewModelProvider(this)[getViewModel()]
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.setLightStatusBars(true)
        setupView(savedInstanceState)
        setupData()
    }

    override fun onStop() {
        super.onStop()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        toast?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        toast?.cancel()
        handlerToast.removeCallbacksAndMessages(null)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        window?.decorView?.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return !isDispatchTouchEvent || super.dispatchTouchEvent(ev)
    }

    protected fun isDispatchTouchEvent(time: Int) {
        isDispatchTouchEvent = false
        Handler(Looper.getMainLooper()).postDelayed({ isDispatchTouchEvent = true }, time.toLong())
    }

    fun toast(text: String) {
        toast?.cancel()
        toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
        toast?.show()

        handlerToast.postDelayed({
            toast?.cancel()
        }, 1500)
    }

    private fun Window.setLightStatusBars(b: Boolean) {
        WindowCompat.getInsetsController(this, decorView).isAppearanceLightStatusBars = b
    }

    fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    fun setMarginStatusBar(textView: View, marginTop: Int) {
        val params = textView.layoutParams as ViewGroup.MarginLayoutParams
        //height status bar = 30, margin top = 20, height screen device code = 2400
        params.topMargin = getHeightStatusBar() + dpToPx(marginTop) * getScreenHeight() / 2400

    }
}