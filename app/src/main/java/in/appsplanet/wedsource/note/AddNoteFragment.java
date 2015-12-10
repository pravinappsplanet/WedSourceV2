package in.appsplanet.wedsource.note;

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

import java.util.Calendar;

import in.appsplanet.wedsource.R;
import in.appsplanet.wedsource.WedSourceApp;
import in.appsplanet.wedsource.utils.AppSettings;
import in.appsplanet.wedsource.utils.Constants;
import in.appsplanet.wedsource.utils.CustomRequest;
import in.appsplanet.wedsource.utils.IOUtils;

public class AddNoteFragment extends Fragment implements OnClickListener,
        Constants {
    private Context mContext;
    private Button mBtnSubmit;
    private EditText mEdtName, mEdtDescription;
    private AppSettings mAppSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_note, null);
        mContext = getActivity();

        mBtnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        mBtnSubmit.setOnClickListener(this);
        mEdtName = (EditText) view.findViewById(R.id.edtName);
        mEdtDescription = (EditText) view.findViewById(R.id.edtDescription);
        mAppSettings = new AppSettings(mContext);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // UPDATE HEADER
        ((NotesActivity) getActivity()).setHeader("Add Note");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit: // SUBMIT
                String name = mEdtName.getText().toString().trim();
                String description = mEdtDescription.getText().toString().trim();

                //VALIDATION
                if (TextUtils.isEmpty(name)) {
                    mEdtName.setError("Please enter name");
                } else if (TextUtils.isEmpty(description)) {
                    mEdtDescription.setError("Please enter description");
                } else {
                    addNote(name, description);
                }

                break;

            default:
                break;
        }
    }

    private void reset() {
        mEdtDescription.setText("");
        mEdtName.setText("");
    }

    /**
     * @param name
     * @param description
     */
    private void addNote(String name, String description) {
        String tag_json_obj = "addNote";
        ContentValues values = new ContentValues();
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_ADDNOTES);
        values.put(Constants.PARAM_NAME, name);
        values.put(Constants.PARAM_DESCRIPTION, description);
        values.put(Constants.PARAM_DATE, Calendar.getInstance().getTime().toGMTString());
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
