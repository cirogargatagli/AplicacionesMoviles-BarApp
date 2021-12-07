package com.example.barapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import java.util.*
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.appcompat.app.AppCompatDelegate


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val preferences = getSharedPreferences("com.example.barapp_preferences", MODE_PRIVATE)
        val listener = OnSharedPreferenceChangeListener { prefs, key ->
            if(key == "language"){
                val lang = prefs.getString("language", "es")
                val res = this.resources
                val config = res.configuration
                val dpMetrics = res.displayMetrics
                config.setLocale(Locale(lang))
                res.updateConfiguration(config, dpMetrics)
                startActivity(Intent(this, SettingsActivity::class.java))
                finish()
            }
            if(key == "theme"){
                val darkMode = prefs.getBoolean("theme", false)
                if(darkMode){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }
        preferences.registerOnSharedPreferenceChangeListener(listener)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}