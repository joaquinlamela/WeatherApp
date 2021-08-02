package com.weatherapp.ui.home.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView


abstract class BaseViewHolder<T>(containerView: View) : RecyclerView.ViewHolder(containerView) {
    abstract fun render(item: T)
}