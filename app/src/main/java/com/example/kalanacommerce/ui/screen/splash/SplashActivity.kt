package com.example.kalanacommerce.ui.screen.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.kalanacommerce.MainActivity
import com.example.kalanacommerce.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val SPLASH_SCREEN_DELAY: Long = 3000 // Diperlama sedikit agar animasi selesai

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 1. Sembunyikan ActionBar jika ada (opsional)
        supportActionBar?.hide()

        // 2. Jalankan Animasi
        playAnimation()

        // 3. Pindah ke Main Activity setelah delay
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            // 4. Tambahkan transisi Fade Out agar perpindahannya halus (tidak kaget)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

            finish()
        }, SPLASH_SCREEN_DELAY)
    }

    private fun playAnimation() {
        // Ambil View dari Layout
        val background = findViewById<View>(R.id.splash_background)
        val logo = findViewById<View>(R.id.splash_logo)
        val text = findViewById<View>(R.id.splash_text)

        // --- Persiapan Awal (Initial State) ---
        // Buat teks transparan dulu agar bisa di-fade in
        text.alpha = 0f
        // Geser teks sedikit ke bawah agar nanti bisa naik
        text.translationY = 50f

        // --- Definisi Animasi ---

        // 1. Logo: Muncul dengan efek Scale (Membesar) dan Fade In
        val logoScaleX = ObjectAnimator.ofFloat(logo, View.SCALE_X, 0.5f, 1f).setDuration(1000)
        val logoScaleY = ObjectAnimator.ofFloat(logo, View.SCALE_Y, 0.5f, 1f).setDuration(1000)
        val logoAlpha = ObjectAnimator.ofFloat(logo, View.ALPHA, 0f, 1f).setDuration(1000)

        // 2. Teks: Muncul perlahan (Fade In) sambil naik ke posisi asli
        val textAlpha = ObjectAnimator.ofFloat(text, View.ALPHA, 1f).setDuration(800)
        val textTransY = ObjectAnimator.ofFloat(text, View.TRANSLATION_Y, 0f).setDuration(800)

        // 3. Background: Efek zoom in halus (opsional, memberikan efek kedalaman)
        val bgScaleX = ObjectAnimator.ofFloat(background, View.SCALE_X, 1.2f, 1f).setDuration(2500)
        val bgScaleY = ObjectAnimator.ofFloat(background, View.SCALE_Y, 1.2f, 1f).setDuration(2500)

        // --- Menjalankan Animasi Secara Berurutan ---
        AnimatorSet().apply {
            playTogether(logoScaleX, logoScaleY, logoAlpha, bgScaleX, bgScaleY) // Logo & BG main duluan
            play(textAlpha).with(textTransY).after(logoAlpha) // Teks muncul SETELAH logo selesai
            interpolator = AccelerateDecelerateInterpolator() // Gerakan lebih natural
            start()
        }
    }
}