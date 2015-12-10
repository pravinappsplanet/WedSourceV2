package in.appsplanet.wedsource.calendar;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import in.appsplanet.wedsource.R;
import in.appsplanet.wedsource.WedSourceApp;
import in.appsplanet.wedsource.event.AddEventFragment;
import in.appsplanet.wedsource.event.EventDetailsFragment;
import in.appsplanet.wedsource.pojo.Event;
import in.appsplanet.wedsource.utils.AppSettings;
import in.appsplanet.wedsource.utils.Constants;
import in.appsplanet.wedsource.utils.CustomRequest;
import in.appsplanet.wedsource.utils.IOUtils;

public class CalendarFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private Context mContext;
    private ListView mListView;
    private ArrayList<Event> mListEvent;
    private AppSettings mAppSettings;
    private ArrayAdapter<Event> mAdapter;
    private MaterialCalendarView mMaterialCalendarView;
    private SimpleDateFormat mSimpleDateFormat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, null);
        mContext = getActivity();


        mMaterialCalendarView = (MaterialCalendarView) view.findViewById(R.id.calendarView);
        mListView = (ListView) view.findViewById(R.id.listEvents);
        mAppSettings = new AppSettings(mContext);

        mListEvent = new ArrayList<Event>();
        mAdapter = new ArrayAdapter<Event>(mContext,
                R.layout.item_vender_country, R.id.txtName, mListEvent);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mSimpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
        mMaterialCalendarView.setOnClickListener(this);
        //CURRENT DATE
        mMaterialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);
        mMaterialCalendarView.setSelectionColor(getResources().getColor(R.color.grey));
        mMaterialCalendarView.setDateSelected(Calendar.getInstance().getTime(), true);
        mMaterialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
                dateSelected(date);
            }
        });
        loadEvent();


        return view;
    }

    /**
     * @param date
     */
    private void dateSelected(CalendarDay date) {
        Event event = getEventFromDate(date.getDate());
        //EVENT EXIST
        if (event != null) {
            loadEventDetails(event);
        } else {//ADD NEW
            //TODO PASS DATE TO EVENT
            AddEventFragment addEventFragment = new AddEventFragment();
            Bundle args = new Bundle();
            args.putSerializable(Constants.INTENT_EVENT_DATE, date.getDate());
            addEventFragment.setArguments(args);
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.addToBackStack(addEventFragment.getClass()
                    .getName());
            fragmentTransaction.replace(R.id.frag_content, addEventFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        ((CalendarActivity) getActivity()).setHeader("Calendar");
        ((CalendarActivity) getActivity()).mImgHeaderAdd.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.calendarView) {//CALENDAR
            Toast.makeText(mContext, "onclick", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadEvent() {
        mMaterialCalendarView.setSelectionColor(getResources().getColor(R.color.red));
        String tag_json_obj = "loadEvent";
        ContentValues values = new ContentValues();
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_GETEVENT);
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
                                mListEvent.add(event);


                                try {
                                    Date date = mSimpleDateFormat.parse(event.getDate());
                                    mMaterialCalendarView.setSelectionColor(getResources().getColor(R.color.red));
                                    mMaterialCalendarView.setDateSelected(date, true);
                                } catch (Exception e) {

                                }
//                                mMaterialCalendarView.notify();
                            }


                            // SET ADAPTER
                            mAdapter.notifyDataSetChanged();

                            setListViewHeightBasedOnChildren(mListView);

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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        loadEventDetails(mListEvent.get(position));
    }

    /**
     * @param event
     */
    private void loadEventDetails(Event event) {
        EventDetailsFragment eventDetailsFragment = new
                EventDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.INTENT_EVENT,
                event);
        eventDetailsFragment.setArguments(args);

        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(eventDetailsFragment.getClass()
                .getName());
        fragmentTransaction.replace(R.id.frag_content, eventDetailsFragment);
        fragmentTransaction.commit();
    }

    /**
     * @param date
     * @return
     */
    private Event getEventFromDate(Date date) {
        for (Event event : mListEvent) {
            if (event.getDate().equalsIgnoreCase(mSimpleDateFormat.format(date))) {
                return event;
            }
        }
        return null;
    }

    /**
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

}
