package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private var _binding: ActivitySettingsBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for ActivitySettingsBinding must not be null!")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.settingsToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        
        binding.settingsTheme.setOnCheckedChangeListener { _, isChecked ->
            (applicationContext as App).switchTheme(isChecked)
        }

        binding.settingsShare.setOnClickListener {
            val shareThisApp = Intent(Intent.ACTION_SEND)
            shareThisApp.setType("text/plain")
            shareThisApp.putExtra(Intent.EXTRA_TEXT, getString(R.string.ya_android_course))
            startActivity(shareThisApp)
        }

        binding.settingsSupport.setOnClickListener {
            val sendMailToSupport = Intent(Intent.ACTION_SENDTO)
            sendMailToSupport.data = Uri.parse("mailto:")
            sendMailToSupport.putExtra(
                Intent.EXTRA_EMAIL,
                arrayOf(getString(R.string.support_mail))
            )
            sendMailToSupport.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_mail_theme))
            sendMailToSupport.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_mail_content))
            startActivity(sendMailToSupport)
        }

        binding.settingsLicense.setOnClickListener {
            val showLicenseInBrowser = Intent(Intent.ACTION_VIEW)
            showLicenseInBrowser.data = Uri.parse(getString(R.string.ya_license))
            startActivity(showLicenseInBrowser)
        }
    }
}