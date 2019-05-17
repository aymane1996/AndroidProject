package com.aymane.android.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aymane.android.Models.MapLocation;
import com.aymane.android.Models.Report;
import com.aymane.android.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MapLocationAdapter extends RecyclerView.Adapter<MapLocationAdapter.MapLocationViewHolder>implements OnMapReadyCallback {

    public ArrayList<MapView> mMapViews = new ArrayList<>();
    public ArrayList<Report> mReports;
    protected GoogleMap mGoogleMap;
    public MapLocation mMapLocation;
    public Context mContext;
    //private List<Report> articles;
    Context context;

    public MapLocationAdapter(){

    }

    public MapLocationAdapter(Context c, ArrayList<Report> articles) {

        this.mReports = articles;
        this.context = c;
    }

    /*public void setMapLocations(ArrayList<MapLocation> mapLocations) {

        this.mapLocations = mapLocations;
    }*/

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;

        MapsInitializer.initialize(mContext);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        // If we have map data, update the map content.
        if (mMapLocation != null) {

        }
    }

    @Override
    public MapLocationViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {

        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.report_card, viewGroup, false);
        MapLocationViewHolder viewHolder = new MapLocationViewHolder(viewGroup.getContext(), view);

        mMapViews.add(viewHolder.mapView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MapLocationViewHolder holder, int position) {

        Report report = mReports.get(position);

        holder.displayName.setText(report.getDisplayNameUserPost());
        holder.publishedAt.setText(report.getPublishedAt());
        holder.source.setText(report.getPid());

        MapLocation mapLocation = new MapLocation(report.getLatitude(), report.getLongitude());
        holder.setMapLocation(mapLocation);
        holder.mapView.getMapAsync(this);
    }

    @Override
    public int getItemCount() {

        return mReports == null ? 0 : mReports.size();
    }

        public ArrayList<MapView> getMapViews() {

        return mMapViews;
    }


    // ViewHolder class for this adapter
    public class MapLocationViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback
    {
        public TextView displayName, publishedAt, source;

        protected GoogleMap mGoogleMap;
        protected MapLocation mMapLocation;

        public MapView mapView;
        private Context mContext;

        public MapLocationViewHolder(Context context, View view) {

            super(view);

            mContext = context;

            displayName = (TextView) itemView.findViewById(R.id.displayNameUser);
            publishedAt = (TextView) itemView.findViewById(R.id.publishedAt);
            source = (TextView) itemView.findViewById(R.id.source);

            mapView = (MapView) itemView.findViewById(R.id.mapImage);
            mapView.onCreate(null);
            mapView.getMapAsync(this);
        }

        public void setMapLocation(MapLocation mapLocation) {

            mMapLocation = mapLocation;

            // If the map is ready, update its content.
            if (mGoogleMap != null) {
                updateMapContents();
            }
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {

            mGoogleMap = googleMap;

            MapsInitializer.initialize(mContext);
            googleMap.getUiSettings().setMapToolbarEnabled(false);

            // If we have map data, update the map content.
            if (mMapLocation != null) {
                updateMapContents();
            }
        }

        protected void updateMapContents() {

            // Since the mapView is re-used, need to remove pre-existing mapView features.
            mGoogleMap.clear();

            // Update the mapView feature data and camera position.
            mGoogleMap.addMarker(new MarkerOptions().position(mMapLocation.center));

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mMapLocation.center, 10f);
            mGoogleMap.moveCamera(cameraUpdate);
        }
    }


}
