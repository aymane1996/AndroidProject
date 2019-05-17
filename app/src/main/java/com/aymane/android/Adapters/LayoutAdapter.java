package com.aymane.android.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aymane.android.Models.Report;
import com.aymane.android.R;
import com.google.android.gms.maps.MapView;
import com.squareup.picasso.Picasso;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LayoutAdapter extends RecyclerView.Adapter<LayoutAdapter.MyViewHolder> {

    //added view types
    private static final int TYPE_HEADER = 2;
    private static final int TYPE_ITEM = 1;

    Context context;
    ArrayList<Report> reports;
    private List<Report> articles;

    public LayoutAdapter(Context c , ArrayList<Report> p)
    {
        context = c;
        reports = p;
        articles = p;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.report_card,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Report report = articles.get(position);

        holder.displayName.setText(report.getDisplayNameUserPost());
        holder.publishedAt.setText(report.getPublishedAt());
        holder.source.setText(report.getPid());

        //holder.imgMap.setImageBitmap(getGoogleMapThumbnail(reports.get(position).getProfilePic()));
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public static Bitmap getGoogleMapThumbnail(String url){

        Bitmap bmp = null;
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);

        InputStream in = null;
        try {
            in = httpclient.execute(request).getEntity().getContent();
            bmp = BitmapFactory.decodeStream(in);
            in.close();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return bmp;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView displayName, publishedAt, timePosted, source;
        RecyclerView recyclerNeeds;
        MapView imgMap;

        public MyViewHolder(View itemView) {
            super(itemView);
            displayName = (TextView) itemView.findViewById(R.id.displayNameUser);
            publishedAt = (TextView) itemView.findViewById(R.id.publishedAt);
            source = (TextView) itemView.findViewById(R.id.source);
            timePosted = (TextView) itemView.findViewById(R.id.time);
            imgMap = (MapView) itemView.findViewById(R.id.mapImage);
        }

        public void onClick(final int position)
        {

        }
    }
}
