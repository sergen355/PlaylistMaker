package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
        }

        val share = findViewById<ImageView>(R.id.share)
        share.setOnClickListener {
            val message = getString(R.string.practicum_android_link)
            val shareIntent  = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(shareIntent)
        }

        val support = findViewById<ImageView>(R.id.support)
        support.setOnClickListener {
            val email = getString(R.string.student_email_address)
            val title = getString(R.string.student_email_title)
            val message = getString(R.string.student_email)
            val supportIntent  = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, title)
            supportIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(supportIntent)
        }

        val eula = findViewById<ImageView>(R.id.eula)
        eula.setOnClickListener {
            val eulaIntent  = Intent(Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.practicum_offer)))
            startActivity(eulaIntent)
        }

    }
}