package in.appsplanet.wedsource;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cete.dynamicpdf.Document;
import com.cete.dynamicpdf.Font;
import com.cete.dynamicpdf.Page;
import com.cete.dynamicpdf.PageOrientation;
import com.cete.dynamicpdf.PageSize;
import com.cete.dynamicpdf.TextAlign;
import com.cete.dynamicpdf.pageelements.Label;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import in.appsplanet.wedsource.pojo.Event;
import in.appsplanet.wedsource.pojo.Finance;
import in.appsplanet.wedsource.pojo.Notes;
import in.appsplanet.wedsource.pojo.Vendor;
import in.appsplanet.wedsource.utils.AppSettings;
import in.appsplanet.wedsource.utils.Constants;
import in.appsplanet.wedsource.utils.CustomRequest;
import in.appsplanet.wedsource.utils.IOUtils;

public class SummaryActivity extends AppCompatActivity implements
        View.OnClickListener {

    private Context mContext;
    private TextView mTxtHeader;
    private String FILE = Environment.getExternalStorageDirectory()
            + "/Summary.pdf";
    private AppSettings mAppSettings;
    final ArrayList<String> vendors = new ArrayList<String>();
    final ArrayList<String> events = new ArrayList<String>();
    final ArrayList<String> finance = new ArrayList<String>();
    final ArrayList<String> noteList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        mContext = this;
        init();

    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTxtHeader = (TextView) findViewById(R.id.txtHeader);
        mTxtHeader.setText("Summary");
        mAppSettings = new AppSettings(mContext);

        File file = new File(FILE);
        boolean deleted = file.delete();


    }

    private void CreatedPDF(ArrayList<String> data) {
// Create a document and set it's properties
        Document objDocument = new Document();
        objDocument.setCreator(getString(R.string.app_name));
        objDocument.setAuthor(getString(R.string.app_name));
        objDocument.setTitle(getString(R.string.app_name));
        // Create a page to add to the document
        Page objPage = new Page(PageSize.LETTER, PageOrientation.PORTRAIT,
                54.0f);
        int space = 150;
        int temp = 0;
        for (int i = 0; i < data.size(); i++) {

            if (i % 4 == 0) {
                // Add page to document
                objPage = new Page(PageSize.LETTER, PageOrientation.PORTRAIT,
                        54.0f);
                objDocument.getPages().add(objPage);
                temp = 0;
            } else {
                temp++;
            }

            String label = data.get(i);
            Log.d("test", "length" + label.length());
//            if (label.length() > 200) {
//                space = 300;
//            } else {
//                space = 120;
//            }

            Log.d("test","label"+label+" space"+space+" length"+label.length());

            Label objLabel = new Label(label, 0, temp * space, 550, 300,
                    Font.getHelvetica(), 14, TextAlign.LEFT);
            // Add label to page
            objPage.getElements().add(objLabel);

        }

        try {
            // Outputs the document to file
            objDocument.draw(FILE);
//            Toast.makeText(this, "File has been written to :" + FILE,
//                    Toast.LENGTH_LONG).show();

            Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:")); // it's not ACTION_SEND
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Summary ");
            intent.putExtra(Intent.EXTRA_TEXT, "Summary");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(FILE)));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this,
                    "Error, unable to write to file\n" + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.imgHeaderBack://HEADER BACK
                finish();
                break;

            case R.id.imgToolbarHome:// TOOLBAR HOME
                startActivity(new Intent(mContext, HomeActivity.class));
                finish();
                break;

            case R.id.imgToolbarSettings:// TOOLBAR SETTINGS
                startActivity(new Intent(mContext, SettingsActivity.class));
                break;

            case R.id.btnMyVendors://MY VENDOR
                Toast.makeText(mContext, "Please wait", Toast.LENGTH_LONG).show();
                loadVendorList(false);
                break;

            case R.id.btnEvents://EVENTS
                Toast.makeText(mContext, "Please wait", Toast.LENGTH_LONG).show();
                loadEvent(false);
                break;

            case R.id.btnNotes://NOTES
                Toast.makeText(mContext, "Please wait", Toast.LENGTH_LONG).show();
                loadNotes(false);
                break;

            case R.id.btnAllWedding://ALL WEDDING REPORT
                Toast.makeText(mContext, "Please wait", Toast.LENGTH_LONG).show();
                loadVendorList(true);
                break;
            default:
                break;
        }
    }


    private void loadVendorList(final boolean isAllWeddingReport) {
        vendors.clear();
        String tag_json_obj = "getVendor";
        ContentValues values = new ContentValues();
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_GETVENDOR);
        values.put(Constants.PARAM_USERID, mAppSettings.getUserId());
        String url = Constants.URL_BASE + IOUtils.getQuery(values);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("test", "vendor");
                            JSONArray data = response.getJSONObject("Response")
                                    .getJSONArray("Data");
                            Gson gson = new Gson();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject object = data.getJSONObject(i);
                                Vendor vendor = gson.fromJson(
                                        object.toString(), Vendor.class);
                                vendors.add(vendor.getPDFData());
                            }

                            //CreatedPDF
                            if (isAllWeddingReport)
                                loadEvent(isAllWeddingReport);
                            else
                                CreatedPDF(vendors);

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


    private void loadEvent(final boolean isAllWeddingReport) {
        events.clear();
        String tag_json_obj = "getEvent";
        ContentValues values = new ContentValues();
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_GETEVENT_GUEST);
        values.put(Constants.PARAM_USERID, mAppSettings.getUserId());
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
                                Event event = gson.fromJson(object.toString(),
                                        Event.class);
                                events.add(event.getPDFData());
                            }

                            //CREATE PDF
                            if (isAllWeddingReport)
                                loadFinance(isAllWeddingReport);
                            else
                                CreatedPDF(events);
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

    private void loadFinance(final boolean isAllWeddingReport) {
        finance.clear();
        String tag_json_obj = "getFinance";
        ContentValues values = new ContentValues();
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_GETFINANCE);
        values.put(Constants.PARAM_USERID, mAppSettings.getUserId());
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
                                Finance event = gson.fromJson(object.toString(),
                                        Finance.class);
                                //TODO GET TANSATION OBJ
                                finance.add(event.getPDFData());
                            }

                            //CREATE PDF
                            if (isAllWeddingReport)
                                loadNotes(isAllWeddingReport);
                            else
                                CreatedPDF(finance);
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

    private void loadNotes(final boolean isAllWeddingReport) {
        noteList.clear();
        String tag_json_obj = "getNotes";
        ContentValues values = new ContentValues();
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_GETNOTES);
        values.put(Constants.PARAM_USERID, mAppSettings.getUserId());
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
                                Notes note = gson.fromJson(object.toString(),
                                        Notes.class);
                                noteList.add(note.getPDFData());
                            }

                            //CREATE PDF
                            if (!isAllWeddingReport)
                                CreatedPDF(noteList);
                            else {
                                //TODO
                                //ALL WEDDING REPORT
                                ArrayList<String> temp = new ArrayList<String>();
                                temp.addAll(vendors);
                                temp.addAll(events);
                                temp.addAll(finance);
                                temp.addAll(noteList);


                                Log.d("test", "all vendor" + vendors.size());
                                Log.d("test", "all event" + events.size());
                                Log.d("test", "all notes" + noteList.size());
                                Log.d("test", "all finanace" + finance.size());
                                Log.d("test", "all items" + temp.size());
                                CreatedPDF(temp);
                            }

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
