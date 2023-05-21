package io.github.jing.work.assistant.gitlab.widget

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import io.github.jing.arch.base.OnClickItemListener
import io.github.jing.work.assistant.databinding.GitlabItemProjectBinding
import io.github.jing.work.assistant.gitlab.data.Project

class ProjectViewHolder(private val binding: GitlabItemProjectBinding, private val onClickItemListener: OnClickItemListener<Project>): ViewHolder(binding.root) {

    private lateinit var data: Project

    init {
        binding.root.setOnClickListener {
            onClickItemListener.onClick(it, data)
        }
    }

    fun bind(data: Project) {
        this.data = data
        if (data.avatarUrl.isNullOrEmpty()) {
            binding.tvAvatar.text = data.name
            binding.tvAvatar.visibility = View.VISIBLE
            binding.ivAvatar.visibility = View.GONE
        } else {
            Glide.with(binding.ivAvatar).load(data.avatarUrl).circleCrop().into(binding.ivAvatar)
            binding.tvAvatar.visibility = View.GONE
            binding.ivAvatar.visibility = View.VISIBLE
        }
        binding.tvName.text = data.name
        binding.tvPath.text = data.pathWithNamespace
    }

}