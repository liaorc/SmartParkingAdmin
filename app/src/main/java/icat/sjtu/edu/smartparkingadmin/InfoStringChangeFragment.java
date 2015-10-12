package icat.sjtu.edu.smartparkingadmin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Ruochen on 2015/9/25.
 */
public class InfoStringChangeFragment extends DialogFragment {
    public static final String EXTRA_STRING = "string";
    private String mTitle;
    private String mString;
    private EditText mEditText;

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setString(String string) {
        mString = string;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_string_change, null);

        mEditText = (EditText)v.findViewById(R.id.stringEditText);
        mEditText.setText(mString);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mString = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(mTitle)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .setNegativeButton("取消", null)
                .create();
    }

    private void sendResult( int resultCode) {
        if (getTargetFragment() == null)
            return ;
        Intent i = new Intent();
        i.putExtra(EXTRA_STRING, mString);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }
}
