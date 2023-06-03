package io.github.jing.work.assistant.gitlab.mr.widget

import android.view.View
import io.github.jing.work.assistant.gitlab.data.MergeRequest

interface OnChangeMRListener {
    /**
     * merge MR
     */
    fun onMergeMR(view: View, mr: MergeRequest)

    /**
     * close MR
     */
    fun onCloseMR(view: View, mr: MergeRequest)
}