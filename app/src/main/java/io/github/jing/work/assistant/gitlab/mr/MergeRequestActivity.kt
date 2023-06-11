package io.github.jing.work.assistant.gitlab.mr

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import io.github.jing.arch.base.BaseActivity
import io.github.jing.arch.base.OnClickItemListener
import io.github.jing.work.assistant.R
import io.github.jing.work.assistant.databinding.ActivityMergeRequestBinding
import io.github.jing.work.assistant.gitlab.Keys
import io.github.jing.work.assistant.gitlab.data.MergeRequest
import io.github.jing.work.assistant.gitlab.mr.widget.MergeRequestAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MergeRequestActivity : BaseActivity<ActivityMergeRequestBinding>() {

    private lateinit var viewModel: MergeRequestViewModel

    private var projectId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        projectId = intent.getIntExtra(Keys.PROJECT_ID, 0)
        if (projectId == 0) {
            finish()
            return
        }
        setSupportActionBar(binding.root.findViewById(R.id.toolbar))
        setTitle(R.string.merge_request)
        enableBack()
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[MergeRequestViewModel::class.java]

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.horizontal_divider)!!)
        binding.recyclerView.addItemDecoration(divider)
        binding.recyclerView.adapter = MergeRequestAdapter(this, object :
            OnClickItemListener<MergeRequest> {
            override fun onClick(view: View, data: MergeRequest) {
                val intent = Intent(this@MergeRequestActivity, MrChangeActivity::class.java).apply {
                    putExtra(Keys.PROJECT_ID, data.projectId)
                    putExtra(Keys.MR_IID, data.iid)
                    putExtra(Keys.TITLE, data.title)
                }
                startActivity(intent)
            }
        })
        binding.btnCreateMergeRequest.setOnClickListener {
            val intent = Intent(this@MergeRequestActivity, CreateMrActivity::class.java).apply {
                putExtra(Keys.PROJECT_ID, projectId!!)
            }
            startActivity(intent)
        }

        loadMergeRequests()
    }

    override fun createBinding(): ActivityMergeRequestBinding {
        return ActivityMergeRequestBinding.inflate(layoutInflater)
    }

    private fun loadMergeRequests() {
        lifecycleScope.launch {
            viewModel.mergeRequests(projectId!!, "").collectLatest { pagingData ->
                val adapter = binding.recyclerView.adapter as MergeRequestAdapter
                adapter.submitData(pagingData)
            }
        }
    }
}