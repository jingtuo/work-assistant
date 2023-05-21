package io.github.jing.work.assistant.gitlab.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import io.github.jing.arch.base.OnClickItemListener
import io.github.jing.work.assistant.databinding.GitlabItemProjectBinding
import io.github.jing.work.assistant.gitlab.data.DiffCallback
import io.github.jing.work.assistant.gitlab.data.Project

class ProjectAdapter(context: Context, private val onClickItemListener: OnClickItemListener<Project>)
    : PagingDataAdapter<Project, ProjectViewHolder>(DiffCallback()) {

    private val inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(context)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        return ProjectViewHolder(GitlabItemProjectBinding.inflate(inflater, parent, false), onClickItemListener)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }


}