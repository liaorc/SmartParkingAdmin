package icat.sjtu.edu.smartparkingadmin;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Ruochen on 2015/8/17.
 */
public class Order {
    public static final int ORDER_TYPE_APPOINTMENT = 1;
    public static final int ORDER_TYPE_INSTANCE = 0;

    private int mOrderId;
    private int mOrderType;
    private Date mSubmitTime;
    private Date mAppointmentTime;
    private Date mConfirmedTime;
    private int mUserId;
    private int mFee;
    private UserInfo mUserInfo;
    private int mDistance;

    public Order(JSONObject json) throws JSONException {
        mOrderId = json.getInt(JSONLabel.ORDERID);
        mOrderType = json.getInt(JSONLabel.ORDER_TYPE);
        mSubmitTime = new Date(json.getLong(JSONLabel.SUBMIT_TIME)*1000);
        mAppointmentTime = new Date(json.getLong(JSONLabel.APPOINTMENT_TIME)*1000);
        mUserId = json.getInt(JSONLabel.USERID);
        mFee = json.getInt(JSONLabel.FEE);
        if(json.has(JSONLabel.CONFIRMED_TIME))
            mConfirmedTime = new Date(json.getLong(JSONLabel.CONFIRMED_TIME)*1000);
        if(json.has(JSONLabel.USER_INFO)) {
            //Log.d("Order_test", "user info:" + json.getString(JSONLabel.USER_INFO));
            mUserInfo = new UserInfo(json.getJSONObject(JSONLabel.USER_INFO));
        }
        if(json.has(JSONLabel.DISTANCE))
            mDistance = json.getInt(JSONLabel.DISTANCE);
    }

    public int getFee() {
        return mFee;
    }

    public void setFee(int fee) {
        mFee = fee;
    }

    public int getOrderId() {
        return mOrderId;
    }

    public void setOrderId(int orderId) {
        mOrderId = orderId;
    }

    public int getOrderType() {
        return mOrderType;
    }

    public void setOrderType(int orderType) {
        mOrderType = orderType;
    }

    public Date getSubmitTime() {
        return mSubmitTime;
    }

    public void setSubmitTime(Date submitTime) {
        mSubmitTime = submitTime;
    }

    public Date getAppointmentTime() {
        return mAppointmentTime;
    }

    public void setAppointmentTime(Date appointmentTime) {
        mAppointmentTime = appointmentTime;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        mUserInfo = userInfo;
    }

    public int getDistance() {
        return mDistance;
    }
}
