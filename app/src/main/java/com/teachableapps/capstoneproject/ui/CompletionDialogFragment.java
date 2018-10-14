package com.teachableapps.capstoneproject.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.teachableapps.capstoneproject.DefaultApplication;
import com.teachableapps.capstoneproject.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CompletionDialogFragment extends DialogFragment {
    private static final String LOG_TAG = CompletionDialogFragment.class.getSimpleName();

    // Google Analytics
    Tracker mTracker;
    String screenName = "CompletionDialogFragment";

    // Butterknife bindings
    @BindView(R.id.iv_completion_screen) ImageView iv_completion;
    @BindView(R.id.tv_completion_screen) TextView tv_completion;
    @BindView(R.id.tv_completion_fun) TextView tv_fun_action;
    @BindView(R.id.iv_close_reward_screen) ImageView iv_close_screen;

    // empty constructor
    public CompletionDialogFragment(){}

    public static CompletionDialogFragment newInstance() {
        CompletionDialogFragment frag = new CompletionDialogFragment();
        return frag;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Send Google Analytics Tracking
        mTracker.setScreenName("Image~" + screenName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        // Calculate ActionBar height
        int actionBarHeight = 56;
        TypedValue tv = new TypedValue();
        if (getActivity().getTheme().resolveAttribute(R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }

        // detect screen size to set dialog dimensions (fill screen)
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        getDialog().getWindow().setLayout(width, height - actionBarHeight);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.completion_screen, container);

        // Butterknife
        ButterKnife.bind(this, view);
        iv_close_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeRewardScreen();
            }
        });

        // Get Google Analytics Tracker
        DefaultApplication application = (DefaultApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView imageView = (ImageView) view.findViewById(R.id.iv_completion_screen);
        int id = getResources().getIdentifier("@drawable/ic_completion_bg", "drawable", getActivity().getPackageName());

        // Use Glide to load random image from the internet
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true);
        Glide.with(CompletionDialogFragment.this)
                .load("https://source.unsplash.com/featured/?cute,safe-kids")
                .apply(requestOptions)
                .into(iv_completion);

        iv_completion.setScaleType(ImageView.ScaleType.CENTER_CROP);
        tv_completion.setText(R.string.completion_congrats);
        tv_fun_action.setText(R.string.completion_fun_action);
    }

    private void closeRewardScreen() {
        dismiss();
    }
}
