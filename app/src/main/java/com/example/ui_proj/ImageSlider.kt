package com.example.ui_proj.features

import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import com.example.ui_proj.R

val imageList = listOf(R.drawable.image_1,R.drawable.image_2, R.drawable.image_3, R.drawable.image_4, R.drawable.image_5, R.drawable.image_6,R.drawable.image_7,R.drawable.image_8,R.drawable.image_9,R.drawable.image_10)

fun setupImageSlider(
    context: Context,
    changingImagesView: ImageView,
    prevBtn: ImageButton,
    nextBtn: ImageButton,
) {
    var currentIndex = 0
    val imageCount = imageList.size

    val slideInRightAnim = AnimationUtils.loadAnimation(context, R.anim.slide_in_right)
    val slideInLeftAnim = AnimationUtils.loadAnimation(context, R.anim.slide_in_left)
    val slideOutLeftAnim = AnimationUtils.loadAnimation(context, R.anim.slide_out_left)
    val slideOutRightAnim = AnimationUtils.loadAnimation(context, R.anim.slide_out_right)

    prevBtn.setOnClickListener {
        changingImagesView.startAnimation(slideOutRightAnim)
        slideOutRightAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                currentIndex = if (currentIndex == 0) imageCount - 1 else currentIndex - 1
                changingImagesView.setImageResource(imageList[currentIndex])
                changingImagesView.startAnimation(slideInLeftAnim)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    nextBtn.setOnClickListener {
        changingImagesView.startAnimation(slideOutLeftAnim)
        slideOutLeftAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                currentIndex = (currentIndex + 1) % imageCount
                changingImagesView.setImageResource(imageList[currentIndex])
                changingImagesView.startAnimation(slideInRightAnim)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }
}
