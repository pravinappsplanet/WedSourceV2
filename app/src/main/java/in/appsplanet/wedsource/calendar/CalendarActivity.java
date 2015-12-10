package in.appsplanet.wedsource.calendar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import in.appsplanet.wedsource.HomeActivity;
import in.appsplanet.wedsource.R;
import in.appsplanet.wedsource.SettingsActivity;
import in.appsplanet.wedsource.event.AddEventFragment;
import in.appsplanet.wedsource.event.AddGuestFragment;
import in.appsplanet.wedsource.event.EventDetailsFragment;
import in.appsplanet.wedsource.utils.Constants;

public class CalendarActivity extends AppCompatActivity implements
        View.OnClickListener {

    private Context mContext;
    private TextView mTxtHeader;
    public ImageView mImgHeaderBack, mImgHeaderAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        mContext = this;
        init();

    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();

        CalendarFragment fragment = new CalendarFragment();
        fragmentTransaction.replace(R.id.frag_content, fragment);
        fragmentTransaction.commit();

        mTxtHeader = (TextView) findViewById(R.id.txtHeader);
        mTxtHeader.setText("Calendar");

        mImgHeaderBack = (ImageView) findViewById(R.id.imgHeaderBack);
        mImgHeaderBack.setOnClickListener(this);

        mImgHeaderAdd = (ImageView) findViewById(R.id.imgHeaderAdd);
        mImgHeaderAdd.setVisibility(View.VISIBLE);
        mImgHeaderAdd.setOnClickListener(this);
    }


    /**
     * @param header
     */
    public void setHeader(String header) {
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
                final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frag_content);
                if (fragment instanceof CalendarFragment) {//ADD EVENT
                    AddEventFragment addEventFragment = new AddEventFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.addToBackStack(addEventFragment.getClass()
                            .getName());
                    fragmentTransaction.replace(R.id.frag_content, addEventFragment);
                    fragmentTransaction.commit();
                } else if (fragment instanceof EventDetailsFragment) {//ADD GUEST
                    AddGuestFragment addGuestFragment = new AddGuestFragment();
                    Bundle args = new Bundle();
                    args.putSerializable(Constants.INTENT_EVENT,
                            ((EventDetailsFragment) fragment).mEvent);
                    addGuestFragment.setArguments(args);

                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.addToBackStack(addGuestFragment.getClass()
                            .getName());
                    fragmentTransaction.replace(R.id.frag_content, addGuestFragment);
                    fragmentTransaction.commit();
                }

                break;
        }
    }

}
