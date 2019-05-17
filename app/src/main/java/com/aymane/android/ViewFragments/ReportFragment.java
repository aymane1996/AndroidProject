package com.aymane.android.ViewFragments;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.aymane.android.MainActivity;
import com.aymane.android.Models.Report;
import com.aymane.android.Models.ReportData;
import com.aymane.android.R;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReportFragment extends AAH_FabulousFragment {

    RelativeLayout relativeContent;
    LinearLayout linearButtons;
    ViewPager viewPager;
    TabLayout reportTabs;
    ImageButton applyFilters, refreshFilters;
    ViewPagerAdapter mAdapter;
    String bestProvider = LocationManager.GPS_PROVIDER;
    Context context;

    ArrayMap<String, List<String>> applied_filters = new ArrayMap<>();
    List<TextView> textviews = new ArrayList<>();

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();


    public static ReportFragment newInstance() {
        return new ReportFragment();

    }

    @Override
    public void setupDialog(Dialog dialog, int style) {

        // Setting the Current date and time
        final Date currentDate = Calendar.getInstance().getTime();
        final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");


        View contentView = View.inflate(getContext(), R.layout.report_fragment, null);
        relativeContent = (RelativeLayout) contentView.findViewById(R.id.rl_content);
        linearButtons = (LinearLayout) contentView.findViewById(R.id.ll_buttons);
        applyFilters = (ImageButton) contentView.findViewById(R.id.imgbtn_apply);
        refreshFilters = (ImageButton) contentView.findViewById(R.id.imgbtn_refresh);
        viewPager = (ViewPager) contentView.findViewById(R.id.vp_types);
        reportTabs = (TabLayout) contentView.findViewById(R.id.tabs_types);
        // Clicking the buttons : => Applying animation and saving data



            applyFilters.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // When Applying the Filters the Report is created:
                    Report report = new Report();
                    report.setAgeOfHomeLess(applied_filters.get("age"));
                    report.setNeeds(applied_filters.get("besoins"));
                    report.setPsychoState(applied_filters.get("etat"));
                    report.setDisplayNameUserPost("Aymane");
                    String pidKey = ref.child("posts").push().getKey();
                    report.setPid(pidKey);
                    //report.setLatitude(location.getLatitude());
                    //report.setLongitude(location.getLongitude());

                    report.setPublishedAt(df.format(currentDate));
                    //report.setProfilePic("");
                    Log.d("Values of Filters:", applied_filters.toString());

                    ref.child("posts").child(pidKey).setValue(report); //insert user in that node
                    closeFilter(applied_filters);
                }
            });


        refreshFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (TextView tv : textviews) {
                    tv.setTag("unselected");
                    tv.setBackgroundResource(R.drawable.chip_unselected);
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));
                }
                applied_filters.clear();
            }
        });


        // Setting up the ViewPager to Contain the FilterElements:
        mAdapter = new ViewPagerAdapter();
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        reportTabs.setupWithViewPager(viewPager);


        // Animation settings
        setAnimationDuration(500); //optional; default 500ms
        setPeekHeight(400); // optional; default 400dp
        setCallbacks((Callbacks) getActivity()); //optional; to get back result
        setAnimationListener((AnimationListener) getActivity()); //optional; to get animation callbacks
        setViewgroupStatic(linearButtons); // optional; layout to stick at bottom on slide
        setViewPager(viewPager); //optional; if you use viewpager that has scrollview
        setViewMain(relativeContent); //necessary; main bottomsheet view
        setMainContentView(contentView); // necessary; call at end before super
        super.setupDialog(dialog, style); //call super at last


    }




    public class ViewPagerAdapter extends PagerAdapter {


        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.filters_view, collection, false);
            FlexboxLayout flexbox = (FlexboxLayout) layout.findViewById(R.id.flexboxLayout);
//            LinearLayout ll_scroll = (LinearLayout) layout.findViewById(R.id.ll_scroll);
//            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (metrics.heightPixels-(104*metrics.density)));
//            ll_scroll.setLayoutParams(lp);
            switch (position) {
                case 0:
                    inflateLayoutWithFilters("besoins", flexbox);
                    break;
                case 1:
                    inflateLayoutWithFilters("etat", flexbox);
                    break;
                case 2:
                    inflateLayoutWithFilters("age", flexbox);
                    break;
                case 3:
                    inflateLayoutWithFilters("quality", flexbox);
                    break;
            }
            collection.addView(layout);
            return layout;

        }

        private void inflateLayoutWithFilters(final String filter_category, FlexboxLayout fbl) {
            List<String> keys = new ArrayList<>();
            switch (filter_category) {
                case "besoins":
                    keys = (ReportData.getBesoinsValues());
                    break;
                case "sexe":
                    keys = (ReportData.getSexeValues());
                    break;
                case "age":
                    keys = (ReportData.getAgeValues());
                    break;
                case "etat":
                    keys = (ReportData.getEtatValues());
                    break;
            }

            for (int i = 0; i < keys.size(); i++) {

                View subchild = getActivity().getLayoutInflater().inflate(R.layout.single_chip, null);
                final TextView tv = ((TextView) subchild.findViewById(R.id.txt_title));
                tv.setText(keys.get(i));

                final int finalIndex = i;
                final List<String> finalKeys = keys;
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (tv.getTag() != null && tv.getTag().equals("selected")) {
                            tv.setTag("unselected");
                            tv.setBackgroundResource(R.drawable.chip_unselected);
                            tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));
                            removeFromSelectedMap(filter_category, finalKeys.get(finalIndex));
                        }

                        else {
                            tv.setTag("selected");
                            tv.setBackgroundResource(R.drawable.chip_selected);
                            tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_header));
                            addToSelectedMap(filter_category, finalKeys.get(finalIndex));
                        }
                    }
                });


                if (applied_filters != null && applied_filters.get(filter_category) != null && applied_filters.get(filter_category).contains(keys.get(finalIndex))) {
                    tv.setTag("selected");
                    tv.setBackgroundResource(R.drawable.chip_selected);
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_header));
                } else {
                    tv.setBackgroundResource(R.drawable.chip_unselected);
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.filters_chips));
                }

                textviews.add(tv);

                fbl.addView(subchild);
            }



        }

        private void removeFromSelectedMap(String key, String value) {

            applied_filters.get(key).remove(value);

        }

        private void addToSelectedMap(String key, String value) {

            if (applied_filters.get(key) != null && !applied_filters.get(key).contains(value)) {
                applied_filters.get(key).add(value);
            }

            else {
                List<String> temp = new ArrayList<>();
                temp.add(value);
                applied_filters.put(key, temp);
            }
        }


        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "BESOINS";
                case 1:
                    return "ETAT";
                case 2:
                    return "AGE";
                case 3:
                    return "SEXE";

            }
            return "";
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
}
