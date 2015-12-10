package in.appsplanet.wedsource;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;

import in.appsplanet.wedsource.utils.AppSettings;

public class SettingsActivity extends AppCompatActivity implements
        OnClickListener {
    private Context mContext;
    private AppSettings mAppSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mContext = this;
        init();
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mAppSettings = new AppSettings(mContext);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogout:// LOGOUT
                mAppSettings.setUserId(null);
                startActivity(new Intent(mContext, LoginActivity.class));
                finish();
                break;

            case R.id.imgToolbarHome:// TOOLBAR HOME
                startActivity(new Intent(mContext, HomeActivity.class));
                finish();
                break;

            case R.id.imgToolbarSettings:// TOOLBAR SETTINGS
                finish();
                break;

            case R.id.btnAboutUs://ABOUT US
                startActivity(new Intent(mContext, AboutUsActivity.class));
                break;

            case R.id.btnSummary:
                startActivity(new Intent(mContext, SummaryActivity.class));
                break;
            case R.id.btnContactUs://CONTACT US
                startActivity(new Intent(mContext, ContactUsActivity.class));
                break;
            default:

                break;
        }
    }

}
