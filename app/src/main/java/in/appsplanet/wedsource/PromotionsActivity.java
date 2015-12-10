package in.appsplanet.wedsource;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import in.appsplanet.wedsource.adapter.PromotionAdapter;
import in.appsplanet.wedsource.pojo.Promotion;
import in.appsplanet.wedsource.utils.Constants;
import in.appsplanet.wedsource.utils.CustomRequest;
import in.appsplanet.wedsource.utils.IOUtils;

public class PromotionsActivity extends AppCompatActivity implements
        View.OnClickListener {

    private Context mContext;
    private ListView mListView;
    private TextView mTxtHeader;
    private ArrayList<Promotion> mListPromotion;
    private PromotionAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotins);

        mContext = this;
        init();

        loadPromotionalText();

    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mListView = (ListView) findViewById(R.id.listViewPromotions);
        mTxtHeader = (TextView) findViewById(R.id.txtHeader);
        mTxtHeader.setText("Promotions");
        mListPromotion = new ArrayList<Promotion>();

        mAdapter = new PromotionAdapter(mContext, mListPromotion);
        mListView.setAdapter(mAdapter);
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
                finish();
                break;
            default:
                break;
        }
    }

    private void loadPromotionalText() {
        String tag_json_obj = "loadPromotionalText";
        ContentValues values = new ContentValues();
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_GETPROMOTION);
        String url = Constants.URL_BASE + IOUtils.getQuery(values);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("test", "Promotion" + response.toString());
                            JSONArray data = response.getJSONObject("Response")
                                    .getJSONArray("Data");
                            Gson gson = new Gson();
                            for (int i = 0; i < data.length(); i++) {

                                Promotion promotion = gson.fromJson(data.getJSONObject(i).toString(), Promotion.class);
                                mListPromotion.add(promotion);
                            }

                            Log.d("test", "size promotion:" + mListPromotion.size());
                            mAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("test", "Promotion error" + error.getLocalizedMessage());
            }
        });

        // Adding request to request queue
        WedSourceApp.getInstance()
                .addToRequestQueue(jsObjRequest, tag_json_obj);
    }
}
