package com.example.kalanacommerce.presentation.screen.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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

    // 1. Inject ThemeManager untuk akses preferensi tema user
    private val themeManager: ThemeManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        // 2. Setup Background Berdasarkan Tema
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
        // Pastikan di layout XML, view dengan id splash_background adalah <ImageView>
        val background = findViewById<ImageView>(R.id.splash_background)
        val splashText = findViewById<android.widget.TextView>(R.id.splash_text) // 1. Ambil ID Text

        // Gunakan lifecycleScope untuk membaca DataStore (karena butuh coroutine)
        lifecycleScope.launch {
            // Ambil settingan tema saat ini (ambil .first() agar hanya baca sekali)
            val themeSetting = themeManager.themeSettingFlow.first()

            // Cek apakah sistem HP sedang mode gelap
            val isSystemDark = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

            // Logika penentuan tema (Sama seperti di MainActivity)
            val isDarkTheme = when (themeSetting) {
                ThemeSetting.LIGHT -> false
                ThemeSetting.DARK -> true
                ThemeSetting.SYSTEM -> isSystemDark
            }

            // 2. Tentukan Resource Gambar & Warna Teks berdasarkan isDarkTheme
            if (isDarkTheme) {
                // TEMA GELAP
                background.setImageResource(R.drawable.profile_background_black)
                // Warna teks terang (Putih/Abu muda) agar terbaca di background gelap
                splashText.setTextColor(android.graphics.Color.parseColor("#EEEEEE"))
            } else {
                // TEMA TERANG
                background.setImageResource(R.drawable.profile_background_white)
                // Warna teks gelap (Hitam/Abu tua) agar terbaca di background terang
                splashText.setTextColor(android.graphics.Color.parseColor("#333333"))
            }
        }
    }

    private fun playAnimation() {
        val background = findViewById<View>(R.id.splash_background)
        val logo = findViewById<View>(R.id.splash_logo)
        val text = findViewById<View>(R.id.splash_text)

        // Initial State
        text.alpha = 0f
        text.translationY = 50f

        // Definisi Animasi
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