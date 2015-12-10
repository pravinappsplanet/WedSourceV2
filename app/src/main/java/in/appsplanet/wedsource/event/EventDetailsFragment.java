package in.appsplanet.wedsource.event;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import in.appsplanet.wedsource.R;
import in.appsplanet.wedsource.WedSourceApp;
import in.appsplanet.wedsource.calendar.CalendarActivity;
import in.appsplanet.wedsource.pojo.Event;
import in.appsplanet.wedsource.pojo.Guest;
import in.appsplanet.wedsource.utils.Constants;
import in.appsplanet.wedsource.utils.CustomRequest;
import in.appsplanet.wedsource.utils.IOUtils;

public class EventDetailsFragment extends Fragment implements OnClickListener {
    private Context mContext;
    private TextView mTxtName, mTxtVenue, mTxtTime, mTxtDate;
    public Event mEvent;
    private ImageView mImgShare, mImgEdit;
    private ArrayList<Guest> mListGuest;
    private LinearLayout mLyGuestList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, null);
        mContext = getActivity();

        mEvent = (Event) getArguments().getSerializable(Constants.INTENT_EVENT);
        mTxtName = (TextView) view.findViewById(R.id.txtName);
        mTxtDate = (TextView) view.findViewById(R.id.txtDate);
        mTxtVenue = (TextView) view.findViewById(R.id.txtVenue);
        mTxtTime = (TextView) view.findViewById(R.id.txtTime);
        mLyGuestList = (LinearLayout) view.findViewById(R.id.lyGuest);
        mImgShare = (ImageView) view.findViewById(R.id.imgShare);
        mImgShare.setOnClickListener(this);
        mImgEdit = (ImageView) view.findViewById(R.id.imgEdit);
        mImgEdit.setOnClickListener(this);
        loadDetails();
        loadGuest();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);

        if (getActivity() instanceof EventActivity) {
            ((EventActivity) getActivity()).mImgHeaderAdd.setVisibility(View.VISIBLE);
            ((EventActivity) getActivity()).setHeader(mEvent.getName());
        }

        if (getActivity() instanceof CalendarActivity) {
            ((CalendarActivity) getActivity()).mImgHeaderAdd.setVisibility(View.VISIBLE);
            ((CalendarActivity) getActivity()).setHeader(mEvent.getName());
        }


    }

    private void loadDetails() {
        mTxtName.setText(mEvent.getName());
        mTxtDate.setText(mEvent.getDate());
        mTxtTime.setText(mEvent.getTime());
        mTxtVenue.setText(mEvent.getVenue());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgEdit://EDIT
                getActivity().getSupportFragmentManager().popBackStack();
                EditEventFragment eventDetailsFragment = new
                        EditEventFragment();
                Bundle args = new Bundle();
                args.putSerializable(Constants.INTENT_EVENT, mEvent);
                eventDetailsFragment.setArguments(args);
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack(eventDetailsFragment.getClass()
                        .getName());
                fragmentTransaction.replace(R.id.frag_content, eventDetailsFragment);
                fragmentTransaction.commit();
                break;
            case R.id.imgShare://SHARE
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mEvent.getName());
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, mEvent.getPDFData() + TextUtils.join(",", mListGuest));
                mContext.startActivity(Intent.createChooser(shareIntent, "Share"));
            default:
                break;
        }
    }

    private void loadGuest() {
        mListGuest = new ArrayList<Guest>();
        mLyGuestList.removeAllViews();
        String tag_json_obj = "getGuest";
        ContentValues values = new ContentValues();
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_GETGUEST);
        values.put(Constants.PARAM_EVENTID, mEvent.getId());
        String url = Constants.URL_BASE + IOUtils.getQuery(values);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONObject("Response")
                                    .getJSONArray("Data");
                            Gson gson = new Gson();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject object = data.getJSONObject(i);
                                mListGuest.add(gson.fromJson(object.toString(),
                                        Guest.class));
                            }
                            setGuestData(mListGuest);
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
     * @param guests
     */
    private void setGuestData(ArrayList<Guest> guests) {
        try {
            mLyGuestList.addView(getGuestItem("Name", "Number of guests"));

            int total = 0;
            for (final Guest guest : guests) {
                View view = getGuestItem(guest.getName(), guest.getNoOfPersons());
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showDialogDelete(guest.getId());
                        return true;
                    }
                });

                mLyGuestList.addView(view);
                total = total + Integer.parseInt(guest.getNoOfPersons());
            }
            mLyGuestList.addView(getGuestItem("Total number of guests", "" + total));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param name
     * @param count
     * @return
     */
    private View getGuestItem(String name, String count) {
        View view = getLayoutInflater(getArguments()).inflate(R.layout.item_guest_list, null);
        TextView txtName = (TextView) view.findViewById(R.id.txtName);
        TextView txtCount = (TextView) view.findViewById(R.id.txtNoOfPersons);
        txtName.setText(name);
        txtCount.setText(count);
        return view;
    }


    private void showDialogDelete(final int id) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete guest?");
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
        String tag_json_obj = "deleteGuest";
        ContentValues values = new ContentValues();
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_DELETEGUEST);
        values.put(Constants.PARAM_GUESTID, id);
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
                                loadGuest();
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
