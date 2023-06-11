package io.github.jing.work.assistant.gitlab.mr

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import io.github.jing.arch.base.BaseActivity
import io.github.jing.work.assistant.R
import io.github.jing.work.assistant.databinding.ActivityCreateMrBinding
import io.github.jing.work.assistant.gitlab.Keys
import io.github.jing.work.assistant.gitlab.mr.widget.CreateMrViewModel

class CreateMrActivity : BaseActivity<ActivityCreateMrBinding>(), OnClickListener {

    private var projectId: Int? = null
    private lateinit var viewModel: CreateMrViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        projectId = intent.getIntExtra(Keys.PROJECT_ID, 0)
        if (projectId == 0) {
            finish()
            return
        }
        setSupportActionBar(binding.root.findViewById(R.id.toolbar))
        setTitle(R.string.create_merge_request)
        enableBack()
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[CreateMrViewModel::class.java]

        binding.ilAssignee.setEndIconOnClickListener {
            viewModel.setAssigneeUser(-1)
        }
        binding.etAssignee.onItemSelectedListener = object: OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.setAssigneeUser(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.setAssigneeUser(-1)
            }

        }
        binding.btnSubmit.setOnClickListener(this)

        viewModel.loadBranches(projectId!!)
        viewModel.loadUsers(projectId!!)

        viewModel.branches.observe(this) {
            binding.etSourceBranch.setAdapter(ArrayAdapter(this@CreateMrActivity,
                R.layout.dropdown_menu_item, it))
            binding.etTargetBranch.setAdapter(ArrayAdapter(this@CreateMrActivity,
                R.layout.dropdown_menu_item, it))
        }
        viewModel.users.observe(this) {
            binding.etAssignee.setAdapter(ArrayAdapter(this@CreateMrActivity,
                R.layout.dropdown_menu_item, it.map { item -> item.name }))
        }

    }

    override fun createBinding(): ActivityCreateMrBinding {
        return ActivityCreateMrBinding.inflate(layoutInflater)
    }

    override fun onClick(v: View) {
        val id = v.id
        if (R.id.btn_submit == id) {
            val sourceBranch = binding.etSourceBranch.text.toString()
            val targetBranch = binding.etTargetBranch.text.toString()
            val title = binding.etTitle.text.toString()
            val assignee = binding.etAssignee.text.toString()
            var errorCount = 0
            if (sourceBranch.isNullOrEmpty()) {
                binding.etSourceBranch.error = "Source Branch is empty"
                errorCount++
            } else {
                binding.etSourceBranch.error = null
            }
            if (targetBranch.isNullOrEmpty()) {
                binding.etSourceBranch.error = "Target Branch is empty"
                errorCount++
            } else {
                binding.etSourceBranch.error = null
            }
            if (title.isNullOrEmpty()) {
                binding.etSourceBranch.error = "Source Branch is empty"
                errorCount++
            } else {
                binding.etSourceBranch.error = null
            }
            if (assignee.isNullOrEmpty()) {
                binding.etSourceBranch.error = "Source Branch is empty"
                errorCount++
            } else {
                binding.etSourceBranch.error = null
            }
            if (errorCount >= 1) {
                return
            }
            viewModel.createMr(projectId!!, sourceBranch, targetBranch, title)
        }
    }
}