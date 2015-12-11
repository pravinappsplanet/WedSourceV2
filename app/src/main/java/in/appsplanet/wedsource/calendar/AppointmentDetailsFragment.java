package in.appsplanet.wedsource.calendar;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import in.appsplanet.wedsource.R;
import in.appsplanet.wedsource.WedSourceApp;
import in.appsplanet.wedsource.pojo.Event;
import in.appsplanet.wedsource.utils.AppSettings;
import in.appsplanet.wedsource.utils.Constants;
import in.appsplanet.wedsource.utils.CustomRequest;
import in.appsplanet.wedsource.utils.IOUtils;

public class AppointmentDetailsFragment extends Fragment implements OnClickListener {
    private Context mContext;
    private TextView mTxtName, mTxtAddress, mTxtEmail, mTxtPhone, mTxtWebSite;
    private ImageView mImgShare, mImgEdit, mImgDelete;
    private Event mEvent;
    private AppSettings mAppSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment_details, null);
        mContext = getActivity();

        mEvent = (Event) getArguments().getSerializable(
                Constants.INTENT_EVENT);
        mTxtName = (TextView) view.findViewById(R.id.txtName);
        mTxtAddress = (TextView) view.findViewById(R.id.txtAddress);
        mTxtEmail = (TextView) view.findViewById(R.id.txtEmail);
        mTxtPhone = (TextView) view.findViewById(R.id.txtPhone);
        mTxtWebSite = (TextView) view.findViewById(R.id.txtWebsite);

        mImgShare = (ImageView) view.findViewById(R.id.imgShare);
        mImgShare.setOnClickListener(this);
        mImgEdit = (ImageView) view.findViewById(R.id.imgEdit);
        mImgEdit.setOnClickListener(this);
        mImgDelete = (ImageView) view.findViewById(R.id.imgDelete);
        mImgDelete.setOnClickListener(this);


        mAppSettings = new AppSettings(mContext);
        loadDetails();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // UPDATE HEADER
        ((CalendarActivity) getActivity())
                .setHeader("Event Details");

    }

    private void loadDetails() {
        mTxtName.setText(mEvent.getName());
        if (TextUtils.isEmpty(mEvent.getVenue()))
            mTxtAddress.setVisibility(View.GONE);
        else
            mTxtAddress.setText("Address: " + mEvent.getVenue());

        if (TextUtils.isEmpty(mEvent.getTime()))
            mTxtEmail.setVisibility(View.GONE);
        else
            mTxtEmail.setText("Time: " + mEvent.getTime());

        if (TextUtils.isEmpty(mEvent.getDate()))
            mTxtPhone.setVisibility(View.GONE);
        else
            mTxtPhone.setText("Date: " + mEvent.getDate());

        if (TextUtils.isEmpty(mEvent.getDescription()))
            mTxtWebSite.setVisibility(View.GONE);
        else
            mTxtWebSite.setText("Description: " + mEvent.getDescription());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.imgShare://SHARE
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, mEvent.getName());
                shareIntent.putExtra(Intent.EXTRA_TEXT, mEvent.getName() + " " + mEvent.getVenue() + " " + mEvent.getTime() + " " + mEvent.getDate() + " " + mEvent.getDescription());
                mContext.startActivity(Intent.createChooser(shareIntent, "Share"));
                break;

            case R.id.imgDelete://DELETE
                showDialogDelete(mEvent.getId());
                break;

            case R.id.imgEdit://EDIT

                break;

            default:
                break;
        }
    }


    /**
     * @param id
     */
    private void showDialogDelete(final int id) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete event?");
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
        String tag_json_obj = "deleteMyVendor";
        ContentValues values = new ContentValues();
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_DELETEAPPOINTMENT);
        values.put(Constants.PARAM_APPOINTMENTID, id);
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
