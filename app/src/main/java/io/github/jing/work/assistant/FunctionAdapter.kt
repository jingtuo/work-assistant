package io.github.jing.work.assistant

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class FunctionAdapter(context: Context, private val data: List<Function>): RecyclerView.Adapter<FunctionViewHolder>() {

    private val inflater: LayoutInflater

    private var onClickFunctionListener: FunctionViewHolder.OnClickFunctionListener? = null

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FunctionViewHolder {
        val view = inflater.inflate(R.layout.function_item, parent, false)
        val viewHolder = FunctionViewHolder(view)
        viewHolder.setOnClickFunctionListener(onClickFunctionListener)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: FunctionViewHolder, position: Int) {
        holder.bindData(data[position])
    }

    fun setOnClickFunctionListener (onClickFunctionListener: FunctionViewHolder.OnClickFunctionListener) {
        this.onClickFunctionListener = onClickFunctionListener
    }


}