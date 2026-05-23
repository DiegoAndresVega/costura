package com.example.costura.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.costura.MainActivity
import com.example.costura.R
import com.example.costura.databinding.ActivityOnboardingBinding
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val slides = listOf(
            OnboardingSlide(
                iconRes = R.drawable.ic_onboarding_calcular,
                titulo = getString(R.string.onboarding_1_titulo),
                descripcion = getString(R.string.onboarding_1_desc)
            ),
            OnboardingSlide(
                iconRes = R.drawable.ic_onboarding_explorar,
                titulo = getString(R.string.onboarding_2_titulo),
                descripcion = getString(R.string.onboarding_2_desc)
            ),
            OnboardingSlide(
                iconRes = R.drawable.ic_onboarding_subir,
                titulo = getString(R.string.onboarding_3_titulo),
                descripcion = getString(R.string.onboarding_3_desc)
            )
        )

        val adapter = OnboardingAdapter(slides)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val esUltimo = position == slides.lastIndex
                binding.btnAccion.text = getString(
                    if (esUltimo) R.string.onboarding_empezar else R.string.onboarding_siguiente
                )
            }
        })

        binding.btnAccion.setOnClickListener {
            val posicionActual = binding.viewPager.currentItem
            if (posicionActual < slides.lastIndex) {
                binding.viewPager.currentItem = posicionActual + 1
            } else {
                completarOnboarding()
            }
        }
    }

    private fun completarOnboarding() {
        getSharedPreferences("costura_prefs", MODE_PRIVATE)
            .edit()
            .putBoolean("onboarding_completado", true)
            .apply()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
