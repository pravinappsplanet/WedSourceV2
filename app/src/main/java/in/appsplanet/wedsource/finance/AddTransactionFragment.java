package in.appsplanet.wedsource.finance;

import android.app.DatePickerDialog;
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
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import in.appsplanet.wedsource.R;
import in.appsplanet.wedsource.WedSourceApp;
import in.appsplanet.wedsource.pojo.Event;
import in.appsplanet.wedsource.utils.AppSettings;
import in.appsplanet.wedsource.utils.Constants;
import in.appsplanet.wedsource.utils.CustomRequest;
import in.appsplanet.wedsource.utils.IOUtils;

public class AddTransactionFragment extends Fragment implements OnClickListener,
        Constants {
    private Context mContext;
    private Button mBtnSubmit, mBtnDate;
    private EditText mEdtName, mEdtAmount, mEdtDescription;
    private Event mEvent;
    private AppSettings mAppSettings;
    private RadioButton mRdbIncome, mRdbExpense;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_transaction, null);
        mContext = getActivity();

        mBtnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        mBtnSubmit.setOnClickListener(this);
        mBtnDate = (Button) view.findViewById(R.id.btnDate);
        mBtnDate.setOnClickListener(this);

        mEdtName = (EditText) view.findViewById(R.id.edtName);
        mEdtAmount = (EditText) view.findViewById(R.id.edtAmount);
        mEdtDescription = (EditText) view.findViewById(R.id.edtDescription);
        mRdbIncome = (RadioButton) view.findViewById(R.id.rdbIncome);
        mRdbExpense = (RadioButton) view.findViewById(R.id.rdbExpense);

        mAppSettings = new AppSettings(mContext);
        mEvent = (Event) getArguments().getSerializable(Constants.INTENT_EVENT);


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((FinanceActivity) getActivity()).setHeader("Add Transaction");
        ((FinanceActivity) getActivity()).mImgHeaderAdd.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit: // SUBMIT
                String name = mEdtName.getText().toString().trim();
                String amount = mEdtAmount.getText().toString().trim();
                String description = mEdtDescription.getText().toString().trim();
                String date = mBtnDate.getText().toString().trim();

                //VALIDATIONS
                if (TextUtils.isEmpty(name)) {
                    mEdtName.setError("Please enter name");
                } else if (TextUtils.isEmpty(amount)) {
                    mEdtAmount.setError("Please enter amount");
                }
//                else if (TextUtils.isEmpty(description)) {
//                    mEdtDescription.setError("Please enter details");
//                }
//                else if (mBtnDate.getText().toString().equalsIgnoreCase("Date")) {
//                    Toast.makeText(mContext, "Please select date", Toast.LENGTH_SHORT).show();
//                }
                else {
                    if (mBtnDate.getText().toString().equalsIgnoreCase("Date"))
                        date = "";

                    addTransaction(name, amount, description, date);
                }
                break;

            case R.id.btnDate://DATE
                Calendar newCalendar = Calendar.getInstance();
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

            default:
                break;
        }
    }

    /**
     * reset all fields
     */
    private void reset() {
        mEdtName.setText("");
        mEdtDescription.setText("");
        mEdtAmount.setText("");
        mBtnDate.setText("Date");
    }

    /**
     * @param name
     * @param description
     */
    private void addTransaction(String name, String amount, String description, String date) {
        String tag_json_obj = "addTransaction";
        ContentValues values = new ContentValues();
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_ADDTRANSACTION);
        values.put(Constants.PARAM_NAME, name);
        values.put(Constants.PARAM_AMOUNT, amount);
        values.put(Constants.PARAM_DESCRIPTION, description);
        values.put(Constants.PARAM_DATE, date);
        values.put(Constants.PARAM_TYPE, mRdbIncome.isChecked() ? 1 : 0);
        values.put(Constants.PARAM_USERID, mAppSettings.getUserId());
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
                                reset();
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
                        "loging error" + error.getLocalizedMessage());
            }
        });

        // Adding request to request queue
        WedSourceApp.getInstance()
                .addToRequestQueue(jsObjRequest, tag_json_obj);

    }


}
