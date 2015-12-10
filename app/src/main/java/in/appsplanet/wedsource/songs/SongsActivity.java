package in.appsplanet.wedsource.songs;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
import in.appsplanet.wedsource.utils.AppSettings;
import in.appsplanet.wedsource.utils.Constants;
import in.appsplanet.wedsource.utils.CustomRequest;
import in.appsplanet.wedsource.utils.IOUtils;

public class SongsActivity extends AppCompatActivity implements OnClickListener {

    private Context mContext;
    private TextView mTxtHeader;
    private ImageView mImgHeaderBack, mImgHeaderAdd;
    private AppSettings mAppSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);

        mContext = this;
        init();

    }

    private void init() {
        mAppSettings = new AppSettings(mContext);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();

        PlayListFragment fragment = new PlayListFragment();
        fragmentTransaction.replace(R.id.frag_content, fragment);
        fragmentTransaction.commit();

        mTxtHeader = (TextView) findViewById(R.id.txtHeader);
        mTxtHeader.setText("Song Lists");

        mImgHeaderBack = (ImageView) findViewById(R.id.imgHeaderBack);
        mImgHeaderBack.setOnClickListener(this);

        mImgHeaderAdd = (ImageView) findViewById(R.id.imgHeaderAdd);
        mImgHeaderAdd.setVisibility(View.VISIBLE);
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

            case R.id.imgHeaderAdd: // HEADER ADD
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.frag_content);
                if (f instanceof PlayListFragment) {
                    showDialogAddNew(true);
                } else {
                    showDialogAddNew(false);
                }
                break;
            default:
                break;
        }
    }

    /**
     * @param isPlaylist
     */
    private void showDialogAddNew(final boolean isPlaylist) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle(isPlaylist ? "Add new list" : "Add new song");
        dialog.setContentView(R.layout.dialog_add_new);
        dialog.show();

        Button btnSubmit = (Button) dialog.findViewById(R.id.btnSubmit);
        final EditText edtName = (EditText) dialog.findViewById(R.id.edtName);
        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    edtName.setError("Please insert name");
                } else {
                    addNew(isPlaylist, name);
                    dialog.dismiss();
                }
            }
        });
    }

    /**
     *
     * @param isPlaylist
     * @param name
     */
    private void addNew(final boolean isPlaylist, String name) {
        String tag_json_obj = "getSongs";
       final Fragment fragment= getSupportFragmentManager().findFragmentById(R.id.frag_content);
        ContentValues values = new ContentValues();
        if (isPlaylist)//PLAYLIST
            values.put(Constants.PARAM_COMMAND, Constants.COMMAND_ADDPLAYLIST);
        else {//SONGS LIST
            values.put(Constants.PARAM_COMMAND, Constants.COMMAND_ADDSONGS);
            values.put(Constants.PARAM_PLAYLISTID, ((SongsListFragment) fragment).mPlaylist.getId());
        }
        values.put(Constants.PARAM_USERID, mAppSettings.getUserId());
        values.put(Constants.PARAM_NAME, name);
        String url = Constants.URL_BASE + IOUtils.getQuery(values);
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
                                //REFRESH DATA
                                if (isPlaylist)
                                    ((PlayListFragment)fragment).loadPlaylist();
                                else
                                    ((SongsListFragment)fragment).loadSongs();
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

            }
        });

        // Adding request to request queue
        WedSourceApp.getInstance()
                .addToRequestQueue(jsObjRequest, tag_json_obj);

    }
}
