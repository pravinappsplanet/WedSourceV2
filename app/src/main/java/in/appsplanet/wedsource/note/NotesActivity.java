package in.appsplanet.wedsource.note;

import in.appsplanet.wedsource.HomeActivity;
import in.appsplanet.wedsource.R;
import in.appsplanet.wedsource.SettingsActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class NotesActivity extends AppCompatActivity implements OnClickListener {

	private Context mContext;
	private TextView mTxtHeader;
	private ImageView mImgHeaderBack, mImgHeaderAdd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notes);

		mContext = this;
		init();

	}

	private void init() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();

		NotesListFragment fragment = new NotesListFragment();
		fragmentTransaction.replace(R.id.frag_content, fragment);
		fragmentTransaction.commit();

		mTxtHeader = (TextView) findViewById(R.id.txtHeader);
		mTxtHeader.setText("Notes");

		mImgHeaderBack = (ImageView) findViewById(R.id.imgHeaderBack);
		mImgHeaderBack.setOnClickListener(this);
		
		mImgHeaderAdd = (ImageView) findViewById(R.id.imgHeaderAdd);
		mImgHeaderAdd.setVisibility(View.VISIBLE);
		mImgHeaderAdd.setOnClickListener(this);

	}

	/**
	 * 
	 * @param header
	 */
	void setHeader(String header) {
		mTxtHeader.setText(header);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.imgToolbarHome:// TOOLBAR HOME
			startActivity(new Intent(mContext, HomeActivity.class));
			finish();
			break;

		case R.id.imgToolbarSettings:// TOOLBAR SETTIGNS
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
			AddNoteFragment feedbackFragment = new AddNoteFragment();
			FragmentTransaction fragmentTransaction = getSupportFragmentManager()
					.beginTransaction();
			fragmentTransaction.addToBackStack(feedbackFragment.getClass()
					.getName());
			fragmentTransaction.replace(R.id.frag_content, feedbackFragment);
			fragmentTransaction.commit();
			break;
		default:
			break;
		}
	}
}
