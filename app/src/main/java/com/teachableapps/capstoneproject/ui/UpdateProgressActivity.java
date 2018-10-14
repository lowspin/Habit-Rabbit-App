package com.teachableapps.capstoneproject.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.MenuItem;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.teachableapps.capstoneproject.DefaultApplication;
import com.teachableapps.capstoneproject.R;
import com.teachableapps.capstoneproject.data.AppExecutors;
import com.teachableapps.capstoneproject.data.database.HelperConverter;
import com.teachableapps.capstoneproject.data.database.InjectorUtils;
import com.teachableapps.capstoneproject.data.database.ProjectEntry;
import com.teachableapps.capstoneproject.data.database.Sticker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.teachableapps.capstoneproject.ui.MainActivity.PROJECT_ID_EXTRA;

public class UpdateProgressActivity extends AppCompatActivity implements StickerAdapter.ListItemClickListener, ChooseStickerDialogFragment.EditStickerDialogListener {
    private static final String LOG_TAG = UpdateProgressActivity.class.getSimpleName();

    // Google Analytics
    Tracker mTracker;
    String screenName = "UpdateProgressActivity";

    // view model
    private UpdateProgressViewModel mViewModel;

    private int mPofileID;
    private int mProjectID;
    private ProjectEntry mProj;
    private String mParentPIN;
    private boolean mNeedApproval;

    private RecyclerView mStickersRecyclerView;
    private StickerAdapter mStickerAdapter;

    private List<Sticker> mStickerList;
    private Date mEarliest;
    private Date mLatest;

    // prevents fast clicks from opening multiple stickers
    private static final long CLICK_TIME_INTERVAL = 1000;
    private long mLastClickTime = System.currentTimeMillis()-CLICK_TIME_INTERVAL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_progress_activity);

        // Obtain the shared Google Analytics Tracker instance.
        DefaultApplication application = (DefaultApplication) getApplication();
        mTracker = application.getDefaultTracker();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
//            mPofileID = extras.getInt(PROFILE_ID_EXTRA);
            mProj = (ProjectEntry) extras.getSerializable(PROJECT_ID_EXTRA);
            mPofileID = mProj.getProfileId();
            mProjectID = mProj.getId();
            mNeedApproval = mProj.isNeedApproval();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            mParentPIN = mNeedApproval? sharedPreferences.getString("pref_password","") : "";

            UpdateProgressViewModelFactory factory = InjectorUtils.provideUpdateProgressViewModelFactory(this.getApplicationContext());
            mViewModel = ViewModelProviders.of(this, factory).get(UpdateProgressViewModel.class);

            if (mProj != null) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mStickerList = mViewModel.getStickers(mPofileID, mProjectID);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setstickerchart();
                            }
                        });

                    }
                });
            }

        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        // Send Analytics Tracking
        mTracker.setScreenName("Image~" + screenName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void setstickerchart(){
        // initialize earliest and latest dates
        setScoreDates();

        // Change title
        this.setTitle(mProj.getTitle());

        //recyclerview
        mStickersRecyclerView = (RecyclerView) findViewById(R.id.rv_sticker_book);

        // detect screen size to set column count
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        float sticker_size = getResources().getDimension(R.dimen.sticker_size)+(2*getResources().getDimension(R.dimen.margin_medium));
        int colcount = (int)(width/sticker_size);

        // layout
        GridLayoutManager layoutManager = new GridLayoutManager(this, colcount);
        mStickersRecyclerView.setLayoutManager(layoutManager);
        mStickersRecyclerView.setHasFixedSize(true);

        mStickerAdapter = new StickerAdapter(mStickerList, this, this);
        mStickersRecyclerView.setAdapter(mStickerAdapter);

    }

    private void showChooseStickerDialog(int stickerPos, Sticker sticker) {
        FragmentManager fm = getSupportFragmentManager();
        ChooseStickerDialogFragment chooseStickerDialogFragment = ChooseStickerDialogFragment.newInstance(stickerPos, sticker, mParentPIN);
        chooseStickerDialogFragment.show(fm, "fragment_sticker");
    }


    @Override
    public void OnListItemClick(int stickerPos) {

        long now = System.currentTimeMillis();
        if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
            return;
        }
        mLastClickTime = now;

        // note: stickerPos in adapter may be different from stickeSlotID
        Sticker sticker = mStickerList.get(stickerPos);
        showChooseStickerDialog(stickerPos, sticker); //(sticker.getImageSrc());

        /* ---- results will be processed in onFinishEditDialog ---- */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // catch "up" button event - to behave as "back" button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return(super.onOptionsItemSelected(item));
    }

    private boolean noDuplicateDates(int pos, Sticker s) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

        if (s.getImageSrc().length()>0) { // if empty ImageSrc, sticker was removed.
            for (int i = 0; i < mStickerList.size(); i++) {
                if ((i==pos) || (mStickerList.get(i).getImageSrc().length()==0)) {
                    continue;
                }
                if (fmt.format(s.getCompletedOn()).equals(fmt.format(mStickerList.get(i).getCompletedOn()))){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onFinishEditDialog(int stickerPos, Sticker sticker, String actionString) {

        // update this sticker in mStickerList
        mStickerList.set(stickerPos, sticker);

        // check duplicate dates
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(UpdateProgressActivity.this);
        Boolean allowMultiple = sharedPreferences.getBoolean("pref_allow_multiple",true);
        Boolean dateok = allowMultiple ? true : noDuplicateDates(stickerPos, sticker);

        if(!allowMultiple && !dateok) {
            // Show alert
            AlertDialog alertDialog = new AlertDialog.Builder(UpdateProgressActivity.this).create();
            alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
            alertDialog.setTitle(getResources().getString(R.string.alert_samedate_title));
            alertDialog.setMessage(getResources().getString(R.string.alert_samedate_dialog));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.ok_button),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

        } else {


            int complete;
            double score;

            updateScoreDates(sticker);

            switch (actionString) {
                case "add":
                    sticker.setImageSrc(sticker.getImageSrc());

                    // if update image, don't change score
                    complete = mProj.getRepCompleted();
                    mProj.setRepCompleted(complete + 1);
                    score = calcScore(complete + 1);
                    mProj.setScore(score);

                    mViewModel.updateProject(mProj);

                    /* ----- Completed Project!! --- */
                    if (mProj.getRepCompleted() == mProj.getRepTarget()) {

                        // fire event to Google Analytics
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Action")
                                .setAction("Completion")
                                .build());

                        FragmentManager fm = getSupportFragmentManager();
                        CompletionDialogFragment completionDialogFragment = CompletionDialogFragment.newInstance();
                        completionDialogFragment.show(fm, "fragment_completion");
                    }

                    break;

                case "update":

                    sticker.setImageSrc(sticker.getImageSrc());

                    // update score in case date changed. no change in completion count.
                    complete = mProj.getRepCompleted();
                    score = calcScore(complete);
                    mProj.setScore(score);
                    mViewModel.updateProject(mProj);

                    break;

                case "delete":

                    sticker.setImageSrc("");

                    complete = mProj.getRepCompleted();
                    mProj.setRepCompleted(complete - 1);
                    score = calcScore(complete - 1);
                    mProj.setScore(score);

                    mViewModel.updateProject(mProj);
                    break;
            }

            // update DB
            mViewModel.updateSticker(sticker);

            // notify adapter to update
            mStickerAdapter.updateSticker(stickerPos, sticker);

        }
    }

    private void setScoreDates() {
        mLatest = new Date(0L); // Thu Jan 01 01:00:00 GMT 1970
        mEarliest = new Date(); // today
        for(int i=0; i<mStickerList.size(); i++){
            Sticker s = mStickerList.get(i);
            if(s.getImageSrc().length()>0) { // have image = completed
                if (s.getCompletedOn().before(mEarliest)) {
                    mEarliest = s.getCompletedOn();
                }
                if (s.getCompletedOn().after(mLatest)) {
                    mLatest = s.getCompletedOn();
                }
            }
        }
    }

    private void updateScoreDates(Sticker s) {
        if (s.getCompletedOn().before(mEarliest)){
            mEarliest = s.getCompletedOn();
        }
        if (s.getCompletedOn().after(mLatest)){
            mLatest = s.getCompletedOn();
        }
    }

    private float calcScore(int completed) {
        Calendar calLatest = HelperConverter.toCalendar(mLatest);
        Calendar calEarlist = HelperConverter.toCalendar(mEarliest);
        long diffMs = calLatest.getTimeInMillis() - calEarlist.getTimeInMillis();
        long diffDays = TimeUnit.MILLISECONDS.toDays(diffMs) + 1; // Same day = Total 1 day
        return ((float) diffDays / (float) completed);
    }
}
