package com.example.playlistmaker.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.ActionHandlerRepository

class ActionHandlerRepositoryImpl(private val context: Context) : ActionHandlerRepository {
    override fun shareApp() {
        val shareThisApp = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.ya_android_course))
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(shareThisApp)
    }

    override fun contactSupport() {
        val sendMailToSupport = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.support_mail)))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.support_mail_theme))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.support_mail_content))
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(sendMailToSupport)
    }

    override fun showLicense() {
        val showLicenseInBrowser = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(context.getString(R.string.ya_license))
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(showLicenseInBrowser)
    }
}