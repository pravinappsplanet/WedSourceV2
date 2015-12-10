package in.appsplanet.wedsource.event;

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
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import in.appsplanet.wedsource.R;
import in.appsplanet.wedsource.WedSourceApp;
import in.appsplanet.wedsource.calendar.CalendarActivity;
import in.appsplanet.wedsource.pojo.Event;
import in.appsplanet.wedsource.utils.AppSettings;
import in.appsplanet.wedsource.utils.Constants;
import in.appsplanet.wedsource.utils.CustomRequest;
import in.appsplanet.wedsource.utils.IOUtils;

public class AddGuestFragment extends Fragment implements OnClickListener,
        Constants {
    private Context mContext;
    private Button mBtnSubmit;
    private EditText mEdtName, mEdtAddress, mEdtPhone, mEdtEmail, mEdtNoOfPerson;
    private AppSettings mAppSettings;
    private Event mEvent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_guest, null);
        mContext = getActivity();

        mBtnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        mBtnSubmit.setOnClickListener(this);
        mEdtName = (EditText) view.findViewById(R.id.edtName);
        mEdtAddress = (EditText) view.findViewById(R.id.edtAddress);
        mEdtPhone = (EditText) view.findViewById(R.id.edtPhone);
        mEdtEmail = (EditText) view.findViewById(R.id.edtEmail);
        mEdtNoOfPerson = (EditText) view.findViewById(R.id.edtNoOfPersons);

        mEvent = (Event) getArguments().getSerializable(Constants.INTENT_EVENT);
        mAppSettings = new AppSettings(mContext);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //ADD NEW GUEST
        // UPDATE HEADER
        if (getActivity() instanceof EventActivity) {
            ((EventActivity) getActivity()).setHeader("Add new guest");
            ((EventActivity) getActivity()).mImgHeaderAdd.setVisibility(View.GONE);
        }
        if (getActivity() instanceof CalendarActivity) {
            ((CalendarActivity) getActivity()).setHeader("Add new guest");
            ((CalendarActivity) getActivity()).mImgHeaderAdd.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit: // SUBMIT
                String name = mEdtName.getText().toString().trim();
                String address = mEdtAddress.getText().toString().trim();
                String email = mEdtEmail.getText().toString().trim();
                String phone = mEdtPhone.getText().toString().trim();
                String noOfPerson = mEdtNoOfPerson.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    mEdtName.setError("Please enter name");
                }
//                else if (TextUtils.isEmpty(address)) {
//                    mEdtAddress.setError("Please enter address");
//                } else if (TextUtils.isEmpty(email)) {
//                    mEdtEmail.setError("Please enter email");
//                } else if (TextUtils.isEmpty(phone)) {
//                    mEdtPhone.setError("Please enter phone");
//                }
                else if (TextUtils.isEmpty(noOfPerson)) {
                    mEdtNoOfPerson.setError("Please enter no of persons");
                } else {
                    addGuest(name, address, email, phone, noOfPerson);
                }


                break;

            default:
                break;
        }

    }

    private void reset() {
        mEdtName.setText("");
        mEdtAddress.setText("");
        mEdtEmail.setText("");
        mEdtPhone.setText("");
        mEdtNoOfPerson.setText("");
    }

    /**
     * @param name
     * @param address
     * @param email
     * @param phone
     * @param noOfPerson
     */
    private void addGuest(String name, String address, String email, String phone, String noOfPerson) {
        String tag_json_obj = "addGuest";
        ContentValues values = new ContentValues();
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_ADDGUEST);
        values.put(Constants.PARAM_NAME, name);
        values.put(Constants.PARAM_ADDRESS, address);
        values.put(Constants.PARAM_EMAIL, email);
        values.put(Constants.PARAM_PHONE, phone);
        values.put(Constants.PARAM_NOOFPERSONS, noOfPerson);
        values.put(Constants.PARAM_EVENTID, mEvent.getId());

        values.put(Constants.PARAM_USERID, mAppSettings.getUserId());
        String url = Constants.URL_BASE + IOUtils.getQuery(values);
        Log.d("test", "loging ulr" + url);

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
