package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivityMainBinding

const val PLAYLIST_MAKER_PREFERENCES = "pm_preferences"
const val DARK_THEME_ENABLED = "dark_theme_enabled"

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding for ActivityMainBinding must not be null!")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainSearchButton.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        binding.mainMediaButton.setOnClickListener {
            startActivity(Intent(this, MediaActivity::class.java))
        }

        binding.mainSettingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}