package io.github.jing.work.assistant.gitlab.setting

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import io.github.jing.work.assistant.R
import io.github.jing.work.assistant.databinding.ActivityGitlabSettingsBinding

class GitlabSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGitlabSettingsBinding

    private lateinit var viewModel: GitlabSettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application))[GitlabSettingsViewModel::class.java]
        binding = ActivityGitlabSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.btnSave.setOnClickListener {
            viewModel.save(binding.etDomainName.text.toString(), binding.etIpAddress.text.toString(),
                binding.rbYes.isChecked, binding.etApiVersion.text.toString(),
                binding.etPersonalAccessToken.text.toString())
            setResult(RESULT_OK)
            finish()
        }
        //显示返回按钮
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.domainName().observe(this) {
            binding.etDomainName.setText(it, TextView.BufferType.NORMAL)
        }

        viewModel.ipAddress().observe(this) {
            binding.etIpAddress.setText(it, TextView.BufferType.NORMAL)
        }

        viewModel.useHttps().observe(this) {
            if (it) {
                binding.rgHttps.check(R.id.rb_yes)
            } else {
                binding.rgHttps.check(R.id.rb_no)
            }
        }

        viewModel.apiVersion().observe(this) {
            binding.etApiVersion.setText(it, TextView.BufferType.NORMAL)
        }

        viewModel.personalAccessToken().observe(this) {
            binding.etPersonalAccessToken.setText(it, TextView.BufferType.NORMAL)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}