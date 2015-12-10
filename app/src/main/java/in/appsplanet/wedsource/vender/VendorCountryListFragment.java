package in.appsplanet.wedsource.vender;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import in.appsplanet.wedsource.pojo.Country;
import in.appsplanet.wedsource.utils.Constants;
import in.appsplanet.wedsource.utils.IOUtils;

public class VendorCountryListFragment extends ListFragment implements
        OnItemClickListener {
    private Context mContext;
    private ListView mListView;
    private ArrayList<Country> mListCountry;
    private ArrayAdapter<Country> mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        // Update header
        ((VendorDirectoryActivity) getActivity())
                .setHeader("Vendor Directory");
        mListCountry = new ArrayList<Country>();

        mAdapter = new ArrayAdapter<Country>(mContext,
                R.layout.item_vender_country, R.id.txtName, mListCountry);
        setListAdapter(mAdapter);
        loadCountry();
        Log.d("test", "fragment onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("test", "fragment onViewCreated");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("test", "fragment onActivityCreated");
        mListView = getListView();
        mListView.setOnItemClickListener(this);

        //ADD VISIBLE
        ((VendorDirectoryActivity) getActivity()).mImgHeaderAdd.setVisibility(View.GONE);
    }

    private void loadCountry() {
        String tag_json_obj = "getCountry";
        ContentValues values = new ContentValues();
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_GETCOUNTRY);
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
                                mListCountry.add(gson.fromJson(
                                        object.toString(), Country.class));
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
        VendorCategoryListFragment feedbackFragment = new VendorCategoryListFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.INTENT_COUNTRY, mListCountry.get(arg2));
        feedbackFragment.setArguments(args);

        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(feedbackFragment.getClass()
                .getName());
        fragmentTransaction.replace(R.id.frag_content, feedbackFragment);
        fragmentTransaction.commit();
    }
}
