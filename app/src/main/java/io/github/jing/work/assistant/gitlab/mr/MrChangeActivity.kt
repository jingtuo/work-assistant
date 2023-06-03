package io.github.jing.work.assistant.gitlab.mr

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import io.github.jing.arch.base.BaseActivity
import io.github.jing.work.assistant.R
import io.github.jing.work.assistant.databinding.ActivityMrChangeBinding
import io.github.jing.work.assistant.gitlab.Keys
import io.github.jing.work.assistant.gitlab.mr.widget.MrChangeAdapter

class MrChangeActivity : BaseActivity<ActivityMrChangeBinding>() {

    private var projectId: Int? = null
    private var mrIid: Int? = null

    private lateinit var viewModel: MrChangeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        projectId = intent.getIntExtra(Keys.PROJECT_ID, 0)
        mrIid = intent.getIntExtra(Keys.MR_IID, 0)
        val title = intent.getStringExtra(Keys.TITLE) ?: ""
        if (projectId == 0 || mrIid == 0) {
            finish()
            return
        }
        setSupportActionBar(binding.root.findViewById(R.id.toolbar))
        setTitle(title)
        enableBack()
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[MrChangeViewModel::class.java]
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.horizontal_divider)!!)
        binding.recyclerView.addItemDecoration(divider)
        binding.recyclerView.adapter = MrChangeAdapter(this)

        viewModel.mrChanges.observe(this) { it ->
            val adapter = binding.recyclerView.adapter as MrChangeAdapter
            adapter.submitList(it.toList())
        }

        viewModel.loadMrChanges(projectId!!, mrIid!!)
    }

    override fun createBinding(): ActivityMrChangeBinding {
        return ActivityMrChangeBinding.inflate(layoutInflater)
    }


}