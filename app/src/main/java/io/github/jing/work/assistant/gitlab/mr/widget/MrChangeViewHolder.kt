package io.github.jing.work.assistant.gitlab.mr.widget

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import io.github.jing.work.assistant.R
import io.github.jing.work.assistant.databinding.GitlabItemMrChangeBinding
import io.github.jing.work.assistant.gitlab.data.MrChange

class MrChangeViewHolder(
    private val binding: GitlabItemMrChangeBinding,
) : ViewHolder(binding.root) {

    private lateinit var data: MrChange

    init {
        binding.tvPath.setOnClickListener {
            if (binding.tvDiff.visibility == View.GONE) {
                binding.tvPath.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.round_keyboard_arrow_up_24,
                    0
                )
                binding.tvDiff.visibility = View.VISIBLE
            } else {
                binding.tvPath.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.round_keyboard_arrow_down_24,
                    0
                )
                binding.tvDiff.visibility = View.GONE
            }
        }
    }

    fun bind(data: MrChange) {
        this.data = data
        //默认是oldPath
        binding.tvPath.text = data.oldPath
        if (data.newFile) {
            binding.tvPath.text = data.newPath
        }
        if (data.renamedFile) {
            binding.tvPath.text = buildString {
                append(data.oldPath)
                append(" -> ")
                append(data.newPath)
            }
        }
        binding.tvDiff.text = data.diff
    }

}