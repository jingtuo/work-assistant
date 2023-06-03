package io.github.jing.work.assistant.gitlab.data

import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil

@Keep
class MrChange {
    var oldPath: String? = null
    var newPath: String? = null
    var aMode: String? = null
    var bMode: String? = null
    var diff: String? = null
    var newFile: Boolean = false
    var renamedFile: Boolean = false
    var deletedFile: Boolean = false

    class DiffCallback: DiffUtil.ItemCallback<MrChange>() {
        override fun areItemsTheSame(oldItem: MrChange, newItem: MrChange): Boolean {
            return oldItem.oldPath == newItem.oldPath && oldItem.newPath == newItem.newPath
        }

        override fun areContentsTheSame(oldItem: MrChange, newItem: MrChange): Boolean {
            return areItemsTheSame(oldItem, newItem)
        }

    }
}
