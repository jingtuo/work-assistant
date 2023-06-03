package io.github.jing.work.assistant.gitlab.mr.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import io.github.jing.work.assistant.databinding.GitlabItemMrChangeBinding
import io.github.jing.work.assistant.gitlab.data.MrChange

class MrChangeAdapter(context: Context) : ListAdapter<MrChange, MrChangeViewHolder>(MrChange.DiffCallback()) {

    private val inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MrChangeViewHolder {
        return MrChangeViewHolder(GitlabItemMrChangeBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: MrChangeViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }


}