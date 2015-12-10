package in.appsplanet.wedsource.finance;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import in.appsplanet.wedsource.R;
import in.appsplanet.wedsource.WedSourceApp;
import in.appsplanet.wedsource.pojo.Event;
import in.appsplanet.wedsource.pojo.Finance;
import in.appsplanet.wedsource.pojo.Transaction;
import in.appsplanet.wedsource.utils.AppSettings;
import in.appsplanet.wedsource.utils.Constants;
import in.appsplanet.wedsource.utils.CustomRequest;
import in.appsplanet.wedsource.utils.IOUtils;

public class FinanceDetailsFragment extends Fragment implements OnClickListener {
    private Context mContext;
    public Event mEvent;
    private ArrayList<Transaction> mListTransaction;
    private LinearLayout mLyFinance;
    private AppSettings mAppSettings;
     private Finance mFinance;
    private ImageView mImgShare;
    private TextView mTxtGrandTotal, mTxtInComeTotal, mTxtExpenseTotal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finance, null);
        mContext = getActivity();

        mEvent = (Event) getArguments().getSerializable(Constants.INTENT_EVENT);
        mLyFinance = (LinearLayout) view.findViewById(R.id.lyFinance);
        mTxtGrandTotal = (TextView) view.findViewById(R.id.txtGrandTotal);
        mTxtExpenseTotal = (TextView) view.findViewById(R.id.txtTotalExpense);
        mTxtInComeTotal = (TextView) view.findViewById(R.id.txtTotalIncome);

        mImgShare = (ImageView) view.findViewById(R.id.imgShare);
        mImgShare.setOnClickListener(this);

        mAppSettings = new AppSettings(mContext);

        //loadFinance();
        loadTransaction();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        ((FinanceActivity) getActivity()).setHeader(mEvent.getName());
        ((FinanceActivity) getActivity()).mImgHeaderAdd.setVisibility(View.VISIBLE);
    }

    /**
     * load finance
     */
    private void loadFinance() {
        String tag_json_obj = "getFinance";
        ContentValues values = new ContentValues();
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_GETFINANCE);
        values.put(Constants.PARAM_EVENTID, mEvent.getId());
        String url = Constants.URL_BASE + IOUtils.getQuery(values);
        Log.d("test", "finance URL" + url);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("test", "finance response:" + response.toString());
                            JSONArray data = response.getJSONObject("Response")
                                    .getJSONArray("Data");
                            Gson gson = new Gson();
                            //FINANCE SET
                            if (data != null && data.length() > 0) {
                                mFinance = gson.fromJson(data.getJSONObject(0).toString(), Finance.class);
                                //LOAD TRANSACTION
                                loadTransaction();
                            } else {//BUDGET NOT SET YET
                                //REMOVE
//                                showDialogSetBudget();
                            }

                            Log.d("test", "finance val:" + mFinance.getId());

                        } catch (Exception e) {
                            e.printStackTrace();
                            //BUDGET NOT SET YET
                            //REMOVE
//                            showDialogSetBudget();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgShare://SHARE
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mEvent.getName());
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, TextUtils.join(", ", mListTransaction));

                mContext.startActivity(Intent.createChooser(shareIntent, "Share"));
                break;
            default:
                break;
        }
    }

    private void showDialogSetBudget() {
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle("Set budget");
        dialog.setContentView(R.layout.dialog_set_budget);
        dialog.setCancelable(false);
        dialog.show();

        Button btnSubmit = (Button) dialog.findViewById(R.id.btnSubmit);
        final EditText edtBudget = (EditText) dialog.findViewById(R.id.edtBudget);
        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String budget = edtBudget.getText().toString().trim();
                if (TextUtils.isEmpty(budget)) {
                    edtBudget.setError("Please insert budget");
                } else {
                    //TODO FORGOT PASSWORD API CALL
                    addBudget(budget);
                    dialog.dismiss();
                }
            }
        });

    }

    /**
     * add budget
     */
    private void addBudget(String budget) {
        String tag_json_obj = "getTransaction";
        ContentValues values = new ContentValues();
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_ADDFINANCE);
        values.put(Constants.PARAM_EVENTID, mEvent.getId());
        values.put(Constants.PARAM_USERID, mAppSettings.getUserId());
        values.put(Constants.PARAM_BUDGET, budget);
        String url = Constants.URL_BASE + IOUtils.getQuery(values);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response
                                    .getJSONObject("Response").getJSONObject(
                                            "Data");
                            if (data.getString("success").equalsIgnoreCase(
                                    "true")) {
                                loadFinance();
                            }
                            Toast.makeText(mContext, data.getString("message"),
                                    Toast.LENGTH_SHORT).show();

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

    /**
     * load transaction
     */
    private void loadTransaction() {
        mListTransaction = new ArrayList<Transaction>();
        mLyFinance.removeAllViews();
        String tag_json_obj = "getTransaction";
        ContentValues values = new ContentValues();
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_GETTRANSACTION);
        values.put(Constants.PARAM_EVENTID, mEvent.getId());
        String url = Constants.URL_BASE + IOUtils.getQuery(values);

        Log.d("test", "transaction  URL" + url);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("test", "transaction response:" + response.toString());
                            JSONArray data = response.getJSONObject("Response")
                                    .getJSONArray("Data");
                            Gson gson = new Gson();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject object = data.getJSONObject(i);
                                mListTransaction.add(gson.fromJson(object.toString(),
                                        Transaction.class));
                            }
                            setTransactionData(mListTransaction);
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

    /**
     * @param transactions
     */
    private void setTransactionData(ArrayList<Transaction> transactions) {
        try {

            int expTotal = 0;
            int incomeTotal = 0;
            for (Transaction guest : transactions) {
                if (guest.getType() == 1) {//INCOME
                    mLyFinance.addView(getTransactionItem(guest.getId(), guest.getName(), guest.getAmount() + "", ""));
                    incomeTotal = incomeTotal + guest.getAmount();
                } else {//EXP
                    mLyFinance.addView(getTransactionItem(guest.getId(), guest.getName(), "", guest.getAmount() + ""));
                    expTotal = expTotal + guest.getAmount();
                }
            }
            //TOTAL
            mTxtInComeTotal.setText(incomeTotal + "");
            mTxtExpenseTotal.setText(expTotal + "");

            //GRAND TOTAL
            mTxtGrandTotal.setText((incomeTotal - expTotal) + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param name
     * @param income
     * @param expense
     * @return
     */
    private View getTransactionItem(final int id, String name, String income, String expense) {
        View view = getLayoutInflater(getArguments()).inflate(R.layout.item_finance, null);
        TextView txtName = (TextView) view.findViewById(R.id.txtName);
        TextView txtIncome = (TextView) view.findViewById(R.id.txtIncome);
        TextView txtExpense = (TextView) view.findViewById(R.id.txtExpense);
        txtName.setText(name);
        txtIncome.setText(income);
        txtExpense.setText(expense);

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDialogDelete(id);
                return true;
            }
        });

        return view;
    }


    /**
     * @param id
     */
    private void showDialogDelete(final int id) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete transaction?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                delete(id + "");
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * @param id
     */
    private void delete(String id) {
        String tag_json_obj = "deleteNote";
        ContentValues values = new ContentValues();
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_DELETETRANSACTION);
        values.put(Constants.PARAM_TRANSACTIONID, id);
        String url = Constants.URL_BASE + IOUtils.getQuery(values);
        Log.d("test", "login ulr" + url);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response
                                    .getJSONObject("Response").getJSONObject(
                                            "Data");
                            if (data.getString("success").equalsIgnoreCase(
                                    "true")) {
                                loadTransaction();
                            }
                            Toast.makeText(mContext, data.getString("message"),
                                    Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
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
