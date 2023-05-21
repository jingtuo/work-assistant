package io.github.jing.work.assistant.gitlab.project

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import io.github.jing.arch.base.BaseActivity
import io.github.jing.arch.base.OnClickItemListener
import io.github.jing.work.assistant.R
import io.github.jing.work.assistant.databinding.ActivityProjectBinding
import io.github.jing.work.assistant.gitlab.data.MergeRequest
import io.github.jing.work.assistant.gitlab.project.widget.MergeRequestAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProjectActivity : BaseActivity<ActivityProjectBinding>() {

    private lateinit var viewModel: ProjectViewModel

    private var projectId: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        projectId = intent.getIntExtra(PROJECT_ID, 0)
        if (projectId == 0) {
            finish()
            return
        }
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.root.findViewById(R.id.toolbar))
        enableBack()
        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application))[ProjectViewModel::class.java]

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.horizontal_divider)!!)
        binding.recyclerView.addItemDecoration(divider)
        binding.recyclerView.adapter = MergeRequestAdapter(this, object:OnClickItemListener<MergeRequest> {
            override fun onClick(view: View, data: MergeRequest) {

            }
        })
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

        loadMergeRequests()
        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.fab)
                .setAction("Action", null).show()
        }

    }

    private fun loadMergeRequests() {
        lifecycleScope.launch {
            viewModel.mergeRequests(projectId!!, "").collectLatest { pagingData ->
                val adapter = binding.recyclerView.adapter as MergeRequestAdapter
                adapter.submitData(pagingData)
            }
        }
    }

    override fun createBinding(): ActivityProjectBinding {
        return ActivityProjectBinding.inflate(layoutInflater)
    }

    companion object {
        const val PROJECT_ID = "PROJECT_ID"
    }
}