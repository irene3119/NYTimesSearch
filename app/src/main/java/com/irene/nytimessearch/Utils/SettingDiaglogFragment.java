package com.irene.nytimessearch.Utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.irene.nytimessearch.Models.Settings;
import com.irene.nytimessearch.R;


/**
 * Created by Irene on 2017/2/22.
 */

public class SettingDiaglogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{


    private EditText etBeginDate;
    private Spinner spinner;
    private Button saveBtn;
    private CheckBox checkbox_arts;
    private CheckBox checkbox_fashion;
    private CheckBox checkbox_sports;
    private SettingItemDialogListener mCallback;

    private Settings settings;



    public SettingDiaglogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below

    }

    public static SettingDiaglogFragment newInstance(Settings settings) {
        SettingDiaglogFragment frag = new SettingDiaglogFragment();
        if(settings!= null)
        {
            Bundle args = new Bundle();
            args.putSerializable("settings",settings);
            frag.setArguments(args);
        }
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setting_fragment, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etBeginDate = (EditText) view.findViewById(R.id.etBeginDate);
        spinner = (Spinner) view.findViewById(R.id.spSort);
        saveBtn = (Button) view.findViewById(R.id.btnSave);
        checkbox_arts = (CheckBox) view.findViewById(R.id.checkbox_arts);
        checkbox_fashion = (CheckBox) view.findViewById(R.id.checkbox_fashion);
        checkbox_sports = (CheckBox) view.findViewById(R.id.checkbox_sports);
        settings = new Settings();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Onclick", etBeginDate.getText().toString());
                settings.setBeginDate(etBeginDate.getText().toString().replace("/",""));
                settings.setSort(spinner.getSelectedItem().toString());
                // Check if the checkbox is checked
                settings.setCheck_Arts(checkbox_arts.isChecked());
                settings.setCheck_Fashion(checkbox_fashion.isChecked());
                settings.setCheck_Sports(checkbox_sports.isChecked());

                SettingItemDialogListener listener = (SettingItemDialogListener) getActivity();

                listener.onFinishSettingDialog(settings);
                dismiss();

            }
        });
        // Fetch arguments from bundle and set title
        if(getArguments() != null)
        {
            settings = (Settings) getArguments().getSerializable("settings");
            if(settings != null) {

                if(!settings.getBeginDate().equals("")) {
                    String beginDate = settings.getBeginDate();
                    etBeginDate.setText(beginDate.substring(0, 4) + "/" + beginDate.substring(4, 6) + "/" + beginDate.substring(6));
                }
                if (settings.getSort().equals("oldest"))
                    spinner.setSelection(0);
                else
                    spinner.setSelection(1);

                if (settings.Check_Arts)
                    checkbox_arts.setChecked(true);
                if (settings.Check_Fashion)
                    checkbox_fashion.setChecked(true);
                if (settings.Check_Sports)
                    checkbox_sports.setChecked(true);
            }
        }

        etBeginDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                onCalendarClick();
            }
        });


        getDialog().setTitle("Settings");
        // Show soft keyboard automatically and request focus to field

        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            if (context instanceof SettingItemDialogListener){
                mCallback = (SettingItemDialogListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }


    // Defines the listener interface
    public interface SettingItemDialogListener {
        void onFinishSettingDialog(Settings settings);
    }

    public void onCalendarClick(){
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getChildFragmentManager(), "datePicker");
    }

    // handle the date selected
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // store the values selected into a Calendar instance
        Log.e("DEBUG","datePicker");
        String day = dayOfMonth >= 10 ? Integer.toString(dayOfMonth) : "0" + Integer.toString(dayOfMonth);
        String month = monthOfYear >= 10 ? Integer.toString(monthOfYear) : "0" + Integer.toString(monthOfYear);
        updateSelectedDate(Integer.toString(year) + "/" + month + "/" + day);

    }

    public void updateSelectedDate(String selectedDate){
        Log.d("Debug", "updateSelectedDate "+selectedDate);
        etBeginDate.setText(selectedDate);
    }

}
