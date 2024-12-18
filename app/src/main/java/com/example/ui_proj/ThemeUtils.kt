// ThemeUtils.kt
package com.example.ui_proj.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import android.widget.ImageButton

fun toggleTheme(context: Context, themeToggle: ImageButton) {
    val currentMode = AppCompatDelegate.getDefaultNightMode()

    if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
        themeToggle.isSelected = false
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    } else {
        themeToggle.isSelected = true
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    saveThemeMode(context, currentMode)
}

fun saveThemeMode(context: Context, mode: Int) {
    val sharedPreferences = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putInt("theme_mode", mode)
    editor.apply()
}