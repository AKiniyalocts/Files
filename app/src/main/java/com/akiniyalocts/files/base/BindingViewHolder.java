package com.akiniyalocts.files.base;

import android.support.v7.widget.RecyclerView;

/**
 * Created by anthonykiniyalocts on 12/6/16.
 */

public abstract class BindingViewHolder<M> extends RecyclerView.ViewHolder {
    public BindingViewHolder(android.view.View itemView) {
        super(itemView);
    }

    public abstract void bind(M viewModel);
}
