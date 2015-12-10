package in.appsplanet.wedsource;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import in.appsplanet.wedsource.utils.AppSettings;

public class SplashActivity extends AppCompatActivity {
	private Context mContext;
	AppSettings mAppSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		mContext = this;

		mAppSettings = new AppSettings(mContext);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (mAppSettings.getUserId() == null)
					startActivity(new Intent(SplashActivity.this,
							LoginActivity.class));
				else
					startActivity(new Intent(SplashActivity.this,
							HomeActivity.class));

				finish();

			}
		}, 2000);
	}

}
