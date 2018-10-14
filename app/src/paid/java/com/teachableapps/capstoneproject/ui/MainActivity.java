package com.teachableapps.capstoneproject.ui;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.teachableapps.capstoneproject.DefaultApplication;
import com.teachableapps.capstoneproject.R;
import com.teachableapps.capstoneproject.data.database.InjectorUtils;
import com.teachableapps.capstoneproject.data.database.ProjectEntry;

import java.util.List;

import static com.teachableapps.capstoneproject.data.database.HelperConverter.toBool7ArrayFromInt;
import static com.teachableapps.capstoneproject.data.database.HelperConverter.toTimeFromInt;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    //    public static final String PROFILE_ID_EXTRA = "TAG_PROFILE_ID";
    public static final String PROJECT_ID_EXTRA = "TAG_PROJECT_ID";
    public static final int TAB_ID_EXTRA = 124;

    // Google Analytics Tracker
    Tracker mTracker;
    String screenName = "MainActivityPaid";

    // Viewpager and tabs
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private int currentTab;

    // view model
    private MainViewModel mViewModel;

    // member variables
    private int mProfileID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Obtain the shared Google Analytics Tracker instance.
        DefaultApplication application = (DefaultApplication) getApplication();
        mTracker = application.getDefaultTracker();

        // get Profile ID - for future expansion (multi-profile)
        mProfileID = 0;

        // main Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Create the adapter that will return a fragment for each primary section.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Attach the sections adapter to the ViewPager.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // tabs layout element
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        currentTab = 0;

        // set Tab listeners
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout)
        {
            @Override
            public void onPageSelected(int position) {
                currentTab = position;
            }
        });
        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        // FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabAction();
            }
        });

        // view model
        MainViewModelFactory factory = InjectorUtils.provideMainActivityViewModelFactory(this.getApplicationContext(), mProfileID);
        mViewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);
        mViewModel.getProjects().observe(this, new Observer<List<ProjectEntry>>() {
            @Override
            public void onChanged(@Nullable List<ProjectEntry> projectEntries) {

                // Update KPI in Shared Preference for Widget
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = sharedPref.edit();
                //number of projects
                editor.putInt(getString(R.string.KEY_NUM_PROJECTS), projectEntries.size());
                //average completion
                float avgCompletion = 0;
                for(int i=0; i<projectEntries.size(); i++) {
                    ProjectEntry p = projectEntries.get(i);
                    avgCompletion += (float) p.getRepCompleted() / (float) p.getRepTarget();
                }
                avgCompletion /= projectEntries.size();
                editor.putFloat(getString(R.string.KEY_AVG_COMPLETION), avgCompletion);
                editor.commit();

                // Update Widgets
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(MainActivity.this);
                int[] appWidgets = appWidgetManager.getAppWidgetIds(new ComponentName(MainActivity.this, HabitWidget.class));
                HabitWidget.updateHabitWidgets(MainActivity.this, appWidgetManager, appWidgets);

                // Update ViewPager
                if(projectEntries.size() > mTabLayout.getTabCount()) { // new project

                    // notify adapter which will take care of all updating since we used setupWithViewPager
                    mSectionsPagerAdapter.setProjects(projectEntries);

                    // now num of tabs = num of projects
                    currentTab = projectEntries.size()-1;
                    mViewPager.setCurrentItem(currentTab);

                } else {

                    // notify adapter which will take care of all updating since we used setupWithViewPager
                    mSectionsPagerAdapter.setProjects(projectEntries);
                }
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mTracker.setScreenName("Image~" + screenName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void updateProgress() {
        List<ProjectEntry> projects = mSectionsPagerAdapter.getProjects();

        final ProjectEntry thisproject = projects.get(mTabLayout.getSelectedTabPosition());

        // update/fill sticker slots
        mViewModel.updateStickerSlots(thisproject);

        Intent updateProgressIntent = new Intent(MainActivity.this, UpdateProgressActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable(PROJECT_ID_EXTRA,thisproject);
        updateProgressIntent.putExtras(bundle);

        startActivity(updateProgressIntent);
    }

    private void editProject() {
        List<ProjectEntry> projects = mSectionsPagerAdapter.getProjects();
        if (projects.size()>0) {
            ProjectEntry thisproject = projects.get(mTabLayout.getSelectedTabPosition());

            Intent editProjectIntent = new Intent(MainActivity.this, AddProjectActivity.class);

            Bundle bundle = new Bundle();
            bundle.putSerializable(PROJECT_ID_EXTRA,thisproject);
            editProjectIntent.putExtras(bundle);

            startActivity(editProjectIntent);
        } else {
            Snackbar.make(findViewById(R.id.fab), R.string.main_project_add_project_message, Snackbar.LENGTH_LONG)
                .setAction(R.string.main_project_add_project_action, new AddProjectActionListener()).show();
        }
    }

    private void newProject() {

        ProjectEntry proj = new ProjectEntry(-1, 0, "", "", 0, 0, 0.0, 0, 0, false);

        Intent addProjectIntent = new Intent(MainActivity.this, AddProjectActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable(PROJECT_ID_EXTRA,proj);
        addProjectIntent.putExtras(bundle);

        startActivity(addProjectIntent);

    }

    private void deleteProject() {

        List<ProjectEntry> projects = mSectionsPagerAdapter.getProjects();
        if (projects.size()>0) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.confirm_delete_project_title))
                    .setMessage(getString(R.string.confirm_delete_project_text))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            final int position = mTabLayout.getSelectedTabPosition();
                            List<ProjectEntry> projects = mSectionsPagerAdapter.getProjects();
                            mViewModel.deleteProject(projects.get(position));

                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        } else {
            Snackbar.make(findViewById(R.id.fab), R.string.main_project_add_project_message, Snackbar.LENGTH_LONG)
                    .setAction(R.string.main_project_add_project_action, new AddProjectActionListener()).show();
        }
    }

    private void changeSettings() {

        Intent changePasswordIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(changePasswordIntent);
    }

    private void fabAction() {
        List<ProjectEntry> projects = mSectionsPagerAdapter.getProjects();
        if (projects.size()>0) {
            updateProgress();
        } else {
            Snackbar.make(findViewById(R.id.fab), R.string.main_project_add_project_message, Snackbar.LENGTH_LONG)
                    .setAction(R.string.main_project_add_project_action, new AddProjectActionListener()).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {

            case R.id.action_edit_project:
                editProject();
                break;

            case R.id.action_new_project:
                newProject();
                break;

            case R.id.action_delete_project:
                deleteProject();
                break;

            case R.id.action_settings:
                changeSettings();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // For snackbar
    public class AddProjectActionListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            newProject();
        }
    }
    /* ----------------------------------------------- */

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_PROJ_TITLE = "project_title";
        private static final String ARG_PROJ_DESC = "project_description";
        private static final String ARG_PROJ_REPS = "project_reps";
        private static final String ARG_PROJ_COMPLETE = "project_complete";
        private static final String ARG_PROJ_SCORE = "project_score";
        private static final String ARG_PROJ_REMIND = "project_remindMins";
        private static final String ARG_PROJ_DAYS = "project_practiceDays";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, String title, String description, int reps, int completed, double score, int remindMins, int practiceDays) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_PROJ_TITLE, title);
            args.putString(ARG_PROJ_DESC, description);
            args.putInt(ARG_PROJ_REPS, reps);
            args.putInt(ARG_PROJ_COMPLETE, completed);
            args.putDouble(ARG_PROJ_SCORE, score);
            args.putInt(ARG_PROJ_REMIND, remindMins);
            args.putInt(ARG_PROJ_DAYS, practiceDays);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.main_fragment, container, false);

            // Title
            TextView tv_title = (TextView) rootView.findViewById(R.id.proj_title);
            tv_title.setText(getArguments().getString(ARG_PROJ_TITLE));

            // Description
            TextView tv_desc = (TextView) rootView.findViewById(R.id.proj_desc);
            String desc = getArguments().getString(ARG_PROJ_DESC);
            if (desc.length()>0)
                tv_desc.setText(desc);
            else
                tv_desc.setVisibility(View.GONE);

            // Days of week
            TextView tv_practice = (TextView) rootView.findViewById(R.id.proj_practice);
            int practicedaysInt = getArguments().getInt(ARG_PROJ_DAYS);
            boolean[] daysofWeek = toBool7ArrayFromInt(practicedaysInt);

            String practiceDaysString = "";
            if(getArguments().getInt(ARG_PROJ_DAYS)==127) {
                practiceDaysString = getString(R.string.day_every) + " ";
            } else {
                if (daysofWeek[0])
                    practiceDaysString += (getResources().getString(R.string.day_sunday) + " ");
                if (daysofWeek[1])
                    practiceDaysString += (getResources().getString(R.string.day_monday) + " ");
                if (daysofWeek[2])
                    practiceDaysString += (getResources().getString(R.string.day_tuesday) + " ");
                if (daysofWeek[3])
                    practiceDaysString += (getResources().getString(R.string.day_wednesday) + " ");
                if (daysofWeek[4])
                    practiceDaysString += (getResources().getString(R.string.day_thursday) + " ");
                if (daysofWeek[5])
                    practiceDaysString += (getResources().getString(R.string.day_friday) + " ");
                if (daysofWeek[6])
                    practiceDaysString += (getResources().getString(R.string.day_saturday) + " ");
            }

            // Show Schedule and Reminder Time, if set
            int remindMins = getArguments().getInt(ARG_PROJ_REMIND);
            if (remindMins>-1) {
                if(practiceDaysString.length()>0) {
                    practiceDaysString += "@ " + toTimeFromInt(remindMins);
                    tv_practice.setText(getResources().getString(R.string.main_project_pracice) + " " + practiceDaysString);
                } else {
                    tv_practice.setText(getResources().getString(R.string.main_project_pracice) + " " + getResources().getString(R.string.day_any) +
                            " @ " + toTimeFromInt(remindMins) + " (" + getResources().getString(R.string.no_alarm) +")");
                }
                tv_practice.setVisibility(View.VISIBLE);
            } else {
                tv_practice.setVisibility(View.GONE);
            }

            final ProgressBar pb_reps = (ProgressBar) rootView.findViewById(R.id.proj_progress_ring);
            final double completion_percent = 100.0 * ((double) getArguments().getInt(ARG_PROJ_COMPLETE)) / ((double) getArguments().getInt(ARG_PROJ_REPS));
            pb_reps.post(new Runnable() {
                @Override
                public void run() {
                    pb_reps.setProgress((int)completion_percent);
                }
            });

            // Progress - text
            TextView tv_percent = (TextView) rootView.findViewById(R.id.proj_progress_text);
            if(completion_percent>0) {
                // percentage text inside progress circle
                tv_percent.setText(String.format("%.1f", completion_percent) + "%");

                // Progress counter
                TextView tv_reps = (TextView) rootView.findViewById(R.id.tv_progress_count);
                tv_reps.setText(String.valueOf(getArguments().getInt(ARG_PROJ_COMPLETE)) + " / " + String.valueOf(getArguments().getInt(ARG_PROJ_REPS)));

                // Score
                TextView tv_score = (TextView) rootView.findViewById(R.id.tv_score);
                tv_score.setText(String.valueOf(getArguments().getDouble(ARG_PROJ_SCORE)));

                ((ImageView)rootView.findViewById(R.id.iv_get_started)).setVisibility(View.GONE);
            } else {
                tv_percent.setText(getResources().getString(R.string.main_project_get_started));
                tv_percent.setTextColor(getResources().getColor(R.color.colorAccent));
                ((ConstraintLayout)rootView.findViewById(R.id.group_stats)).setVisibility(LinearLayout.GONE);
                ((ImageView)rootView.findViewById(R.id.iv_get_started)).setVisibility(View.VISIBLE);
            }

            tv_percent.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    ((MainActivity)getActivity()).updateProgress();
                }
            });

            return rootView;
        }
    }

    /**
     * A FragmentStatePagerAdapter that creates and updates fragments corresponding to
     * the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private List<ProjectEntry> pList;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public List<ProjectEntry> getProjects() { return pList; }

        public void setProjects(List<ProjectEntry> projectLIst){
            //if(pList != null) pList.clear();
            pList = projectLIst;
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class).
            return PlaceholderFragment.newInstance(
                    position,
                    pList.get(position).getTitle(),
                    pList.get(position).getDescription(),
                    pList.get(position).getRepTarget(),
                    pList.get(position).getRepCompleted(),
                    pList.get(position).getScore(),
                    pList.get(position).getRemindMins(),
                    pList.get(position).getPracticeDays()
            );
        }

        // This is important for deleting/updating to always force fragments to recreate its view hierarchy again with new data.
        // ref: https://stackoverflow.com/questions/33342613/replace-current-fragment-in-viewpager-on-android
        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pList.get(position).getTitle();
        }

        @Override
        public int getCount() {
            // total number of pages.
            if(pList == null) {
                return 0;
            }
            return pList.size();
        }
        
    }

}
