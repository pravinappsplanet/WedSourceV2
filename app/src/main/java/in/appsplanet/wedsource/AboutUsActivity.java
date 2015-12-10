package in.appsplanet.wedsource;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class AboutUsActivity extends AppCompatActivity implements
        View.OnClickListener {

    private Context mContext;
    private TextView mTxtHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        mContext = this;
        init();

    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTxtHeader = (TextView) findViewById(R.id.txtHeader);
        mTxtHeader.setText("About Us");
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

            default:
                break;
        }
    }
}
