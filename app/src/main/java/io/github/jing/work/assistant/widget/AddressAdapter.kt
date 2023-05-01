package io.github.jing.work.assistant.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckedTextView
import io.github.jing.work.assistant.data.Address

class AddressAdapter(context: Context, private val addresses: List<Address>) : BaseAdapter() {

    private val inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return addresses.size
    }

    override fun getItem(position: Int): Address {
        return addresses.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var textView: CheckedTextView = if (convertView == null) {
            inflater.inflate(
                android.R.layout.select_dialog_singlechoice,
                parent,
                false
            ) as CheckedTextView
        } else {
            convertView as CheckedTextView
        }
        textView.text = addresses[position].detail
        return textView
    }

}