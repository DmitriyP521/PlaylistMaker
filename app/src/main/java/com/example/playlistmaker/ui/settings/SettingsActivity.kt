package com.example.playlistmaker.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.playlistmaker.App
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    private lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }

        settingsInteractor = Creator.provideSettingsInteractor(this)
        val backButton = findViewById<Button>(R.id.back)
        val shareAppButton = findViewById<LinearLayout>(R.id.shareApp)
        val writeSupp = findViewById<LinearLayout>(R.id.writeSupp)
        val userAgrButton = findViewById<LinearLayout>(R.id.userAgr)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        var settingsInteractor: SettingsInteractor = Creator.provideSettingsInteractor(this)

        themeSwitcher.isChecked = settingsInteractor.isDarkThemeEnabled()

        backButton.setOnClickListener {
            finish()
        }

        shareAppButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.course_link))
            startActivity(Intent.createChooser(intent, getString(R.string.share_via)))
        }

        writeSupp.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.send_to))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.message_subject))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.message_text))
            startActivity(intent)
        }

        userAgrButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.agr_link)))
            startActivity(intent)
        }

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            settingsInteractor.setDarkThemeEnabled(checked)
            (applicationContext as App).switchTheme(checked)
        }
    }
}