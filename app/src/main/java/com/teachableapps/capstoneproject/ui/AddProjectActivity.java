package com.teachableapps.capstoneproject.ui;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.teachableapps.capstoneproject.DefaultApplication;
import com.teachableapps.capstoneproject.R;
import com.teachableapps.capstoneproject.data.database.InjectorUtils;
import com.teachableapps.capstoneproject.data.database.ProjectEntry;
import com.teachableapps.capstoneproject.utilities.NotificationPublisher;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.teachableapps.capstoneproject.data.database.HelperConverter.toBool7ArrayFromInt;
import static com.teachableapps.capstoneproject.data.database.HelperConverter.toIntFromBool7Array;
import static com.teachableapps.capstoneproject.data.database.HelperConverter.toMinsFromString;
import static com.teachableapps.capstoneproject.data.database.HelperConverter.toTimeFromInt;
import static com.teachableapps.capstoneproject.ui.MainActivity.PROJECT_ID_EXTRA;

public class AddProjectActivity extends AppCompatActivity {
    private static final String LOG_TAG = AddProjectActivity.class.getSimpleName();
//    private static final int HABIT_REMINDER_NOTIFICATION_ID = 1138;
    private static final int HABIT_REMINDER_PENDING_INTENT_ID = 3417;
    private static final String HABIT_REMINDER_NOTIFICATION_CHANNEL_ID = "reminder_notification_channel";

    // Google Analytics
    Tracker mTracker;
    String screenName = "AddProjectActivity";

    // view model
    private AddProjectViewModel mViewModel;

    // private HabitDatabase mDb;
    private int mProfileID;
    private int mProjectID;
    private ProjectEntry mProj;
    private int originalRepTarget;

    // to facilitate time
    private int mHour=0;
    private int mMins=0;

    // Butterknife bindings
    @BindView(R.id.editTextProjectTitle) EditText et_title;
    @BindView(R.id.editTextProjectDescription) EditText et_desc;
    @BindView(R.id.editTextRepetition) EditText et_rep;
    @BindView(R.id.tv_reminder_time) TextView tv_reminder_time;
    @BindView(R.id.reminderSwitch) Switch reminderSwitch;
    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.block_reminder_settings) ConstraintLayout ll_togglegroup_day;
    @BindView(R.id.tg_switch_day0) ToggleButton tg_day0;
    @BindView(R.id.tg_switch_day1) ToggleButton tg_day1;
    @BindView(R.id.tg_switch_day2) ToggleButton tg_day2;
    @BindView(R.id.tg_switch_day3) ToggleButton tg_day3;
    @BindView(R.id.tg_switch_day4) ToggleButton tg_day4;
    @BindView(R.id.tg_switch_day5) ToggleButton tg_day5;
    @BindView(R.id.tg_switch_day6) ToggleButton tg_day6;
    @BindView(R.id.switch_password) Switch passwordSwitch;
    @BindView(R.id.tv_whatspasswordfor) TextView tv_whatspasswordfor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_project_activity);

        // Obtain the shared Google Analytics Tracker instance.
        DefaultApplication application = (DefaultApplication) getApplication();
        mTracker = application.getDefaultTracker();

        // Butterknife
        ButterKnife.bind(this);

        // view model
        AddProjectViewModelFactory factory = InjectorUtils.provideAddProjectViewModelFactory(this.getApplicationContext());
        mViewModel = ViewModelProviders.of(this, factory).get(AddProjectViewModel.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mProj = (ProjectEntry) extras.getSerializable(PROJECT_ID_EXTRA);
            mProfileID = mProj.getProfileId();
            mProjectID = mProj.getId();
        }

        // New Project or Edit Existing?
        if(mProjectID<0) {
            // new project
            this.setTitle(getResources().getString(R.string.title_new_proj));

            // no data - set to -1
            originalRepTarget = -1;

            // set all days to true for new projects
            tg_day0.setChecked(true);
            tg_day1.setChecked(true);
            tg_day2.setChecked(true);
            tg_day3.setChecked(true);
            tg_day4.setChecked(true);
            tg_day5.setChecked(true);
            tg_day6.setChecked(true);

            // default = reminder offf
            reminderSwitch.setChecked(false);

            // Hide reminder day and time initially for new projects
            ll_togglegroup_day.setVisibility(View.GONE);
            tv_reminder_time.setVisibility(View.GONE);

            // change time by clicking on it
            tv_reminder_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setReminderTime(7,0);
                }
            });

            //reminder switch
            reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    tv_reminder_time.setVisibility(isChecked?View.VISIBLE:View.GONE);
                    ll_togglegroup_day.setVisibility(isChecked?View.VISIBLE:View.GONE);
                }
            });

            // password switch
            passwordSwitch.setChecked(false);

            // save button text
            saveButton.setText(getResources().getString(R.string.add_button));

        } else {
            // update existing project - load existing parameters

            this.setTitle(getResources().getString(R.string.title_edit_proj) + " " + String.valueOf(mProjectID));

            if(mProj!=null) {

                // get RepTarget to align number of sticker slots
                originalRepTarget = mProj.getRepTarget();

                et_title.setText(mProj.getTitle());
                et_desc.setText(mProj.getDescription());
                et_rep.setText(String.valueOf(originalRepTarget));

                // Days of week
                boolean[] daysofWeek = toBool7ArrayFromInt(mProj.getPracticeDays());
                tg_day0.setChecked(daysofWeek[0]);
                tg_day1.setChecked(daysofWeek[1]);
                tg_day2.setChecked(daysofWeek[2]);
                tg_day3.setChecked(daysofWeek[3]);
                tg_day4.setChecked(daysofWeek[4]);
                tg_day5.setChecked(daysofWeek[5]);
                tg_day6.setChecked(daysofWeek[6]);

                // Reminder Time
                int remindMins = mProj.getRemindMins();
                final int showHour = remindMins/60;
                final int showMins = remindMins - (showHour*60);
                tv_reminder_time.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setReminderTime(showHour, showMins);
                    }
                });

                if(remindMins<0) {
                    reminderSwitch.setChecked(false);
                    tv_reminder_time.setVisibility(View.GONE);
                    ll_togglegroup_day.setVisibility(View.GONE);
                } else {
                    reminderSwitch.setChecked(true);
                    tv_reminder_time.setText(toTimeFromInt(remindMins));
                    tv_reminder_time.setVisibility(View.VISIBLE);
                    ll_togglegroup_day.setVisibility(View.VISIBLE);
                }

                //reminder switch
                reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        tv_reminder_time.setVisibility(isChecked?View.VISIBLE:View.GONE);
                        ll_togglegroup_day.setVisibility(isChecked?View.VISIBLE:View.GONE);
                    }
                });

                // Require password switch
                boolean requirePassword = mProj.isNeedApproval();
                passwordSwitch.setChecked(requirePassword);

            }

            // save button text
            saveButton.setText(getResources().getString(R.string.update_button));
        }

        // reminder to set password
        passwordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AddProjectActivity.this);
                    if(sharedPreferences.getString("pref_password","").length()==0) {
                        // Show alert
                        AlertDialog alertDialog = new AlertDialog.Builder(AddProjectActivity.this).create();
                        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                        alertDialog.setTitle(getResources().getString(R.string.reminder_set_password_title));
                        alertDialog.setMessage(getResources().getString(R.string.reminder_set_password_description));
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.ok_button),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                }
            }
        });


        // What's password for?
        tv_whatspasswordfor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show alert
                AlertDialog alertDialog = new AlertDialog.Builder(AddProjectActivity.this).create();
                alertDialog.setIcon(android.R.drawable.ic_dialog_info);
                alertDialog.setTitle(getResources().getString(R.string.whats_that));
                alertDialog.setMessage(getResources().getString(R.string.require_approval_description));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.ok_button),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        // button click
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String title = ((EditText) findViewById(R.id.editTextProjectTitle)).getText().toString();
                if (title.length()==0) {
                    title = "[Habit]";
                }

                String description = ((EditText) findViewById(R.id.editTextProjectDescription)).getText().toString();

                int repTarget = Integer.parseInt(((EditText) findViewById(R.id.editTextRepetition)).getText().toString());

                boolean[] daysofWeek = new boolean[7];
                daysofWeek[0] = tg_day0.isChecked(); // Sun
                daysofWeek[1] = tg_day1.isChecked(); // Mon
                daysofWeek[2] = tg_day2.isChecked(); // Tue
                daysofWeek[3] = tg_day3.isChecked(); // Wed
                daysofWeek[4] = tg_day4.isChecked(); // Thu
                daysofWeek[5] = tg_day5.isChecked(); // Fri
                daysofWeek[6] = tg_day6.isChecked(); // Sat
                int daysofweekInt = toIntFromBool7Array(daysofWeek);

                int remindMins = -1;
                if(reminderSwitch.isChecked()) {
                    String remindTimeStr = tv_reminder_time.getText().toString();
                    remindMins = toMinsFromString(remindTimeStr);

                    // Set reminder notifications
                    schduleNotification(title,0, mHour, mMins, daysofWeek[0]? true: false); // Sun
                    schduleNotification(title,1, mHour, mMins, daysofWeek[1]? true: false); // Mon
                    schduleNotification(title,2, mHour, mMins, daysofWeek[2]? true: false); // Tue
                    schduleNotification(title,3, mHour, mMins, daysofWeek[3]? true: false); // Wed
                    schduleNotification(title,4, mHour, mMins, daysofWeek[4]? true: false); // Thu
                    schduleNotification(title,5, mHour, mMins, daysofWeek[5]? true: false); // Fri
                    schduleNotification(title,6, mHour, mMins, daysofWeek[6]? true: false); // Sat

                }

                boolean needApproval = passwordSwitch.isChecked();

                if(mProjectID<0) {
                    //new project
                    mProj = new ProjectEntry(mProfileID, title, description, repTarget, 0, 0.0, remindMins, daysofweekInt, needApproval);
                    mViewModel.insertProject(mProj);

                    // fire event to Google Analytics
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Action")
                            .setAction("NewProject")
                            .build());

                    // close activity
                    finish();

                } else {
                    //update project
                    int completed = mProj.getRepCompleted();
                    double score = mProj.getScore();
                    mProj = new ProjectEntry(mProjectID, mProfileID, title, description, repTarget, completed, score, remindMins, daysofweekInt, needApproval);
                    mViewModel.updateProject(mProj);

                    // close activity
                    finish();
                }
            }
        });

    }

    private void schduleNotification(String title, int remindDay, int remindHour, int remindMins, boolean onOff) {

        Context context = this;

        // Get the NotificationManager using context.getSystemService
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel for Android O devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    HABIT_REMINDER_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH); // force notification to pop-up in HUD
            notificationManager.createNotificationChannel(mChannel);
        }

        // Use NotificationCompat.Builder to create a notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,HABIT_REMINDER_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary)) // - has a color of R.colorPrimary - use ContextCompat.getColor to get a compatible color
                .setSmallIcon(R.drawable.ic_rabbit_01) // - has ic_drink_notification as the small icon
                .setLargeIcon( drawableToBitmap( getResources().getDrawable( R.drawable.ic_rabbit_01 ) ) ) //largeIcon(context)) // - use same icon as the large icon
                .setContentTitle(title) //(context.getString(R.string.habit_reminder_notification_title)) // - sets the title to the habit_reminder_notification_title String resource
                .setContentText(context.getString(R.string.habit_reminder_notification_body)) // - sets the text to the habit_reminder_notification_body String resource
                .setStyle(new NotificationCompat.BigTextStyle().bigText( // - sets the style to NotificationCompat.BigTextStyle().bigText(text)
                        context.getString(R.string.habit_reminder_notification_body)))
                .setDefaults(Notification.DEFAULT_VIBRATE) // - sets the notification defaults to vibrate
                .setContentIntent(contentIntent(context)) // - uses the content intent returned by the contentIntent helper method for the contentIntent
                .setAutoCancel(true); // - automatically cancels the notification when the notification is clicked

        // If the build version is greater than JELLY_BEAN and lower than OREO, set the notification's priority to PRIORITY_HIGH.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        Notification notification = notificationBuilder.build();

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);

        // use profileID.projectID.dayofweek as unique id
        int requestId = (mProfileID*1000) + (mProjectID*10) + remindDay;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if(onOff) { // turn on notification
            long futureInMillis = calcFutureInMillis(remindDay, remindHour, remindMins);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, futureInMillis, 7 * AlarmManager.INTERVAL_DAY, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
        }
    }

    // Create the pending intent which will trigger when the notification is pressed, and open the MainActivity.
    private static PendingIntent contentIntent(Context context) {
        Intent startActivityIntent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(
                context,
                HABIT_REMINDER_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT); //if the intent is created again, keep the intent but update the data
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    long calcFutureInMillis(int remindDay, int remindHour, int remindMins) {
        // Note about numeric conventions:
        // remindDay: Sun(0), Mon(1), Tue(2), Wed(3), Thu(4), Fri(5), Sat(6)
        // android Calendar (France): Mon(1), Tue(2), Wed(3), Thu(4), Fri(5), Sat(6), Sun(7)
        // android Calendar (USA)   : Sun(1), Mon(2), Tue(3), Wed(4), Thu(5), Fri(6), Sat(7)

        int remindDayAndroid;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        if(calendar.getFirstDayOfWeek()==2) { // firsy of week is Monday, e.g. France
            remindDayAndroid = (remindDay>0)? remindDay + 1 : remindDay;
        } else { // first day of week is Sunday (consistent with our convention)
            remindDayAndroid = remindDay + 1;
        }

        // Special case: next occurence is later today
        if( calendar.get(Calendar.DAY_OF_WEEK) == remindDayAndroid) {
            if( calendar.get(Calendar.HOUR_OF_DAY) <= remindHour ) {
                if ( calendar.get(Calendar.MINUTE) <= remindMins) {
                    calendar.set(Calendar.HOUR_OF_DAY, remindHour);
                    calendar.set(Calendar.MINUTE, remindMins);
                    return calendar.getTimeInMillis();
                }
            }
        }

        // General case
        int day_of_week_today = calendar.get(Calendar.DAY_OF_WEEK);
        int diff = remindDayAndroid - day_of_week_today;
        if (diff<=0) { // even if same day, time will be in the past, else will be returned already
            diff += 7;
        }
        calendar.set(Calendar.HOUR_OF_DAY, remindHour);
        calendar.set(Calendar.MINUTE, remindMins);
        calendar.add(Calendar.DAY_OF_WEEK, diff);
        return calendar.getTimeInMillis();
    }

    private void setReminderTime(int showHour, int showMins) {
        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(AddProjectActivity.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        copyTime( hourOfDay, minute );

                        if (minute < 10)
                            tv_reminder_time.setText(hourOfDay + ":0" + minute);
                        else
                            tv_reminder_time.setText(hourOfDay + ":" + minute);

                    }
                }, showHour, showMins, false);
        timePickerDialog.show();
    }

    private void copyTime(int hour, int mins) {
        this.mHour = hour;
        this.mMins = mins;
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

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mTracker.setScreenName("Image~" + screenName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
