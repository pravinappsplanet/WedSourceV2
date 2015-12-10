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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import in.appsplanet.wedsource.R;
import in.appsplanet.wedsource.WedSourceApp;
import in.appsplanet.wedsource.pojo.Category;
import in.appsplanet.wedsource.pojo.Country;
import in.appsplanet.wedsource.utils.AppSettings;
import in.appsplanet.wedsource.utils.Constants;
import in.appsplanet.wedsource.utils.CustomRequest;
import in.appsplanet.wedsource.utils.IOUtils;

public class AddVendorFragment extends Fragment implements View.OnClickListener {
    private Context mContext;
    private EditText mEdtName, mEdtAddress, mEdtPhone, mEdtEmail, mEdtWebsite;
    private Spinner mSpnCategory, mSpnCountry;
    private Button mBtnSubmit;
    private ArrayList<Category> mlistCategory;
    private ArrayList<Country> mListCountry;
    private AppSettings mAppSettings;
    private ArrayAdapter<Category> mAdapterCategory;
    private ArrayAdapter<Country> mAdapterCountry;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_vendor, null);
        mContext = getActivity();

        mEdtName = (EditText) view.findViewById(R.id.edtName);
        mEdtAddress = (EditText) view.findViewById(R.id.edtAddress);
        mEdtPhone = (EditText) view.findViewById(R.id.edtPhone);
        mEdtEmail = (EditText) view.findViewById(R.id.edtEmail);
        mEdtWebsite = (EditText) view.findViewById(R.id.edtWebsite);
        mSpnCategory = (Spinner) view.findViewById(R.id.spnCategory);
        mSpnCountry = (Spinner) view.findViewById(R.id.spnCountry);
        mBtnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        mBtnSubmit.setOnClickListener(this);

        mAppSettings = new AppSettings(mContext);
        //LOAD CATEGORY
        mlistCategory = new ArrayList<Category>();
        mAdapterCategory = new ArrayAdapter<Category>(mContext,
                android.R.layout.simple_list_item_2, android.R.id.text1, mlistCategory);
        mSpnCategory.setAdapter(mAdapterCategory);

        loadCategory();

        mListCountry = new ArrayList<Country>();
        mAdapterCountry = new ArrayAdapter<Country>(mContext,
                android.R.layout.simple_list_item_2, android.R.id.text1, mListCountry);
        mSpnCountry.setAdapter(mAdapterCountry);

        loadCountry();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // UPDATE HEADER
        ((VendorDirectoryActivity) getActivity()).setHeader("Add Vendor");

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
            }
// else if (TextUtils.isEmpty(address)) {
//                mEdtAddress.setError("Please enter address");
//                return;
//            }
            else if (TextUtils.isEmpty(phone)) {
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
                addVendor(name, address, phone, email, website, mListCountry.get(mSpnCountry.getSelectedItemPosition()).getId(), mlistCategory.get(mSpnCategory.getSelectedItemPosition()).getId());
            }
        }
    }


    /**
     * reset all fields
     */
    private void reset() {
        mEdtName.setText("");
        mEdtAddress.setText("");
        mEdtPhone.setText("");
        mEdtEmail.setText("");
        mEdtWebsite.setText("");
    }

    private void loadCategory() {
        mlistCategory.clear();
        String tag_json_obj = "getCategory";
        ContentValues values = new ContentValues();
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_GETCATEGORY);
        String url = Constants.URL_BASE + IOUtils.getQuery(values);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONObject("Response")
                                    .getJSONArray("Data");
                            Gson gson = new Gson();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject object = data.getJSONObject(i);
                                mlistCategory.add(gson.fromJson(
                                        object.toString(), Category.class));
                            }
                            // SET ADAPTER
                            mAdapterCategory.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        // Adding request to request queue
        WedSourceApp.getInstance()
                .addToRequestQueue(jsObjRequest, tag_json_obj);
    }

    private void loadCountry() {
        String tag_json_obj = "getCountry";
        ContentValues values = new ContentValues();
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_GETCOUNTRY);
        String url = Constants.URL_BASE + IOUtils.getQuery(values);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONObject("Response")
                                    .getJSONArray("Data");
                            Gson gson = new Gson();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject object = data.getJSONObject(i);
                                mListCountry.add(gson.fromJson(
                                        object.toString(), Country.class));
                            }

                            // SET ADAPTER
                            mAdapterCountry.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        // Adding request to request queue
        WedSourceApp.getInstance()
                .addToRequestQueue(jsObjRequest, tag_json_obj);

    }

    /**
     * @param name
     * @param address
     * @param phone
     * @param email
     * @param website
     * @param countryId
     * @param categoryId
     */
    private void addVendor(String name, String address, String phone, String email, String website, int countryId, int categoryId) {
        {
            String tag_json_obj = "addVendor";
            ContentValues values = new ContentValues();
            values.put(Constants.PARAM_COMMAND, Constants.COMMAND_ADDVENDOR);
            values.put(Constants.PARAM_NAME, name);
            values.put(Constants.PARAM_ADDRESS, address);
            values.put(Constants.PARAM_PHONE, phone);
            values.put(Constants.PARAM_EMAIL, email);
            values.put(Constants.PARAM_WEBSITE, website);
            values.put(Constants.PARAM_CATEGORYID, countryId);
            values.put(Constants.PARAM_CATEGORYID, categoryId);
            values.put(Constants.PARAM_USERID, mAppSettings.getUserId());
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
//RESET ALL FIELDS
                                    reset();
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
