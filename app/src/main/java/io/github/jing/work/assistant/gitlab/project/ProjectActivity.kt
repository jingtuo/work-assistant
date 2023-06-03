package io.github.jing.work.assistant.gitlab.project

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import com.google.android.material.snackbar.Snackbar
import io.github.jing.arch.base.BaseActivity
import io.github.jing.work.assistant.R
import io.github.jing.work.assistant.databinding.ActivityProjectBinding
import io.github.jing.work.assistant.gitlab.Keys
import io.github.jing.work.assistant.gitlab.mr.MergeRequestActivity

class ProjectActivity : BaseActivity<ActivityProjectBinding>(), OnClickListener {

    private var projectId: Int? = null
    private var projectName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        projectId = intent.getIntExtra(Keys.PROJECT_ID, 0)
        projectName = intent.getStringExtra(Keys.PROJECT_NAME)
        if (projectId == 0) {
            finish()
            return
        }
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.root.findViewById(R.id.toolbar))
        title = projectName
        enableBack()
        binding.tvMergeRequest.setOnClickListener(this)
        binding.btnCreateProject.setOnClickListener { view ->
            Snackbar.make(view, "TODO Create Project", Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.btn_create_project)
                .setAction("Action", null).show()
        }

    }

    override fun createBinding(): ActivityProjectBinding {
        return ActivityProjectBinding.inflate(layoutInflater)
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.tv_merge_request) {
            val intent = Intent(this, MergeRequestActivity::class.java).apply {
                putExtra(Keys.PROJECT_ID, projectId)
            }
            startActivity(intent)
        }
    }
}