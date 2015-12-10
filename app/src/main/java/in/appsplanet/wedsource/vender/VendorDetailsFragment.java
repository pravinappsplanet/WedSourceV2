package in.appsplanet.wedsource.vender;

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
import android.widget.TextView;
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

public class VendorDetailsFragment extends Fragment implements OnClickListener {
    private Context mContext;
    private TextView mTxtName, mTxtAddress, mTxtEmail, mTxtPhone, mTxtWebSite;
    public Vendor mVendor;
    private ImageView mImgShare, mImgEdit, mImgDelete;
    private AppSettings mAppSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vendor_details, null);
        mContext = getActivity();

        mVendor = (Vendor) getArguments().getSerializable(
                Constants.INTENT_VENDOR);
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
        if (mVendor.getCategory() == null)
            ((VendorDirectoryActivity) getActivity())
                    .setHeader("Vendors Directory");
        else
            ((VendorDirectoryActivity) getActivity())
                    .setHeader(mVendor.getCategory().getName());

        //ADD VISIBLE
        ((VendorDirectoryActivity) getActivity()).mImgHeaderAdd.setVisibility(View.VISIBLE);

        //EDIT AND DELETE ONLY FOR MY VENDOR
        if (((VendorDirectoryActivity) getActivity()).isMyVendor) {
            mImgDelete.setVisibility(View.VISIBLE);
            mImgEdit.setVisibility(View.VISIBLE);
        } else {
            mImgDelete.setVisibility(View.GONE);
            mImgEdit.setVisibility(View.GONE);
        }

    }

    private void loadDetails() {
        mTxtName.setText(mVendor.getName());
        if (TextUtils.isEmpty(mVendor.getAddress()))
            mTxtAddress.setVisibility(View.GONE);
        else
            mTxtAddress.setText("Address: " + mVendor.getAddress());

        if (TextUtils.isEmpty(mVendor.getEmail()))
            mTxtEmail.setVisibility(View.GONE);
        else
            mTxtEmail.setText("Email: " + mVendor.getEmail());

        if (TextUtils.isEmpty(mVendor.getPhone()))
            mTxtPhone.setVisibility(View.GONE);
        else
            mTxtPhone.setText("Phone: " + mVendor.getPhone());

        if (TextUtils.isEmpty(mVendor.getWebsite()))
            mTxtWebSite.setVisibility(View.GONE);
        else
            mTxtWebSite.setText("Website: " + mVendor.getWebsite());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.imgShare://SHARE
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mVendor.getName());
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, mVendor.getName() + " " + mVendor.getAddress() + " " + mVendor.getPhone() + " " + mVendor.getEmail() + " " + mVendor.getWebsite());
                mContext.startActivity(Intent.createChooser(shareIntent, "Share"));
                break;

            case R.id.imgDelete://DELETE
                showDialogDelete(mVendor.getId());
                break;

            case R.id.imgEdit://EDIT
                getActivity().getSupportFragmentManager().popBackStack();
                EditVendorFragment feedbackFragment = new EditVendorFragment();
                Bundle args = new Bundle();
                args.putSerializable(Constants.INTENT_VENDOR, mVendor);
                feedbackFragment.setArguments(args);
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack(feedbackFragment.getClass()
                        .getName());
                fragmentTransaction.replace(R.id.frag_content, feedbackFragment);
                fragmentTransaction.commit();
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
        builder.setMessage("Are you sure you want to delete vendor?");
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
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_DELETEMYVENDOR);
        values.put(Constants.PARAM_VENDORID, id);
        values.put(Constants.PARAM_USERID, mAppSettings.getUserId());
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
