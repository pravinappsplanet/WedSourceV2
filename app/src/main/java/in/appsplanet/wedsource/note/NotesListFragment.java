package in.appsplanet.wedsource.note;

import in.appsplanet.wedsource.R;
import in.appsplanet.wedsource.WedSourceApp;
import in.appsplanet.wedsource.pojo.Notes;
import in.appsplanet.wedsource.utils.AppSettings;
import in.appsplanet.wedsource.utils.Constants;
import in.appsplanet.wedsource.utils.CustomRequest;
import in.appsplanet.wedsource.utils.IOUtils;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
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
import com.google.gson.Gson;

public class NotesListFragment extends ListFragment implements
		OnItemClickListener {
	private Context mContext;
	private ListView mListView;
	private ArrayList<Notes> mListNotes;
	private AppSettings mAppSettings;
	private ArrayAdapter<Notes> mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		// Update header
		((NotesActivity) getActivity()).setHeader("Notes");

		mListNotes = new ArrayList<Notes>();
		mAdapter = new ArrayAdapter<Notes>(mContext,
				R.layout.item_vender_country, R.id.txtName, mListNotes);
		setListAdapter(mAdapter);

		mAppSettings = new AppSettings(mContext);

		loadNotes();
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mListView = getListView();
		mListView.setOnItemClickListener(this);

	}

	private void loadNotes() {
		String tag_json_obj = "getNotes";
		ContentValues values = new ContentValues();
		values.put(Constants.PARAM_COMMAND, Constants.COMMAND_GETNOTES);
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
								mListNotes.add(gson.fromJson(object.toString(),
										Notes.class));
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
		NotesDetailsFragment feedbackFragment = new NotesDetailsFragment();
		Bundle args = new Bundle();
		args.putSerializable(Constants.INTENT_NOTES, mListNotes.get(arg2));
		feedbackFragment.setArguments(args);

		FragmentTransaction fragmentTransaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.addToBackStack(feedbackFragment.getClass()
				.getName());
		fragmentTransaction.replace(R.id.frag_content, feedbackFragment);
		fragmentTransaction.commit();
	}
}
