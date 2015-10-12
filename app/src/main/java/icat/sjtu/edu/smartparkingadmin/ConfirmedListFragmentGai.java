package icat.sjtu.edu.smartparkingadmin;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;

import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Ruochen on 2015/9/24.
 */
public class ConfirmedListFragmentGai extends ListFragment {
    private static final String TAG = "confirmed_list_fragment";

    private PullToRefreshListView mListView;

    private ArrayList<Order> mOrders;
    private OrderAdapter mOrderAdapter;


    //private class OrderAdapter(ArrayList<Order> orders)

    private class OrderAdapter extends ArrayAdapter<Order> {
        public OrderAdapter(ArrayList<Order> orders) {
            super(getActivity(), 0, orders);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_confirmed, null);
            }
            if ((position % 2) == 0) {
                convertView.setBackgroundColor(getResources().getColor(R.color.secondary_background));
                //Log.d(TAG, "$$$$$position is: " + position);
            } else {
                convertView.setBackgroundColor(getResources().getColor(R.color.main_background));
                //Log.d(TAG, "position is: " + position);
            }

            Order o = getItem(position);

            TextView usernameTextView = (TextView)convertView.findViewById(R.id.confirmed_list_item_usernameTextView);
            usernameTextView.setText(o.getUserInfo().getName());

            TextView appointmentTimeTextView = (TextView) convertView.findViewById(R.id.confirmed_list_item_appointmentTimeTextView);
            //Log.d(TAG, o.getSubmitTime().toString());
            appointmentTimeTextView.setText(MiscUtils.getTimeDescription(o.getSubmitTime(), new Date()));

            TextView feeTextView = (TextView)convertView.findViewById(R.id.confirmed_list_item_feeTextView);
            //Log.d(TAG, o.getFee() + "");
            feeTextView.setText(o.getFee() + "元");
//
//            ImageButton confirmButton = (ImageButton) convertView.findViewById(R.id.order_list_item_confirmButton);
//            confirmButton.setFocusable(false);

            return convertView;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        mOrders = ConfirmedOrders.get(getActivity()).getOrders();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_order_list_gai, container, false);
        mListView = (PullToRefreshListView)v.findViewById(R.id.order_list);

        View empty = v.findViewById(R.id.order_list_empty);
        mListView.setEmptyView(empty);

        View header = inflater.inflate(R.layout.header_view_confirmed_list, null, false);

        header.setClickable(false);
        header.setFocusable(false);
        mOrderAdapter = new OrderAdapter(mOrders);
        mListView.setAdapter(mOrderAdapter);
        mListView.getRefreshableView().addHeaderView(header);
        //mListView.setScrollEmptyView(true);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);


        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshList(ConfirmedOrders.LIST_REFRESH);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshList(ConfirmedOrders.LIST_LOAD_MORE);
            }
        });
        return v;
    }

    class ListRefreshHandler extends AsyncHttpResponseHandler {
        private int mMode;

        public ListRefreshHandler(int mode) {
            mMode = mode;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            Log.i(TAG, "info: " + (new String(responseBody)));
            Log.i(TAG, "completed order got");
            ServerRequest serverRequest = new ServerRequest();
            try {
                serverRequest.setJSONRev(new String(responseBody));
                if (serverRequest.getStatusCode() == 0) {
                    ConfirmedOrders.get(getActivity()).update(serverRequest.getData(), mMode);
                }
            } catch (Exception e){

            } finally {
                mOrderAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        }

        @Override
        public void onFinish() {
            super.onFinish();
            mListView.onRefreshComplete();
        }
    }

    private void refreshList(int mode) {
        try {
            ServerRequest serverRequest = new ServerRequest();
            serverRequest.setString(JSONLabel.SESSION,
                    CurrentUser.get(getActivity()).getSession());
            serverRequest.setInt(JSONLabel.TYPE, 1);
            if(mode == CompletedOrders.LIST_REFRESH)
                serverRequest.setInt(JSONLabel.PAGE, 0);
            else
                serverRequest.setInt(JSONLabel.PAGE, ConfirmedOrders.get(getActivity()).getNextPage());
            AsyncHttpClient client = new AsyncHttpClient();
            client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
            client.get(serverRequest.queryConfirmed(), new ListRefreshHandler(mode));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}