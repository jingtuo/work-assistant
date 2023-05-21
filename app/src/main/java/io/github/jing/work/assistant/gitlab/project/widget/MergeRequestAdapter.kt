package io.github.jing.work.assistant.gitlab.project.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import io.github.jing.arch.base.OnClickItemListener
import io.github.jing.work.assistant.databinding.GitlabItemMergeRequestBinding
import io.github.jing.work.assistant.databinding.GitlabItemProjectBinding
import io.github.jing.work.assistant.gitlab.data.DiffCallback
import io.github.jing.work.assistant.gitlab.data.MergeRequest
import io.github.jing.work.assistant.gitlab.data.Project

class MergeRequestAdapter(context: Context, private val onClickItemListener: OnClickItemListener<MergeRequest>)
    : PagingDataAdapter<MergeRequest, MergeRequestViewHolder>(DiffCallback()) {

    private val inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(context)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MergeRequestViewHolder {
        return MergeRequestViewHolder(GitlabItemMergeRequestBinding.inflate(inflater, parent, false),
            onClickItemListener)
    }

    override fun onBindViewHolder(holder: MergeRequestViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }


}