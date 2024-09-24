package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageClickListener : View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "button was pressed via anonymous class!", Toast.LENGTH_SHORT).show()
            }
        }
        binding.mainSearchButton.setOnClickListener(imageClickListener)

        binding.mainMediaButton.setOnClickListener{
            showToastMessage()
        }

        binding.mainSettingsButton.setOnClickListener{
            showToastMessage()
        }
    }

    private fun showToastMessage() {
        Toast.makeText(this, "button was pressed via lambda expression!", Toast.LENGTH_SHORT).show()
    }
}