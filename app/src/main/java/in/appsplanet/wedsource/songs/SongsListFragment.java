package in.appsplanet.wedsource.songs;

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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
import in.appsplanet.wedsource.pojo.Playlist;
import in.appsplanet.wedsource.pojo.Song;
import in.appsplanet.wedsource.utils.AppSettings;
import in.appsplanet.wedsource.utils.Constants;
import in.appsplanet.wedsource.utils.CustomRequest;
import in.appsplanet.wedsource.utils.IOUtils;

public class SongsListFragment extends Fragment implements
        OnItemClickListener, AdapterView.OnItemLongClickListener, View.OnClickListener {
    private Context mContext;
    private ListView mListView;
    private ArrayList<Song> mListSongs;
    private AppSettings mAppSettings;
    private ArrayAdapter<Song> mAdapter;
    private ImageView mImgShare;
    Playlist mPlaylist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songslist, null);
        mContext = getActivity();

        mListView = (ListView) view.findViewById(R.id.listViewSongs);
        mImgShare = (ImageView) view.findViewById(R.id.imgShare);
        mImgShare.setOnClickListener(this);
        mListSongs = new ArrayList<Song>();
        mAdapter = new ArrayAdapter<Song>(mContext,
                R.layout.item_vender_country, R.id.txtName, mListSongs);
        mListView.setAdapter(mAdapter);

        mAppSettings = new AppSettings(mContext);
        mPlaylist = (Playlist) getArguments().getSerializable(
                Constants.INTENT_PLAYLIST);

        // Update header
        ((SongsActivity) getActivity()).setHeader(mPlaylist.getName());
        loadSongs();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
    }

    protected void loadSongs() {
        mListSongs.clear();
        String tag_json_obj = "getPlaylist";
        ContentValues values = new ContentValues();
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_GETSONGS);
        values.put(Constants.PARAM_PLAYLISTID, mPlaylist.getId());
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
                                mListSongs.add(gson.fromJson(object.toString(),
                                        Song.class));
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
        // NotesDetailsFragment feedbackFragment = new NotesDetailsFragment();
        // Bundle args = new Bundle();
        // args.putSerializable(Constants.INTENT_NOTES, mListNotes.get(arg2));
        // feedbackFragment.setArguments(args);
        //
        // FragmentTransaction fragmentTransaction = getActivity()
        // .getSupportFragmentManager().beginTransaction();
        // fragmentTransaction.addToBackStack(feedbackFragment.getClass()
        // .getName());
        // fragmentTransaction.replace(R.id.frag_content, feedbackFragment);
        // fragmentTransaction.commit();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        showDialogDelete(mListSongs.get(position).getId());
        return true;
    }

    private void showDialogDelete(final int id) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete song?");
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
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_DELETESONGS);
        values.put(Constants.PARAM_SONGSID, id);
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
                                loadSongs();
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

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.imgShare) {//SHARE
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mPlaylist.getName());
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, TextUtils.join(", ", mListSongs));
            mContext.startActivity(Intent.createChooser(shareIntent, "Share"));
        }
    }
}
