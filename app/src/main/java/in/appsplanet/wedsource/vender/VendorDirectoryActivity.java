package in.appsplanet.wedsource.vender;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import in.appsplanet.wedsource.HomeActivity;
import in.appsplanet.wedsource.R;
import in.appsplanet.wedsource.SettingsActivity;
import in.appsplanet.wedsource.WedSourceApp;
import in.appsplanet.wedsource.pojo.Vendor;
import in.appsplanet.wedsource.utils.AppSettings;
import in.appsplanet.wedsource.utils.Constants;
import in.appsplanet.wedsource.utils.CustomRequest;
import in.appsplanet.wedsource.utils.IOUtils;

public class VendorDirectoryActivity extends AppCompatActivity implements
        OnClickListener {

    private Context mContext;
    private TextView mTxtHeader;
    public ImageView mImgHeaderBack, mImgHeaderAdd;
    public boolean isMyVendor;
    private AppSettings mAppSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_directory);

        mContext = this;
        init();

    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTxtHeader = (TextView) findViewById(R.id.txtHeader);

        mAppSettings = new AppSettings(mContext);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();

        if (getIntent().getBooleanExtra(Constants.INTENT_IS_MY_VENDOR, false)) {
            MyVendorNameListFragment fragment = new MyVendorNameListFragment();
            fragmentTransaction.replace(R.id.frag_content, fragment);
            fragmentTransaction.commit();
            mTxtHeader.setText("My Vendor Directory");
            isMyVendor = true;
        } else {
            VendorCountryListFragment fragment = new VendorCountryListFragment();
            fragmentTransaction.replace(R.id.frag_content, fragment);
            fragmentTransaction.commit();
            mTxtHeader.setText("Vendor Directory");
            isMyVendor = false;
        }

        mImgHeaderBack = (ImageView) findViewById(R.id.imgHeaderBack);
        mImgHeaderBack.setOnClickListener(this);

        mImgHeaderAdd = (ImageView) findViewById(R.id.imgHeaderAdd);
        mImgHeaderAdd.setOnClickListener(this);

    }

    /**
     * @param header
     */
    void setHeader(String header) {
        mTxtHeader.setText(header);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.imgToolbarHome:// TOOLBAR HOME
                startActivity(new Intent(mContext, HomeActivity.class));
                finish();
                break;

            case R.id.imgToolbarSettings:// TOOLBAR SETTINGS
                startActivity(new Intent(mContext, SettingsActivity.class));
                break;

            case R.id.imgHeaderBack:// HEADER BACK
                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                } else {
                    finish();
                }
                break;
            case R.id.imgHeaderAdd://ADD
                final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frag_content);
                if (fragment instanceof MyVendorNameListFragment) {//MY VENDOR
                    AddVendorFragment feedbackFragment = new AddVendorFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.addToBackStack(feedbackFragment.getClass()
                            .getName());
                    fragmentTransaction.replace(R.id.frag_content, feedbackFragment);
                    fragmentTransaction.commit();

                } else if (fragment instanceof VendorDetailsFragment) {//DETAILS
                    if (isMyVendor) {
                        AddVendorFragment feedbackFragment = new AddVendorFragment();
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.addToBackStack(feedbackFragment.getClass()
                                .getName());
                        fragmentTransaction.replace(R.id.frag_content, feedbackFragment);
                        fragmentTransaction.commit();

                    } else {
                        final Vendor vendor = ((VendorDetailsFragment) fragment).mVendor;
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Add to my vendor");
                        builder.setMessage("Are you sure you would like to Add Vendor");
                        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                addToMyVendor(vendor.getId() + "");
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
                }

                break;

            default:
                break;
        }
    }

    /**
     * add to my vendor
     *
     * @param vendorId
     */
    private void addToMyVendor(String vendorId) {
        String tag_json_obj = "addTOMyVendor";
        ContentValues values = new ContentValues();
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_MAKEVENDOR);
        values.put(Constants.PARAM_VENDORID, vendorId);
        values.put(Constants.PARAM_USERID, mAppSettings.getUserId());
        String url = Constants.URL_BASE + IOUtils.getQuery(values);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response
                                    .getJSONObject("Response").getJSONObject(
                                            "Data");
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
