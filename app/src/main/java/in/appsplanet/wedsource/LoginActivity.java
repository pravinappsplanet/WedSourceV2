package in.appsplanet.wedsource;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import in.appsplanet.wedsource.utils.AppSettings;
import in.appsplanet.wedsource.utils.Constants;
import in.appsplanet.wedsource.utils.CustomRequest;
import in.appsplanet.wedsource.utils.IOUtils;

public class LoginActivity extends AppCompatActivity implements OnClickListener {

    private Context mContext;
    private EditText mEdtEmailId, mEdtPassword;
    private AppSettings mAppSettings;
    private CheckBox mChkRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        mContext = this;
        init();
    }

    private void init() {
        mEdtEmailId = (EditText) findViewById(R.id.edtEmailId);
        mEdtPassword = (EditText) findViewById(R.id.edtPassword);
        mChkRememberMe = (CheckBox) findViewById(R.id.chkRememberMe);
        mAppSettings = new AppSettings(mContext);

        if (mAppSettings.getRememberUserName() != null) {
            mChkRememberMe.setChecked(true);
        } else {
            mChkRememberMe.setChecked(false);
        }

        mEdtEmailId.setText(mAppSettings.getRememberUserName());
        mEdtPassword.setText(mAppSettings.getRememberPassword());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:// LOGIN
                String email = mEdtEmailId.getText().toString().trim();
                String password = mEdtPassword.getText().toString().trim();
                // VALIDATIONS
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(mContext, "Please enter email id",
                            Toast.LENGTH_SHORT).show();
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(mContext, "Please enter valid email id",
                            Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(mContext, "Please enter password",
                            Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    doLogin(email, password);
                }
                break;

            case R.id.btnForgotPassword://FORGOT PASSWORD
                final Dialog dialog = new Dialog(mContext);
                dialog.setTitle("Forgot Password");
                dialog.setContentView(R.layout.dialog_forgot_password);
                dialog.show();

                Button btnSubmit = (Button) dialog.findViewById(R.id.btnSubmit);
                final EditText edtName = (EditText) dialog.findViewById(R.id.edtEmail);
                btnSubmit.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = edtName.getText().toString().trim();
                        if (TextUtils.isEmpty(name)) {
                            edtName.setError("Please insert email");
                        } else {
                            //TODO FORGOT PASSWORD API CALL
                            forgotPassword(name);
                            dialog.dismiss();
                        }
                    }
                });
                break;

            case R.id.btnPrivacyPolicy://PrivacyPolicy
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(Constants.URL_PRIVACY_POLICY));
                startActivity(i);
                break;
            default:
                break;
        }
    }

    private void doLogin(final String email, final String password) {
        String tag_json_obj = "login";
        ContentValues values = new ContentValues();
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_USERLOGIN);
        values.put(Constants.PARAM_USERNAME, email);
        values.put(Constants.PARAM_PASSWORD, password);

        String url = Constants.URL_BASE + IOUtils.getQuery(values);
        Log.d("test", "login ulr" + url);

        CustomRequest jsObjRequest = new CustomRequest(Method.GET, url, null,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("test", "login" + response.toString());
                            JSONObject data = response
                                    .getJSONObject("Response").getJSONObject(
                                            "Data");
                            if (data.getString("success").equalsIgnoreCase(
                                    "true")) {
                                startActivity(new Intent(mContext,
                                        HomeActivity.class));

                                // SAVE USER ID
                                mAppSettings.setUserId(data.getString("id"));

                                //REMEMBER ME
                                if (mChkRememberMe.isChecked()) {
                                    mAppSettings.setRememberUserName(email);
                                    mAppSettings.setRememberPassword(password);
                                } else {
                                    mAppSettings.setRememberUserName(null);
                                    mAppSettings.setRememberPassword(null);
                                }

                                finish();
                            }
                            Toast.makeText(mContext, data.getString("message"),
                                    Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }, new ErrorListener() {
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

    /**
     * @param email
     */
    private void forgotPassword(String email) {
        String tag_json_obj = "FORGOTPASSWORD";
        ContentValues values = new ContentValues();
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_FORGOTPASSWORD);
        values.put(Constants.PARAM_EMAIL, email);

        String url = Constants.URL_BASE + IOUtils.getQuery(values);

        CustomRequest jsObjRequest = new CustomRequest(Method.GET, url, null,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response
                                    .getJSONObject("Response").getJSONObject(
                                            "Data");
                            if (data.getString("success").equalsIgnoreCase(
                                    "true")) {

                            }
                            Toast.makeText(mContext, data.getString("message"),
                                    Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }, new ErrorListener() {
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

}
