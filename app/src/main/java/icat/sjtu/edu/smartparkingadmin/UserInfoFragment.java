package icat.sjtu.edu.smartparkingadmin;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Ruochen on 2015/9/25.
 */
public class UserInfoFragment extends Fragment {
    private static final String TAG = "user_info_fragment";

    private static final String DIALOG_DISPLAY_NAME = "display_name";
    private static final String DIALOG_ADDRESS = "address";
    private static final String DIALOG_PHONE = "phone";
    private static final String DIALOG_PRICE = "price";
    private static final int REQUEST_ADDRESS = 1;
    private static final int REQUEST_DISPLAY_NAME = 2;
    private static final int REQUEST_PHONE = 3;
    private static final int REQUEST_PRICE = 4;

    private TextView mUsernameTextView;

    private View mAddressLinearView;
    private TextView mAddressTextView;

    private View mDisplayNameLinearView;
    private TextView mDisplayTextView;

    private View mPhoneLinearView;
    private TextView mPhoneTextView;


    private View mPriceLinearView;
    private TextView mPriceTextView;

    private View mDoneOrdersLinearView;
    private TextView mDoneOrdersTextView;

    private Button mLogoutButton;

    private String mNewString;

    private void updateText() {
        mUsernameTextView.setText(CurrentUser.get(getActivity()).getUsername());
        mDisplayTextView.setText(CurrentUser.get(getActivity()).getDisplayName());
        mPhoneTextView.setText(CurrentUser.get(getActivity()).getPhone());
        mAddressTextView.setText(CurrentUser.get(getActivity()).getAddress());
        mPriceTextView.setText(CurrentUser.get(getActivity()).getPrice());
        mDoneOrdersTextView.setText(CurrentUser.get(getActivity()).getDoneOrders()+"个");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            ServerRequest serverRequest = new ServerRequest();
            serverRequest.setString(JSONLabel.SESSION, CurrentUser.get(getActivity()).getSession());
            AsyncHttpClient client = new AsyncHttpClient();
            client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
            client.get(serverRequest.getInfo(), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        JSONObject json = new JSONObject(new String(responseBody));
                        if (json.getInt(JSONLabel.STATUS) == 0) {
                            CurrentUser.get(getActivity()).update(new JSONObject(json.getString(JSONLabel.INFO)));
                            Log.d(TAG, "get info: " + json.getString(JSONLabel.INFO));
                            updateText();
                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });
        } catch (Exception e) {

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_user_info, container, false);

        mUsernameTextView = (TextView)v.findViewById(R.id.username_textView);
        mDisplayTextView = (TextView)v.findViewById(R.id.display_name_textView);
        mAddressTextView = (TextView)v.findViewById(R.id.address_textView);
        mPhoneTextView = (TextView)v.findViewById(R.id.phone_textView);
        mPriceTextView = (TextView)v.findViewById(R.id.price_textView);
        mDoneOrdersTextView = (TextView)v.findViewById(R.id.done_ordersTextView);

        mLogoutButton = (Button)v.findViewById(R.id.logoutButton);

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), LoginActivity.class);
                CurrentUser.get(getActivity()).setSession(null);
                CurrentUser.get(getActivity()).saveUser();
                startActivity(i);
                getActivity().finish();
            }
        });

        mAddressLinearView = v.findViewById(R.id.address_linearLayout);
        mDisplayNameLinearView = v.findViewById(R.id.display_name_linearLayout);
        mPhoneLinearView = v.findViewById(R.id.phone_linearLayout);
        mPriceLinearView = v.findViewById(R.id.price_linearLayout);

        //mAddressEditButton = (Button)v.findViewById(R.id.address_editButton);


        mAddressLinearView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getFragmentManager();
                InfoStringChangeFragment dialog = new InfoStringChangeFragment();
                dialog.setTitle("修改地址");
                dialog.setString(CurrentUser.get(getActivity()).getAddress());
                dialog.setTargetFragment(UserInfoFragment.this, REQUEST_ADDRESS);
                dialog.show(fm, DIALOG_ADDRESS);
            }
        });
        mDisplayNameLinearView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getFragmentManager();
                InfoStringChangeFragment dialog = new InfoStringChangeFragment();
                dialog.setTitle("修改名称");
                dialog.setString(CurrentUser.get(getActivity()).getDisplayName());
                dialog.setTargetFragment(UserInfoFragment.this, REQUEST_DISPLAY_NAME);
                dialog.show(fm, DIALOG_DISPLAY_NAME);
            }
        });
        mPhoneLinearView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getFragmentManager();
                InfoStringChangeFragment dialog = new InfoStringChangeFragment();
                dialog.setTitle("修改电话");
                dialog.setString(CurrentUser.get(getActivity()).getPhone());
                dialog.setTargetFragment(UserInfoFragment.this, REQUEST_PHONE);
                dialog.show(fm, DIALOG_PHONE);
            }
        });
        mPriceLinearView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getFragmentManager();
                InfoStringChangeFragment dialog = new InfoStringChangeFragment();
                dialog.setTitle("修改价格");
                dialog.setString(CurrentUser.get(getActivity()).getPrice());
                dialog.setTargetFragment(UserInfoFragment.this, REQUEST_PRICE);
                dialog.show(fm, DIALOG_PRICE);
            }
        });
        updateText();

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADDRESS) {
            mNewString = data.getStringExtra(InfoStringChangeFragment.EXTRA_STRING);
            try {
                ServerRequest serverRequest = new ServerRequest();
                serverRequest.setString(JSONLabel.SESSION, CurrentUser.get(getActivity()).getSession());
                serverRequest.setString(JSONLabel.ADDRESS, mNewString);
                AsyncHttpClient client = new AsyncHttpClient();
                client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
                client.setURLEncodingEnabled(true);
                client.get(serverRequest.changeAddress(), new AsyncHttpResponseHandler() {
                    private void failedMessage(){
                        Toast.makeText(getActivity(), getString(R.string.change_failed), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            JSONObject json = new JSONObject(new String(responseBody));
                            if (json.getInt(JSONLabel.STATUS) == 0) {
                                //CurrentUser.get(getActivity()).update(new JSONObject(json.getString(JSONLabel.ADDRESS)));
                                CurrentUser.get(getActivity()).setAddress(mNewString);
                                Toast.makeText(getActivity(), getString(R.string.change_complete), Toast.LENGTH_SHORT).show();
                                updateText();
                            }
                            else {
                                Log.d(TAG, "error " + json.toString());
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "failed: " + e.toString());
                            failedMessage();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d(TAG, "failed: ");
                        failedMessage();
                    }
                });
            } catch (Exception e) {

            }

        } else if (requestCode == REQUEST_DISPLAY_NAME) {
            mNewString = data.getStringExtra(InfoStringChangeFragment.EXTRA_STRING);
            try {
                ServerRequest serverRequest = new ServerRequest();
                serverRequest.setString(JSONLabel.SESSION, CurrentUser.get(getActivity()).getSession());
                serverRequest.setString(JSONLabel.NAME, mNewString);
                AsyncHttpClient client = new AsyncHttpClient();
                client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
                //client.setURLEncodingEnabled(true);
                client.get(serverRequest.changeDisplayName(), new AsyncHttpResponseHandler() {
                    private void failedMessage(){
                        Toast.makeText(getActivity(), getString(R.string.change_failed), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            JSONObject json = new JSONObject(new String(responseBody));
                            if (json.getInt(JSONLabel.STATUS) == 0) {
                                //CurrentUser.get(getActivity()).update(new JSONObject(json.getString(JSONLabel.ADDRESS)));
                                CurrentUser.get(getActivity()).setDisplayName(mNewString);
                                Toast.makeText(getActivity(), getString(R.string.change_complete), Toast.LENGTH_SHORT).show();
                                updateText();
                            }
                            else {
                                Log.d(TAG, "error " + json.toString());
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "failed: " + e.toString());
                            failedMessage();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d(TAG, "failed: ");
                        failedMessage();
                    }
                });
            } catch (Exception e) {

            }
        } else if (requestCode == REQUEST_PHONE) {
            mNewString = data.getStringExtra(InfoStringChangeFragment.EXTRA_STRING);
            try {
                ServerRequest serverRequest = new ServerRequest();
                serverRequest.setString(JSONLabel.SESSION, CurrentUser.get(getActivity()).getSession());
                serverRequest.setString(JSONLabel.PHONE, mNewString);
                AsyncHttpClient client = new AsyncHttpClient();
                client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
                client.get(serverRequest.changePhone(), new AsyncHttpResponseHandler() {
                    private void failedMessage(){
                        Toast.makeText(getActivity(), getString(R.string.change_failed), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            JSONObject json = new JSONObject(new String(responseBody));
                            if (json.getInt(JSONLabel.STATUS) == 0) {
                                //CurrentUser.get(getActivity()).update(new JSONObject(json.getString(JSONLabel.ADDRESS)));
                                CurrentUser.get(getActivity()).setPhone(mNewString);
                                Toast.makeText(getActivity(), getString(R.string.change_complete), Toast.LENGTH_SHORT).show();
                                updateText();
                            }
                            else {
                                Log.d(TAG, "error " + json.toString());
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "failed: " + e.toString());
                            failedMessage();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d(TAG, "failed: ");
                        failedMessage();
                    }
                });
            } catch (Exception e) {

            }
        } else if (requestCode == REQUEST_PRICE) {
            mNewString = data.getStringExtra(InfoStringChangeFragment.EXTRA_STRING);
            try {
                ServerRequest serverRequest = new ServerRequest();
                serverRequest.setString(JSONLabel.SESSION, CurrentUser.get(getActivity()).getSession());
                serverRequest.setString(JSONLabel.PRICE, mNewString);
                AsyncHttpClient client = new AsyncHttpClient();
                client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
                client.get(serverRequest.updateUserInfo(), new AsyncHttpResponseHandler() {
                    private void failedMessage(){
                        Toast.makeText(getActivity(), getString(R.string.change_failed), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            JSONObject json = new JSONObject(new String(responseBody));
                            if (json.getInt(JSONLabel.STATUS) == 0) {
                                //CurrentUser.get(getActivity()).update(new JSONObject(json.getString(JSONLabel.ADDRESS)));
                                CurrentUser.get(getActivity()).setPrice(mNewString);
                                Toast.makeText(getActivity(), getString(R.string.change_complete), Toast.LENGTH_SHORT).show();
                                updateText();
                            }
                            else {
                                Log.d(TAG, "error " + json.toString());
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "failed: " + e.toString());
                            failedMessage();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d(TAG, "failed: ");
                        failedMessage();
                    }
                });
            } catch (Exception e) {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
