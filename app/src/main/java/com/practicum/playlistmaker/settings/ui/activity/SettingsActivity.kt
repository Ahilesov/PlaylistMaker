package com.practicum.playlistmaker.settings.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, SettingsViewModelFactory(this, application))
            .get(SettingsViewModel::class.java)

        viewModel.themeSettingsState.observe(this) { themeSettings ->
            binding.themeSwitcher.isChecked = themeSettings.darkTheme
        }

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.switchTheme(checked)
        }

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.shareApp.setOnClickListener {
            viewModel.shareApp()
        }

        binding.writeToSupport.setOnClickListener {
            viewModel.supportEmail()
        }

        binding.userAgreement.setOnClickListener {
            viewModel.openAgreement()
        }
    }

}