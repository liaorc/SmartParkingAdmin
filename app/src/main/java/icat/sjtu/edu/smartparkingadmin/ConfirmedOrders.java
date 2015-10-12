package icat.sjtu.edu.smartparkingadmin;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONTokener;

import java.util.ArrayList;

/**
 * Created by Ruochen on 2015/9/24.
 */
public class ConfirmedOrders {
    private static final String TAG = "QUERY_LIST";
    public static final int LIST_REFRESH = 0;
    public static final int LIST_LOAD_MORE = 1;

    private ArrayList<Order> mOrders;

    private static ConfirmedOrders sConfirmedOrders;
    private Context mAppContext;

    private int mNextPage;

    public int getNextPage() {
        return mNextPage;
    }

    private ConfirmedOrders (Context appContext) {
        mAppContext = appContext;
        mOrders = new ArrayList<Order>();
        mNextPage = 0;
    }

    public static ConfirmedOrders get(Context c) {
        if (sConfirmedOrders == null) {
            sConfirmedOrders = new ConfirmedOrders(c.getApplicationContext());
        }
        return sConfirmedOrders;
    }

    public void update(String data, int mode) {
        if(mode == LIST_REFRESH) {
            clear();
            mNextPage = 1;
        } else {
            mNextPage++;
        }
        try {
            //task.queryOrders();
            //int status = task.getStatusCode();
            JSONArray array = (JSONArray) new JSONTokener(data).nextValue();
            //task.getOrderList();
            for ( int i=0 ; i<array.length() ; i++ ) {
                Order order = new Order(array.getJSONObject(i));
                mOrders.add(order);
                Log.d(TAG, array.getJSONObject(i).toString());
                Log.d(TAG, "submit_time: "+order.getSubmitTime().toString());
            }
        } catch (Exception e) {
            Log.d(TAG, "Req Error");
        }
        Log.d(TAG, "IDKW :" + mOrders.size());
    }

    public ArrayList<Order> getOrders() {
        return mOrders;
    }

    public void clear() {
        mOrders.clear();
    }
}
