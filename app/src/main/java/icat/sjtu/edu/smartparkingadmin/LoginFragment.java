package icat.sjtu.edu.smartparkingadmin;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * Created by Ruochen on 2015/8/4.
 */
public class LoginFragment extends Fragment {
    private static final String TAG = "PARK_LOGIN_FRAGMENT";

    //private CurrentUser mCurrentUser;

    private EditText mUsernameField;
    private EditText mPasswordField;
    private Button mLoginButton;

    private void loginSuccess() {
        //Intent i = new Intent(getActivity(), OrderListActivity.class);
        Intent i = new Intent(getActivity(), MainActivity.class);
        startActivity(i);
        getActivity().finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // mCurrentUser = CurrentUser.get(getActivity());

        if (CurrentUser.get(getActivity()).getSession() != null ) {
            //start next activity
            loginSuccess();
        }

    }

    //@Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, parent, false);

        mUsernameField = (EditText) v.findViewById(R.id.usernameText);
        mUsernameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CurrentUser.get(getActivity()).setUsername(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });

        mPasswordField = (EditText) v.findViewById(R.id.passwordText);
        mPasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CurrentUser.get(getActivity()).setPassword(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });


        mLoginButton = (Button) v.findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {
                    HttpRequestTask loginReq = new HttpRequestTask();
                    loginReq.setString(JSONLabel.USERNAME,
                            CurrentUser.get(getActivity()).getUsername());
                    loginReq.setString(JSONLabel.PASSWORD,
                            CurrentUser.get(getActivity()).getPassword());
                    loginReq.login();
                    int status = loginReq.getStatusCode();
                    String session = loginReq.getSession();
                    if (status == 0) {
                        Toast.makeText(getActivity(), R.string.login_success, Toast.LENGTH_SHORT).show();
                        CurrentUser.get(getActivity()).setSession(session);
                        Log.d(TAG, "status:" + status);
                        Log.d(TAG, "session: " + CurrentUser.get(getActivity()).getSession());
                        //start next activity
                        loginSuccess();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                CurrentUser.get(getActivity()).setSession(null);
                Toast.makeText(getActivity(), R.string.login_fail, Toast.LENGTH_SHORT).show();
            }
        });

        mUsernameField.setText(CurrentUser.get(getActivity()).getUsername());
        mPasswordField.setText(CurrentUser.get(getActivity()).getPassword());

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        CurrentUser.get(getActivity()).saveUser();
    }
}
