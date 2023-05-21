package io.github.jing.work.assistant.gitlab.project.widget

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import io.github.jing.arch.base.OnClickItemListener
import io.github.jing.work.assistant.databinding.GitlabItemMergeRequestBinding
import io.github.jing.work.assistant.gitlab.data.MergeRequest

class MergeRequestViewHolder(private val binding: GitlabItemMergeRequestBinding,
                             private val onClickItemListener: OnClickItemListener<MergeRequest>
): ViewHolder(binding.root) {

    private lateinit var data: MergeRequest

    init {
        binding.root.setOnClickListener {
            onClickItemListener.onClick(it, data)
        }
    }

    fun bind(data: MergeRequest) {
        this.data = data
        binding.tvName.text = data.title
        binding.tvPath.text = data.description
    }

}