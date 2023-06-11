package io.github.jing.work.assistant.gitlab

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.appbar.MaterialToolbar
import io.github.jing.arch.base.BaseActivity
import io.github.jing.arch.base.OnClickItemListener
import io.github.jing.work.assistant.R
import io.github.jing.work.assistant.databinding.ActivityGitlabBinding
import io.github.jing.work.assistant.gitlab.data.GitlabManager
import io.github.jing.work.assistant.gitlab.data.Project
import io.github.jing.work.assistant.gitlab.project.ProjectActivity
import io.github.jing.work.assistant.gitlab.setting.GitlabSettingsActivity
import io.github.jing.work.assistant.gitlab.widget.ProjectAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GitlabActivity : BaseActivity<ActivityGitlabBinding>() {

    private lateinit var launcher: ActivityResultLauncher<Intent>

    private lateinit var viewModel: GitlabViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application))[GitlabViewModel::class.java]
        val toolBar = binding.root.findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolBar)
        enableBack()

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.horizontal_divider)!!)
        binding.recyclerView.addItemDecoration(divider)
        binding.recyclerView.adapter = ProjectAdapter(this,  object: OnClickItemListener<Project> {
            override fun onClick(view: View, data: Project) {
                val intent = Intent(this@GitlabActivity, ProjectActivity::class.java).apply {
                    putExtra(Keys.PROJECT_ID, data.id)
                    putExtra(Keys.PROJECT_NAME, data.name)
                }
                startActivity(intent)
            }
        })
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())  {
            if (it.resultCode == Activity.RESULT_OK) {
                loadProjects()
            } else {
                finish()
            }
        }
        //初始化Gitlab
        viewModel.initGitlab()
        if (GitlabManager.instance.gitlab == null) {
            launcher.launch(Intent(this@GitlabActivity, GitlabSettingsActivity::class.java))
            return
        }

        loadProjects()
    }

    private fun loadProjects() {
        lifecycleScope.launch {
            viewModel.search("")?.collectLatest { pagingData ->
                val adapter = binding.recyclerView.adapter as ProjectAdapter
                adapter.submitData(pagingData)
            }
        }
    }


    override fun createBinding(): ActivityGitlabBinding {
        return ActivityGitlabBinding.inflate(layoutInflater)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            startActivity(Intent(this@GitlabActivity, GitlabSettingsActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}