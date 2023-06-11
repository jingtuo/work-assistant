package io.github.jing.work.assistant.gitlab.mr.widget

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import io.github.jing.arch.base.OnClickItemListener
import io.github.jing.work.assistant.R
import io.github.jing.work.assistant.databinding.GitlabItemMergeRequestBinding
import io.github.jing.work.assistant.gitlab.data.MergeRequest
import io.github.jing.work.assistant.gitlab.data.MrState

class MergeRequestViewHolder(
    private val binding: GitlabItemMergeRequestBinding,
    private val onClickItemListener: OnClickItemListener<MergeRequest>,
) : ViewHolder(binding.root) {

    private lateinit var data: MergeRequest

    init {
        binding.root.setOnClickListener {
            onClickItemListener.onClick(it, data)
        }
    }

    fun bind(data: MergeRequest) {
        this.data = data
        binding.tvTitle.text = data.title
        val context = binding.root.context
        binding.tvSourceToTarget.text = context.getString(R.string.merge_into, data.sourceBranch, data.targetBranch)
        if (MrState.OPENED == data.state) {
            binding.btnMerge.visibility = View.VISIBLE
            binding.btnClose.visibility = View.VISIBLE
        } else {
            binding.btnMerge.visibility = View.GONE
            binding.btnClose.visibility = View.GONE
        }
    }

}