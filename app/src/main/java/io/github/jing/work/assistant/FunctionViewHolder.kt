package io.github.jing.work.assistant

import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.textview.MaterialTextView

class FunctionViewHolder(itemView: View) : ViewHolder(itemView), OnClickListener {

    private val ivIcon: AppCompatImageView
    private val tvName: MaterialTextView
    private var data: Function? = null
    private var onClickFunctionListener: OnClickFunctionListener? = null

    init {
        ivIcon = itemView.findViewById(R.id.iv_icon)
        tvName = itemView.findViewById(R.id.tv_name)
        itemView.setOnClickListener(this)
    }

    fun bindData(data: Function) {
        this.data = data
        ivIcon.setImageResource(data.iconResId)
        tvName.text = data.name
    }

    override fun onClick(v: View?) {
        onClickFunctionListener?.onClickFunction(data!!)
    }

    fun setOnClickFunctionListener (onClickFunctionListener: OnClickFunctionListener?) {
        this.onClickFunctionListener = onClickFunctionListener
    }


    interface OnClickFunctionListener {
        fun onClickFunction(data: Function)
    }

}