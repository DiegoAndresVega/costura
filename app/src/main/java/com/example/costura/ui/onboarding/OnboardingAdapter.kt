package com.example.costura.ui.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.example.costura.databinding.ItemOnboardingSlideBinding

data class OnboardingSlide(
    @DrawableRes val iconRes: Int,
    val titulo: String,
    val descripcion: String
)

class OnboardingAdapter(private val slides: List<OnboardingSlide>) :
    RecyclerView.Adapter<OnboardingAdapter.SlideViewHolder>() {

    inner class SlideViewHolder(private val binding: ItemOnboardingSlideBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(slide: OnboardingSlide) {
            binding.ivIcono.setImageResource(slide.iconRes)
            binding.tvTitulo.text = slide.titulo
            binding.tvDescripcion.text = slide.descripcion
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideViewHolder {
        val binding = ItemOnboardingSlideBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SlideViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SlideViewHolder, position: Int) =
        holder.bind(slides[position])

    override fun getItemCount() = slides.size
}
