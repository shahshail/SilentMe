package com.corral.firebase.shailshah.silentme;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.places.PlaceBuffer;

/**
 * Created by shailshah on 10/26/17.
 */

public class SilentAdapter extends RecyclerView.Adapter<SilentAdapter.SilentViewHolder> {
    private Context mContext;
    private PlaceBuffer mPlace;
    public SilentAdapter(Context context, PlaceBuffer mPlace)
    {
        this.mPlace = mPlace;
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

        String placeName = mPlace.get(position).getName().toString();
        String placeAddress = mPlace.get(position).getAddress().toString();
        float placeRatings = mPlace.get(position).getRating();
        String placeAttributes = (String) mPlace.get(position).getAttributions();

        Log.v(SilentAdapter.class.getSimpleName(), "Place informations Name : " + placeName + " Address : " + placeAddress + " Ratings : " + placeRatings + " Attriburtes : " + placeAttributes);

        holder.nameTextView.setText(placeName);
        holder.addressTextView.setText(placeAddress);



    }

    public void swapPlaces(PlaceBuffer newPlace)
    {
        mPlace = newPlace;
        if (mPlace != null)
        {
            this.notifyDataSetChanged();
        }

    }

    @Override
    public int getItemCount() {
        if (mPlace == null) return 0;
        return mPlace.getCount();
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
