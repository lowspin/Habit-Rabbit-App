package com.teachableapps.capstoneproject.ui;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.teachableapps.capstoneproject.DefaultApplication;
import com.teachableapps.capstoneproject.R;
import com.teachableapps.capstoneproject.data.database.Sticker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChooseStickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private static final String LOG_TAG = ChooseStickerDialogFragment.class.getSimpleName();
    public static final String STICKER_POS_EXTRA = "TAG_STICKER_POSITION";
    public static final String STICKER_STRING_EXTRA = "TAG_STICKER_STRING";
    public static final String PARENT_PIN_EXTRA = "TAG_PARENT_PIN";

    // Google Analytics
    Tracker mTracker;
    String screenName = "ChooseStickerDialogFragment";

    private int mStickerPositon;
    private String mChosenStickerImgString;
    private Sticker mTargetSticker;
    private String mParentPIN;
    private Date mSetDate;
    private boolean mStickerExist;

    // Butterknife bindings
    @BindView(R.id.iv_sticker_01) ImageView iv01;
    @BindView(R.id.iv_sticker_02) ImageView iv02;
    @BindView(R.id.iv_sticker_03) ImageView iv03;
    @BindView(R.id.iv_sticker_04) ImageView iv04;
    @BindView(R.id.iv_sticker_05) ImageView iv05;
    @BindView(R.id.iv_sticker_06) ImageView iv06;
    @BindView(R.id.iv_sticker_07) ImageView iv07;
    @BindView(R.id.iv_sticker_08) ImageView iv08;
    @BindView(R.id.tv_date_completed) TextView mDateText;
    @BindView(R.id.et_password) EditText mEditPIN;
    @BindView(R.id.button_cancel_choose) Button btn_cancel;
    @BindView(R.id.button_ok_choose) Button btn_ok;
    @BindView(R.id.tv_remove_record) TextView tv_delete;

    public interface EditStickerDialogListener {
        void onFinishEditDialog(int stickerPos, Sticker sticker, String actionString);
    }

    // empty constructor
    public ChooseStickerDialogFragment(){}

    public static ChooseStickerDialogFragment newInstance(int stickerPos, Sticker sticker, String parentPIN) {
        ChooseStickerDialogFragment frag = new ChooseStickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(STICKER_POS_EXTRA, stickerPos);
        args.putSerializable(STICKER_STRING_EXTRA, sticker);
        args.putString(PARENT_PIN_EXTRA, parentPIN);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Send Google Analytics Tracking
        mTracker.setScreenName("Image~" + screenName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_sticker_dialog, container);

        // Butterknife
        ButterKnife.bind(this, view);

        // Get Google Analytics Tracker
        DefaultApplication application = (DefaultApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get sticker object
        mTargetSticker = (Sticker) getArguments().getSerializable(STICKER_STRING_EXTRA);

        // Get sticker position
        mStickerPositon = getArguments().getInt(STICKER_POS_EXTRA);

        // Hightlight chosen sticker if available
        mChosenStickerImgString = mTargetSticker.getImageSrc();
        mStickerExist = (mChosenStickerImgString.length()>0)? true : false;
        toggleImageBG(mChosenStickerImgString);

        // Set today's date
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        if (mStickerExist) {
            mSetDate = mTargetSticker.getCompletedOn();
        } else {
            mSetDate = new Date();
        }
        showDate();

        // Show Parent PIN field if applicable
        mParentPIN = getArguments().getString(PARENT_PIN_EXTRA);
        mEditPIN.setVisibility( (mParentPIN.length()>0)? View.VISIBLE : View.GONE );

        // hide delete option if record does not exist
        tv_delete.setVisibility( mStickerExist? View.VISIBLE: View.GONE);

        // Set OnClickListeners
        setClickListeners();
    }

    private void showDate() {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        if (DateUtils.isToday(mSetDate.getTime())) {
            mDateText.setText(df.format(mSetDate)+" ("+getResources().getString(R.string.day_today)+")");
        } else {
            mDateText.setText(df.format(mSetDate));
        }
    }

    private void toggleImageBG(String imgString){

        iv01.setBackgroundColor(getResources().getColor(R.color.toggleOff));
        iv02.setBackgroundColor(getResources().getColor(R.color.toggleOff));
        iv03.setBackgroundColor(getResources().getColor(R.color.toggleOff));
        iv04.setBackgroundColor(getResources().getColor(R.color.toggleOff));
        iv05.setBackgroundColor(getResources().getColor(R.color.toggleOff));
        iv06.setBackgroundColor(getResources().getColor(R.color.toggleOff));
        iv07.setBackgroundColor(getResources().getColor(R.color.toggleOff));
        iv08.setBackgroundColor(getResources().getColor(R.color.toggleOff));

        switch (imgString){
            case "ic_rabbit_01":
                iv01.setBackgroundColor(getResources().getColor(R.color.materialYellow));
                break;
            case "ic_rabbit_02":
                iv02.setBackgroundColor(getResources().getColor(R.color.materialYellow));
                break;
            case "ic_rabbit_03":
                iv03.setBackgroundColor(getResources().getColor(R.color.materialYellow));
                break;
            case "ic_rabbit_04":
                iv04.setBackgroundColor(getResources().getColor(R.color.materialYellow));
                break;
            case "ic_rabbit_05":
                iv05.setBackgroundColor(getResources().getColor(R.color.materialYellow));
                break;
            case "ic_rabbit_06":
                iv06.setBackgroundColor(getResources().getColor(R.color.materialYellow));
                break;
            case "ic_rabbit_07":
                iv07.setBackgroundColor(getResources().getColor(R.color.materialYellow));
                break;
            case "ic_rabbit_08":
                iv08.setBackgroundColor(getResources().getColor(R.color.materialYellow));
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Date date = new GregorianCalendar(year, month, day).getTime();
        mSetDate = date;
        showDate();
    }

    private void setClickListeners(){
        iv01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChosenStickerImgString = "ic_rabbit_01";
                toggleImageBG(mChosenStickerImgString);
            }
        });
        iv02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChosenStickerImgString = "ic_rabbit_02";
                toggleImageBG(mChosenStickerImgString);
            }
        });
        iv03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChosenStickerImgString = "ic_rabbit_03";
                toggleImageBG(mChosenStickerImgString);
            }
        });
        iv04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChosenStickerImgString = "ic_rabbit_04";
                toggleImageBG(mChosenStickerImgString);
            }
        });
        iv05.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChosenStickerImgString = "ic_rabbit_05";
                toggleImageBG(mChosenStickerImgString);
            }
        });
        iv06.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChosenStickerImgString = "ic_rabbit_06";
                toggleImageBG(mChosenStickerImgString);
            }
        });
        iv07.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChosenStickerImgString = "ic_rabbit_07";
                toggleImageBG(mChosenStickerImgString);
            }
        });
        iv08.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChosenStickerImgString = "ic_rabbit_08";
                toggleImageBG(mChosenStickerImgString);
            }
        });

        final Calendar c = Calendar.getInstance();
        int startYear = c.get(Calendar.YEAR);
        int starthMonth = c.get(Calendar.MONTH);
        int startDay = c.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(), ChooseStickerDialogFragment.this, startYear, starthMonth, startDay);

        mDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processAction("cancel");
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processAction("update");
            }
        });

        tv_delete.setOnClickListener(new View.OnClickListener() {
            private boolean confirmDelete = false;

            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle(R.string.alert_delete_confirmation);
//                alertDialog.setMessage("Are you sure?");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.alert_cancel_button),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                confirmDelete = false;
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.alert_confirm_button),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                confirmDelete = true;
                                processAction("delete");
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    private void processAction(String action) {

        EditStickerDialogListener activity = (EditStickerDialogListener) getActivity();

        switch(action){

            case "update":

                if(mChosenStickerImgString.length()>0) {

                    // set Image
                    mTargetSticker.setImageSrc(mChosenStickerImgString);

                    // set Date
                    mTargetSticker.setCompletedOn(mSetDate);

                    // set sticker's confirmed flag
                    if(mParentPIN.length()>0){
                        String pinString = mEditPIN.getText().toString();

                        if (pinString.equals(mParentPIN)) {
                            mTargetSticker.setConfirmed(true);
                        } else {

                            // if just changing image or date, no need to ask for PIN
                            if(!mStickerExist) {

                                mTargetSticker.setConfirmed(false);

                                // Show alert
                                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                                alertDialog.setTitle(getResources().getString(R.string.alert_wrongPIN_title));
                                alertDialog.setMessage(getResources().getString(R.string.alert_wrongPIN_dialog));
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.ok_button),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                            }
                        }
                    } else {
                        mTargetSticker.setConfirmed(true);
                    }

                    if (mStickerExist)
                        activity.onFinishEditDialog(mStickerPositon, mTargetSticker, "update");
                    else
                        activity.onFinishEditDialog(mStickerPositon, mTargetSticker, "add");
                }
                break;

            case "delete":
                if(mStickerExist) {
                    mTargetSticker.setImageSrc(""); // just to be consistent
                    activity.onFinishEditDialog(mStickerPositon, mTargetSticker, "delete");
                }
                break;
        }

        getDialog().dismiss();
    }
}
