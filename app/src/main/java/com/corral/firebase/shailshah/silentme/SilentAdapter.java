package com.corral.firebase.shailshah.silentme;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by shailshah on 10/26/17.
 */

public class SilentAdapter extends RecyclerView.Adapter<SilentAdapter.SilentViewHolder> {
    private Context mContext;
    public SilentAdapter(Context context)
    {
        this.mContext = context;
    }

    @Override
    public SilentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_place_card,parent,false);

        return new SilentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SilentViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class SilentViewHolder extends RecyclerView.ViewHolder
    {
        TextView nameTextView;
        TextView addressTextView;
        public SilentViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.name_text_view);
            addressTextView = (TextView) itemView.findViewById(R.id.address_text_view);
        }
    }
}
