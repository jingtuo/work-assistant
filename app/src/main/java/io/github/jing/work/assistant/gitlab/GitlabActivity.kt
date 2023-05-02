package io.github.jing.work.assistant.gitlab

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import io.github.jing.work.assistant.R
import io.github.jing.work.assistant.databinding.ActivityGitlabBinding
import io.github.jing.work.assistant.gitlab.settings.GitlabSettingsActivity

class GitlabActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGitlabBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGitlabBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            //设置
            val intent = Intent(this, GitlabSettingsActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onContextItemSelected(item)
    }
}