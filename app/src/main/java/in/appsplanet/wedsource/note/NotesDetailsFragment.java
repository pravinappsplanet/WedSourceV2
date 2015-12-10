package in.appsplanet.wedsource.note;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import in.appsplanet.wedsource.R;
import in.appsplanet.wedsource.WedSourceApp;
import in.appsplanet.wedsource.pojo.Notes;
import in.appsplanet.wedsource.utils.Constants;
import in.appsplanet.wedsource.utils.CustomRequest;
import in.appsplanet.wedsource.utils.IOUtils;

public class NotesDetailsFragment extends Fragment implements OnClickListener {
    private Context mContext;
    private TextView mTxtName, mTxtDescription;
    private Notes mNotes;
    private ImageView mImgDelete, mImgShare, mImgEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes_details, null);
        mContext = getActivity();

        mNotes = (Notes) getArguments().getSerializable(Constants.INTENT_NOTES);
        mTxtName = (TextView) view.findViewById(R.id.txtName);
        mTxtDescription = (TextView) view.findViewById(R.id.txtDescription);

        mImgDelete = (ImageView) view.findViewById(R.id.imgDelete);
        mImgDelete.setOnClickListener(this);
        mImgShare = (ImageView) view.findViewById(R.id.imgShare);
        mImgShare.setOnClickListener(this);

        mImgEdit = (ImageView) view.findViewById(R.id.imgEdit);
        mImgEdit.setOnClickListener(this);
        loadDetails();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // UPDATE HEADER
        ((NotesActivity) getActivity()).setHeader("Notes");
    }

    private void loadDetails() {
        mTxtName.setText(mNotes.getName());
        mTxtDescription.setText(mNotes.getDate() + "\n\n"
                + mNotes.getDescription());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgDelete://DELETE
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure you want to delete note?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteNote(mNotes.getId() + "");
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

                break;
            case R.id.imgShare://SHARE
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mNotes.getName());
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, mNotes.getName() + " " + mNotes.getDescription());

                mContext.startActivity(Intent.createChooser(shareIntent, "Share"));
                break;

            case R.id.imgEdit://EDIT
                getActivity().getSupportFragmentManager().popBackStack();
                EditNoteFragment feedbackFragment = new EditNoteFragment();
                Bundle args = new Bundle();
                args.putSerializable(Constants.INTENT_NOTES, mNotes);
                feedbackFragment.setArguments(args);

                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack(feedbackFragment.getClass()
                        .getName());
                fragmentTransaction.replace(R.id.frag_content, feedbackFragment);
                fragmentTransaction.commit();


                break;
            default:
                break;
        }
    }

    /**
     * @param id
     */
    private void deleteNote(String id) {
        String tag_json_obj = "deleteNote";
        ContentValues values = new ContentValues();
        values.put(Constants.PARAM_COMMAND, Constants.COMMAND_DELETENOTES);
        values.put(Constants.PARAM_NOTESID, id);
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
                                getActivity().getSupportFragmentManager().popBackStack();
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
