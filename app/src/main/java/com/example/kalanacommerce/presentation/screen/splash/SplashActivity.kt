package com.example.kalanacommerce.presentation.screen.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color // Import Color
import android.os.Build // Import Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager // Import WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat // Import WindowCompat
import androidx.lifecycle.lifecycleScope
import com.example.kalanacommerce.MainActivity
import com.example.kalanacommerce.R
import com.example.kalanacommerce.data.local.datastore.ThemeManager
import com.example.kalanacommerce.data.local.datastore.ThemeSetting
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val SPLASH_SCREEN_DELAY: Long = 3000

    private val themeManager: ThemeManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // --- TAMBAHAN: KONFIGURASI FULL SCREEN (EDGE-TO-EDGE) ---
        // Letakkan kode ini SEBELUM setContentView agar efeknya langsung terasa saat layout dimuat

        // 1. Izinkan konten digambar di belakang system bar (Status Bar & Nav Bar)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 2. Ubah warna bar menjadi transparan
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        // 3. Khusus HP berponi (Notch) - Android 9 (Pie) ke atas
        // Agar gambar background melebar memenuhi area poni
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        // ---------------------------------------------------------

        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        setupThemeBackground()

        playAnimation()

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, SPLASH_SCREEN_DELAY)
    }

    private fun setupThemeBackground() {
        val background = findViewById<ImageView>(R.id.splash_background)
        val splashText = findViewById<android.widget.TextView>(R.id.splash_text)

        lifecycleScope.launch {
            val themeSetting = themeManager.themeSettingFlow.first()
            val isSystemDark = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

            val isDarkTheme = when (themeSetting) {
                ThemeSetting.LIGHT -> false
                ThemeSetting.DARK -> true
                ThemeSetting.SYSTEM -> isSystemDark
            }

            if (isDarkTheme) {
                background.setImageResource(R.drawable.profile_background_black)
                splashText.setTextColor(Color.parseColor("#EEEEEE"))
            } else {
                background.setImageResource(R.drawable.profile_background_white)
                splashText.setTextColor(Color.parseColor("#333333"))
            }
        }
    }

    private fun playAnimation() {
        val background = findViewById<View>(R.id.splash_background)
        val logo = findViewById<View>(R.id.splash_logo)
        val text = findViewById<View>(R.id.splash_text)

        text.alpha = 0f
        text.translationY = 50f

        val logoScaleX = ObjectAnimator.ofFloat(logo, View.SCALE_X, 0.5f, 1f).setDuration(1000)
        val logoScaleY = ObjectAnimator.ofFloat(logo, View.SCALE_Y, 0.5f, 1f).setDuration(1000)
        val logoAlpha = ObjectAnimator.ofFloat(logo, View.ALPHA, 0f, 1f).setDuration(1000)

        val textAlpha = ObjectAnimator.ofFloat(text, View.ALPHA, 1f).setDuration(800)
        val textTransY = ObjectAnimator.ofFloat(text, View.TRANSLATION_Y, 0f).setDuration(800)

        val bgScaleX = ObjectAnimator.ofFloat(background, View.SCALE_X, 1.2f, 1f).setDuration(2500)
        val bgScaleY = ObjectAnimator.ofFloat(background, View.SCALE_Y, 1.2f, 1f).setDuration(2500)

        AnimatorSet().apply {
            playTogether(logoScaleX, logoScaleY, logoAlpha, bgScaleX, bgScaleY)
            play(textAlpha).with(textTransY).after(logoAlpha)
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }
}