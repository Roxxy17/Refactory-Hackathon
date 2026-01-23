package com.example.kalanacommerce.presentation.screen.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.example.kalanacommerce.MainActivity
import com.example.kalanacommerce.R
import com.example.kalanacommerce.data.local.datastore.ThemeManager
import com.example.kalanacommerce.data.local.datastore.ThemeSetting
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val SPLASH_SCREEN_DELAY: Long = 3000
    private val themeManager: ThemeManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // --- KONFIGURASI FULL SCREEN ---
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        // -------------------------------

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
        val footer = findViewById<TextView>(R.id.splash_footer)
        // Kita tidak lagi mengubah warna teks logo karena sekarang berupa Gambar (Image)

        lifecycleScope.launch {
            themeManager.themeSettingFlow.collect { themeSetting ->
                val isSystemDark = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                        Configuration.UI_MODE_NIGHT_YES

                val isDarkTheme = when (themeSetting) {
                    ThemeSetting.LIGHT -> false
                    ThemeSetting.DARK -> true
                    ThemeSetting.SYSTEM -> isSystemDark
                }

                if (isDarkTheme) {
                    background.setImageResource(R.drawable.profile_background_black)
                    footer.setTextColor(Color.WHITE)
                    // Catatan: Jika logo kamu teksnya warna hitam, pastikan kamu punya versi logo
                    // warna putih untuk dark mode, atau gunakan background yang tetap terang.
                } else {
                    background.setImageResource(R.drawable.splash_background_white)
                    footer.setTextColor(Color.BLACK)
                }
            }
        }
    }

    private fun playAnimation() {
        val background = findViewById<View>(R.id.splash_background)
        val mainLogo = findViewById<View>(R.id.iv_main_logo) // Logo Utuh
        val footer = findViewById<View>(R.id.splash_footer)

        // --- 1. SET POSISI AWAL (INVISIBLE) ---

        // Logo: Kecil & transparan
        mainLogo.alpha = 0f
        mainLogo.scaleX = 0.0f
        mainLogo.scaleY = 0.0f

        // Footer: Turun ke bawah
        footer.alpha = 0f
        footer.translationY = 80f

        // --- 2. DEFINISI ANIMASI ---

        // Background: Zoom Out perlahan
        val bgScaleX = ObjectAnimator.ofFloat(background, View.SCALE_X, 1.3f, 1f).setDuration(2500)
        val bgScaleY = ObjectAnimator.ofFloat(background, View.SCALE_Y, 1.3f, 1f).setDuration(2500)

        // Logo: Pop Up Effect (Membal)
        val logoAlpha = ObjectAnimator.ofFloat(mainLogo, View.ALPHA, 0f, 1f).setDuration(800)
        val logoScaleX = ObjectAnimator.ofFloat(mainLogo, View.SCALE_X, 0f, 1f).setDuration(800)
        val logoScaleY = ObjectAnimator.ofFloat(mainLogo, View.SCALE_Y, 0f, 1f).setDuration(800)

        // Footer: Naik & Fade In
        val footerAlpha = ObjectAnimator.ofFloat(footer, View.ALPHA, 1f).setDuration(600)
        val footerTransY = ObjectAnimator.ofFloat(footer, View.TRANSLATION_Y, 0f).setDuration(600)

        // --- 3. EKSEKUSI ANIMASI ---
        AnimatorSet().apply {
            play(bgScaleX).with(bgScaleY)

            // Logo muncul
            play(logoScaleX).with(logoScaleY).with(logoAlpha)

            // Footer muncul setelah logo mulai (delayed 300ms)
            play(footerAlpha).with(footerTransY).after(300)

            interpolator = OvershootInterpolator(1.2f)
            start()
        }
    }
}