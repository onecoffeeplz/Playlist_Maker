package com.example.playlistmaker.root.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityRootBinding
import com.example.playlistmaker.media.ui.MediaFragment

class RootActivity : AppCompatActivity() {

    private var _binding: ActivityRootBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for ActivityRootBinding must not be null!")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                this.add(R.id.rootFragmentContainerView, MediaFragment())
            }
        }
    }
}