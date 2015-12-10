package in.appsplanet.wedsource.vender;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import in.appsplanet.wedsource.R;
import in.appsplanet.wedsource.WedSourceApp;
import in.appsplanet.wedsource.pojo.Vendor;
import in.appsplanet.wedsource.utils.AppSettings;
import in.appsplanet.wedsource.utils.Constants;
import in.appsplanet.wedsource.utils.IOUtils;

public class MyVendorNameListFragment extends ListFragment implements
        OnItemClickListener {
    private Context mContext;
    private ListView mListView;
    private ArrayList<Vendor> mlistVendor;
    private AppSettings mAppSettings;
    private ArrayAdapter<Vendor> mAdapter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
        mlistVendor = new ArrayList<Vendor>();
        mAppSettings = new AppSettings(mContext);
        mAdapter = new ArrayAdapter<Vendor>(mContext,
                R.layout.item_vender_country, R.id.txtName, mlistVendor);
        setListAdapter(mAdapter);
        loadVendorList();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = getListView();
        mListView.setOnItemClickListener(this);

        // UPDATE HEADER
        ((VendorDirectoryActivity) getActivity()).setHeader("My Vendors");

        //ADD VISIBLE
        ((VendorDirectoryActivity) getActivity()).mImgHeaderAdd.setVisibility(View.VISIBLE);

    }

    private void loadVendorList() {
        String tag_json_obj = "getCountry";
        ContentValues values = new ContentValues();
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_GETVENDOR);
        values.put(Constants.PARAM_USERID, mAppSettings.getUserId());
        String url = Constants.URL_BASE + IOUtils.getQuery(values);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Method.GET, url,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONObject("Response")
                                    .getJSONArray("Data");
                            Gson gson = new Gson();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject object = data.getJSONObject(i);
                                mlistVendor.add(gson.fromJson(
                                        object.toString(), Vendor.class));
                            }

                            // SET ADAPTER
                            mAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        // Adding request to request queue
        WedSourceApp.getInstance()
                .addToRequestQueue(jsObjRequest, tag_json_obj);

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        VendorDetailsFragment feedbackFragment = new VendorDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.INTENT_VENDOR, mlistVendor.get(arg2));
        feedbackFragment.setArguments(args);

        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(feedbackFragment.getClass()
                .getName());
        fragmentTransaction.replace(R.id.frag_content, feedbackFragment);
        fragmentTransaction.commit();
    }
}
