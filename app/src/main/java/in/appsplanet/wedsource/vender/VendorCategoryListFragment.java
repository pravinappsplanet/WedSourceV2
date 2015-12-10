package in.appsplanet.wedsource.vender;

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
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import in.appsplanet.wedsource.R;
import in.appsplanet.wedsource.WedSourceApp;
import in.appsplanet.wedsource.pojo.Category;
import in.appsplanet.wedsource.pojo.Country;
import in.appsplanet.wedsource.utils.Constants;
import in.appsplanet.wedsource.utils.IOUtils;

public class VendorCategoryListFragment extends ListFragment implements
		OnItemClickListener {

	private Context mContext;
	private ListView mListView;
	private ArrayList<Category> mlistCategory;
	private ArrayAdapter<Category> mAdapter;
	private Country mCountry;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();

		mlistCategory = new ArrayList<Category>();

		mAdapter = new ArrayAdapter<Category>(mContext,
				R.layout.item_vender_country, R.id.txtName, mlistCategory);
		setListAdapter(mAdapter);

		mCountry = (Country) getArguments().getSerializable(
				Constants.INTENT_COUNTRY);
		loadCategory();
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mListView = getListView();
		mListView.setOnItemClickListener(this);

		// UPDATE HEADER
		((VendorDirectoryActivity) getActivity())
				.setHeader("Vendor Directory");

		//ADD VISIBLE
		((VendorDirectoryActivity) getActivity()).mImgHeaderAdd.setVisibility(View.GONE);
	}

	private void loadCategory() {
		String tag_json_obj = "getCountry";
		ContentValues values = new ContentValues();
		values.put(Constants.PARAM_COMMAND, Constants.COMMAND_GETCATEGORY);
		String url = Constants.URL_BASE + IOUtils.getQuery(values);

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(Method.GET, url,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							JSONArray data = response.getJSONObject("Response")
									.getJSONArray("Data");
							Gson gson = new Gson();
							for (int i = 0; i < data.length(); i++) {
								JSONObject object = data.getJSONObject(i);
								mlistCategory.add(gson.fromJson(
										object.toString(), Category.class));
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
		VendorNameListFragment feedbackFragment = new VendorNameListFragment();
		Bundle args = new Bundle();
		args.putSerializable(Constants.INTENT_CATEGORY, mlistCategory.get(arg2));
		args.putSerializable(Constants.INTENT_COUNTRY, mCountry);
		feedbackFragment.setArguments(args);

		FragmentTransaction fragmentTransaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.addToBackStack(feedbackFragment.getClass()
				.getName());
		fragmentTransaction.replace(R.id.frag_content, feedbackFragment);
		fragmentTransaction.commit();
	}
}
