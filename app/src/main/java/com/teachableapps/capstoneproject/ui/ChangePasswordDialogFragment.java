package com.teachableapps.capstoneproject.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.teachableapps.capstoneproject.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangePasswordDialogFragment extends DialogFragment {
    private static final String LOG_TAG = ChangePasswordDialogFragment.class.getSimpleName();

    // Butterknife bindings
    @BindView(R.id.et_password_new1) EditText et_password_new1;
    @BindView(R.id.et_password_new2) EditText et_password_new2;
    @BindView(R.id.et_password_old) EditText et_password_old;
    @BindView(R.id.btn_change_PIN) Button btn_password_change;

    // empty constructor
    public ChangePasswordDialogFragment(){}

    public static ChangePasswordDialogFragment newInstance() {
        ChangePasswordDialogFragment frag = new ChangePasswordDialogFragment();
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.change_password_dialog, container);
        // Butterknife
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_password_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG,"Change Password");
            }
        });
    }
}
