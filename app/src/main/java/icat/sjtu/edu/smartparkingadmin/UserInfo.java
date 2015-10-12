package icat.sjtu.edu.smartparkingadmin;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ruochen on 2015/9/27.
 */
public class UserInfo {

    private int mId;
    private String mName;
    private String mMobile;

    public UserInfo(JSONObject json) throws JSONException {
        //Log.d("test???test", json.toString());
        mId = json.getInt(JSONLabel.ID);
        mName = json.getString(JSONLabel.NAME);
        mMobile = json.getString(JSONLabel.MOBILE);
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getMobile() {
        return mMobile;
    }

    public void setMobile(String phone) {
        mMobile = phone;
    }
}
