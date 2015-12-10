package in.appsplanet.wedsource;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import in.appsplanet.wedsource.calendar.CalendarActivity;
import in.appsplanet.wedsource.event.EventActivity;
import in.appsplanet.wedsource.finance.FinanceActivity;
import in.appsplanet.wedsource.note.NotesActivity;
import in.appsplanet.wedsource.songs.SongsActivity;
import in.appsplanet.wedsource.utils.AppSettings;
import in.appsplanet.wedsource.utils.Constants;
import in.appsplanet.wedsource.vender.VendorDirectoryActivity;

public class HomeActivity extends AppCompatActivity implements OnClickListener {
    private Context mContext;
    private AppSettings mAppSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mContext = this;
        init();
    }

    private void init() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView imgSettings = (ImageView) toolbar
                .findViewById(R.id.imgToolbarSettings);
        imgSettings.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        mAppSettings=new AppSettings(HomeActivity.this);
        if (mAppSettings.getUserId() == null)
            finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgToolbarSettings:// TOOLBAR SETTINGS
                startActivity(new Intent(mContext, SettingsActivity.class));
                break;

            case R.id.btnVendorDirectory:// VENDOR DIRECTORY
                startActivity(new Intent(mContext, VendorDirectoryActivity.class)
                        .putExtra(Constants.INTENT_IS_MY_VENDOR, false));
                break;

            case R.id.btnMyVendors:// My VENDOR
                startActivity(new Intent(mContext, VendorDirectoryActivity.class)
                        .putExtra(Constants.INTENT_IS_MY_VENDOR, true));
                break;

            case R.id.btnNotes:// NOTES
                startActivity(new Intent(mContext, NotesActivity.class));
                break;

            case R.id.btnSummary:// SUMMARY
                startActivity(new Intent(mContext, SummaryActivity.class));
                break;


            case R.id.btnSongs:// songs
                startActivity(new Intent(mContext, SongsActivity.class));
                break;

            case R.id.btnEvents:// EVENTS
                startActivity(new Intent(mContext, EventActivity.class));
                break;

            case R.id.btnCalendar://CALENDAR
                startActivity(new Intent(mContext, CalendarActivity.class));
                break;

            case R.id.btnFinances://FINANCE
                startActivity(new Intent(mContext, FinanceActivity.class));
                break;

            case R.id.btnPromotions://PROMOTIONS
                startActivity(new Intent(mContext, PromotionsActivity.class));
                break;
            default:
                break;
        }
    }

}
