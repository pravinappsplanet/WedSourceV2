package in.appsplanet.wedsource.vender;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import in.appsplanet.wedsource.R;
import in.appsplanet.wedsource.WedSourceApp;
import in.appsplanet.wedsource.pojo.Vendor;
import in.appsplanet.wedsource.utils.AppSettings;
import in.appsplanet.wedsource.utils.Constants;
import in.appsplanet.wedsource.utils.CustomRequest;
import in.appsplanet.wedsource.utils.IOUtils;

public class EditVendorFragment extends Fragment implements View.OnClickListener {
    private Context mContext;
    private EditText mEdtName, mEdtAddress, mEdtPhone, mEdtEmail, mEdtWebsite;
    private Button mBtnSubmit;
    private AppSettings mAppSettings;
    private Vendor mVendor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_vendor, null);
        mContext = getActivity();

        mEdtName = (EditText) view.findViewById(R.id.edtName);
        mEdtAddress = (EditText) view.findViewById(R.id.edtAddress);
        mEdtPhone = (EditText) view.findViewById(R.id.edtPhone);
        mEdtEmail = (EditText) view.findViewById(R.id.edtEmail);
        mEdtWebsite = (EditText) view.findViewById(R.id.edtWebsite);
        mBtnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        mBtnSubmit.setOnClickListener(this);
        mAppSettings = new AppSettings(mContext);
        mVendor = (Vendor) getArguments().get(Constants.INTENT_VENDOR);
        loadVendor();
        return view;
    }

    private void loadVendor() {
        mEdtName.setText(mVendor.getName());
        mEdtAddress.setText(mVendor.getAddress());
        mEdtPhone.setText(mVendor.getPhone());
        mEdtEmail.setText(mVendor.getEmail());
        mEdtWebsite.setText(mVendor.getWebsite());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // UPDATE HEADER
        ((VendorDirectoryActivity) getActivity()).setHeader("Edit Vendor");

        //ADD VISIBLE
        ((VendorDirectoryActivity) getActivity()).mImgHeaderAdd.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnSubmit) {//SUBMIT
            String name = mEdtName.getText().toString().trim();
            String address = mEdtAddress.getText().toString().trim();
            String phone = mEdtPhone.getText().toString().trim();
            String email = mEdtEmail.getText().toString().trim();
            String website = mEdtWebsite.getText().toString().trim();

            //VALIDATE
            if (TextUtils.isEmpty(name)) {
                mEdtName.setError("Please enter name");
                return;
            } else if (TextUtils.isEmpty(address)) {
                mEdtAddress.setError("Please enter address");
                return;
            } else if (TextUtils.isEmpty(phone)) {
                mEdtPhone.setError("Please enter phone");
                return;
            }
//            else if (TextUtils.isEmpty(email)) {
//                mEdtEmail.setError("Please enter email");
//                return;
//            } else if (TextUtils.isEmpty(website)) {
//                mEdtWebsite.setError("Please enter website");
//                return;
//            }
            else {
                addVendor(name, address, phone, email, website);
            }
        }
    }


    /**
     * @param name
     * @param address
     * @param phone
     * @param email
     * @param website
     */
    private void addVendor(String name, String address, String phone, String email, String website) {
        {
            String tag_json_obj = "addVendor";
            ContentValues values = new ContentValues();
            values.put(Constants.PARAM_COMMAND, Constants.COMMAND_EDITVENDOR);
            values.put(Constants.PARAM_NAME, name);
            values.put(Constants.PARAM_ADDRESS, address);
            values.put(Constants.PARAM_PHONE, phone);
            values.put(Constants.PARAM_EMAIL, email);
            values.put(Constants.PARAM_WEBSITE, website);
            values.put(Constants.PARAM_VENDORID, mVendor.getId());
            String url = Constants.URL_BASE + IOUtils.getQuery(values);
            Log.d("test", "loging ulr" + url);

            CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
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

                    }, new Response.ErrorListener() {
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
}
