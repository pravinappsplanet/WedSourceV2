package in.appsplanet.wedsource.finance;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import in.appsplanet.wedsource.R;
import in.appsplanet.wedsource.WedSourceApp;
import in.appsplanet.wedsource.pojo.Event;
import in.appsplanet.wedsource.utils.AppSettings;
import in.appsplanet.wedsource.utils.Constants;
import in.appsplanet.wedsource.utils.CustomRequest;
import in.appsplanet.wedsource.utils.IOUtils;

public class FinanceEventListFragment extends ListFragment implements
        OnItemClickListener, AdapterView.OnItemLongClickListener {
    private Context mContext;
    private ListView mListView;
    private ArrayList<Event> mListEvent;
    private AppSettings mAppSettings;
    private ArrayAdapter<Event> mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();

        mListEvent = new ArrayList<Event>();
        mAdapter = new ArrayAdapter<Event>(mContext,
                R.layout.item_vender_country, R.id.txtName, mListEvent);
        setListAdapter(mAdapter);

        mAppSettings = new AppSettings(mContext);


        loadEvent();
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putSerializable(Constants.INTENT_EVENT, mListEvent);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = getListView();
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        setRetainInstance(true);
        ((FinanceActivity) getActivity()).setHeader("Finances");
        ((FinanceActivity) getActivity()).mImgHeaderAdd.setVisibility(View.GONE);
    }

    private void loadEvent() {
        mListEvent.clear();
        String tag_json_obj = "getCountry";
        ContentValues values = new ContentValues();
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_GETEVENT);
        values.put(Constants.PARAM_USERID, mAppSettings.getUserId());
        String url = Constants.URL_BASE + IOUtils.getQuery(values);

        CustomRequest jsObjRequest = new CustomRequest(Method.GET, url, null,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONObject("Response")
                                    .getJSONArray("Data");
                            Gson gson = new Gson();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject object = data.getJSONObject(i);
                                mListEvent.add(gson.fromJson(object.toString(),
                                        Event.class));
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
        FinanceDetailsFragment eventDetailsFragment = new
                FinanceDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.INTENT_EVENT,
                mListEvent.get(arg2));
        eventDetailsFragment.setArguments(args);

        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(eventDetailsFragment.getClass()
                .getName());
        fragmentTransaction.replace(R.id.frag_content, eventDetailsFragment);
        fragmentTransaction.commit();
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        showDialogDelete(mListEvent.get(position).getId());
        return true;
    }

    private void showDialogDelete(final int id) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete finance?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                delete(id + "");
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * @param id
     */
    private void delete(String id) {
        String tag_json_obj = "deleteNote";
        ContentValues values = new ContentValues();
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_DELETEEVENT);
        values.put(Constants.PARAM_EVENTID, id);
        String url = Constants.URL_BASE + IOUtils.getQuery(values);
        Log.d("test", "login ulr" + url);

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
                                loadEvent();
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
