package com.practicum.playlistmaker.settings.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.settings.ui.presentation.SettingsCommand
import com.practicum.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupListeners()
        setupObservers()
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupListeners() = with(binding) {
        switchOption.setOnCheckedChangeListener { _, checked ->
            settingsViewModel.switchTheme(checked)
        }

        settingsItem2.setOnClickListener {
            settingsViewModel.onShareButtonClicked()
        }

        settingsItem3.setOnClickListener {
            settingsViewModel.onSupportButtonClicked()
        }

        settingsItem4.setOnClickListener {
            settingsViewModel.onAgreementButtonClicked()
        }
    }

    private fun setupObservers() {
        settingsViewModel.isDarkThemeEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.switchOption.isChecked = isEnabled
        }

        settingsViewModel.command.observe(viewLifecycleOwner) { command ->
            when (command) {
                is SettingsCommand.Share -> shareText(command.link)
                is SettingsCommand.SendEmail -> {
                    val data = command.emailData
                    sendEmail(data.email, data.subject, data.body)
                }
                is SettingsCommand.OpenUrl -> openUrl(command.url)
            }
        }
    }

    private fun shareText(text: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        startActivity(shareIntent)
    }

    private fun sendEmail(email: String, subject: String, body: String) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        startActivity(emailIntent)
    }

    private fun openUrl(url: String) {
        val userAgreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(userAgreementIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
