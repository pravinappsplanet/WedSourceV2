package in.appsplanet.wedsource.event;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import in.appsplanet.wedsource.R;
import in.appsplanet.wedsource.WedSourceApp;
import in.appsplanet.wedsource.calendar.CalendarActivity;
import in.appsplanet.wedsource.pojo.Event;
import in.appsplanet.wedsource.utils.AppSettings;
import in.appsplanet.wedsource.utils.Constants;
import in.appsplanet.wedsource.utils.CustomRequest;
import in.appsplanet.wedsource.utils.IOUtils;

public class EditEventFragment extends Fragment implements OnClickListener,
        Constants {
    private Context mContext;
    private Button mBtnSubmit, mBtnDate, mBtnTime;
    private EditText mEdtName, mEdtVenue, mEdtDescription;
    private AppSettings mAppSettings;
    private Calendar newCalendar = Calendar.getInstance();
    private Event mEvent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_event, null);
        mContext = getActivity();

        mBtnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        mBtnSubmit.setOnClickListener(this);
        mBtnTime = (Button) view.findViewById(R.id.btnTime);
        mBtnTime.setOnClickListener(this);
        mBtnDate = (Button) view.findViewById(R.id.btnDate);
        mBtnDate.setOnClickListener(this);

        mEdtName = (EditText) view.findViewById(R.id.edtName);
        mEdtVenue = (EditText) view.findViewById(R.id.edtVenue);
        mEdtDescription = (EditText) view.findViewById(R.id.edtDescription);
        mAppSettings = new AppSettings(mContext);

        mEvent = (Event) getArguments().get(Constants.INTENT_EVENT);

        loadData();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof EventActivity) {
            ((EventActivity) getActivity()).setHeader("Edit Event");
            ((EventActivity) getActivity()).mImgHeaderAdd.setVisibility(View.GONE);
        }

        if (getActivity() instanceof CalendarActivity) {
            ((CalendarActivity) getActivity()).setHeader("Edit Event");
            ((CalendarActivity) getActivity()).mImgHeaderAdd.setVisibility(View.GONE);
        }
    }

    private void loadData() {
        mEdtName.setText(mEvent.getName());
        mEdtDescription.setText(mEvent.getDescription());
        mEdtVenue.setText(mEvent.getVenue());
        mBtnDate.setText(mEvent.getDate());
        mBtnTime.setText(mEvent.getTime());
        Log.d("test", "time load" + mEvent.getTime());
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(Constants.DATE_FORMAT);
            newCalendar.setTime(dateFormatter.parse(mEvent.getDate()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit: // SUBMIT
                String name = mEdtName.getText().toString().trim();
                String venue = mEdtVenue.getText().toString().trim();
                String description = mEdtDescription.getText().toString().trim();
                String date = mBtnDate.getText().toString().trim();
                String time = mBtnTime.getText().toString().trim();

                //VALIDATIONS
                if (TextUtils.isEmpty(name)) {
                    mEdtName.setError("Please enter name");
//                } else if (TextUtils.isEmpty(venue)) {
//                    mEdtVenue.setError("Please enter venue");
//                } else if (TextUtils.isEmpty(description)) {
//                    mEdtDescription.setError("Please enter details");
//                } else if (mBtnTime.getText().toString().equalsIgnoreCase("Time")) {
//                    Toast.makeText(mContext, "Please select time", Toast.LENGTH_SHORT).show();
//                } else if (mBtnDate.getText().toString().equalsIgnoreCase("Date")) {
//                    Toast.makeText(mContext, "Please select date", Toast.LENGTH_SHORT).show();
                } else {
                    if (mBtnDate.getText().toString().equalsIgnoreCase("Date"))
                        date = "";
                    if (mBtnTime.getText().toString().equalsIgnoreCase("Time"))
                        time = "";

                    addEvent(name, venue, description, date, time);
                }
                break;

            case R.id.btnDate://DATE

                final SimpleDateFormat dateFormatter = new SimpleDateFormat(Constants.DATE_FORMAT);
                DatePickerDialog toDatePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        mBtnDate.setText(dateFormatter.format(newDate.getTime()));
                    }

                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                toDatePickerDialog.setTitle("Select Date");
                toDatePickerDialog.show();
                break;

            case R.id.btnTime://TIME
                final Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String myFormat = "hh:mm a"; // your own format
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                        mcurrentTime.set(Calendar.MINUTE,selectedMinute);
                        mcurrentTime.set(Calendar.HOUR,selectedHour);
                        String formated_time = sdf.format(mcurrentTime.getTime());
                        Log.d("test","time"+formated_time);
                        mBtnTime.setText(formated_time);
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
                break;
            default:
                break;
        }
    }


    /**
     * @param name
     * @param description
     */
    private void addEvent(String name, String venue, String description, String date, String time) {
        String tag_json_obj = "addEvent";
        ContentValues values = new ContentValues();
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_EDITEVENT);
        values.put(Constants.PARAM_NAME, name);
        values.put(Constants.PARAM_VENUE, venue);
        values.put(Constants.PARAM_DESCRIPTION, description);
        values.put(Constants.PARAM_DATE, date);
        values.put(Constants.PARAM_TIME, time);
        values.put(Constants.PARAM_EVENTID, mEvent.getId());
        String url = Constants.URL_BASE + IOUtils.getQuery(values);
        Log.d("test", "ulr" + url);

        CustomRequest jsObjRequest = new CustomRequest(Method.GET, url, null,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response
                                    .getJSONObject("Response").getJSONObject(
                                            "Data");
                            if (data.getString("success").equalsIgnoreCase(
                                    "true")) {
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                            Toast.makeText(mContext, data.getString("message"),
                                    Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("test",
                        "error" + error.getLocalizedMessage());
            }
        });

        // Adding request to request queue
        WedSourceApp.getInstance()
                .addToRequestQueue(jsObjRequest, tag_json_obj);

    }


}
