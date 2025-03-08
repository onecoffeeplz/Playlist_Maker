package com.example.playlistmaker.main.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.media.ui.MediaActivity
import com.example.playlistmaker.search.ui.SearchActivity
import com.example.playlistmaker.databinding.ActivityMainBinding

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
    }
}