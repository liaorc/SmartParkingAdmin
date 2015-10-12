package icat.sjtu.edu.smartparkingadmin;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;

import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Ruochen on 2015/9/24.
 */
public class CompletedListFragment extends ListFragment {
    private static final String TAG = "completed_list_fragment";
    private static ListView mListView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    SwipeRefreshLayout mSwipeRefreshLayoutEmpty;

    private ArrayList<Order> mOrders;

    //private class OrderAdapter(ArrayList<Order> orders)

    private class OrderAdapter extends ArrayAdapter<Order> {
        public OrderAdapter(ArrayList<Order> orders) {
            super(getActivity(), 0, orders);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_completed, null);
            }
            if ((position % 2) == 0) {
                convertView.setBackgroundColor(getResources().getColor(R.color.secondary_background));
                //Log.d(TAG, "$$$$$position is: " + position);
            } else {
                convertView.setBackgroundColor(getResources().getColor(R.color.main_background));
                //Log.d(TAG, "position is: " + position);
            }

            Order o = getItem(position);

            TextView usernameTextView = (TextView)convertView.findViewById(R.id.completed_list_item_usernameTextView);
            usernameTextView.setText(o.getUserInfo().getName());

            TextView appointmentTimeTextView = (TextView) convertView.findViewById(R.id.completed_list_item_appointmentTimeTextView);
            //Log.d(TAG, o.getSubmitTime().toString());
            appointmentTimeTextView.setText(MiscUtils.getTimeDescription(o.getSubmitTime(), new Date()));

            TextView feeTextView = (TextView) convertView.findViewById(R.id.completed_list_item_feeTextView);
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


        //OrderList.get(getActivity()).downloadOrderList();

        mOrders = CompletedOrders.get(getActivity()).getOrders();

        OrderAdapter adapter = new OrderAdapter(mOrders);

        setListAdapter(adapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_completed_list,container, false);
        mListView = (ListView)v.findViewById(android.R.id.list);

        View header = inflater.inflate(R.layout.header_view_completed_list, mListView, false);

        header.setClickable(false);
        header.setFocusable(false);
        //View emptyView = v.findViewById(R.id.empty);
        //mListView.setEmptyView(emptyView);
        mListView.addHeaderView(header);

        mSwipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.order_list_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshOrderList(mSwipeRefreshLayout);
            }
        });

        mSwipeRefreshLayoutEmpty = (SwipeRefreshLayout)v.findViewById(android.R.id.empty);
        mSwipeRefreshLayoutEmpty.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshOrderList(mSwipeRefreshLayoutEmpty);
            }
        });

        return v;
    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mSwipeRefreshLayout.setRefreshing(true);
//        refreshOrderList(mSwipeRefreshLayout);
//    }

    class ListRefreshHandler extends AsyncHttpResponseHandler {
        SwipeRefreshLayout mLayout = null;
        public ListRefreshHandler(SwipeRefreshLayout layout) {
            mLayout = layout;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            Log.i(TAG, "info: " + (new String(responseBody)));
            ServerRequest serverRequest = new ServerRequest();
            try {
                serverRequest.setJSONRev(new String(responseBody));
                if (serverRequest.getStatusCode() == 0) {
                    CompletedOrders.get(getActivity()).update(serverRequest.getData(), 0);
                }
            } catch (Exception e){

            } finally {
                ((OrderAdapter) getListAdapter()).notifyDataSetChanged();
                if (mLayout != null)
                    mLayout.setRefreshing(false);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            if (mLayout != null)
                mLayout.setRefreshing(false);
        }
    }

    private void refreshOrderList(SwipeRefreshLayout layout) {
        try {
            ServerRequest serverRequest = new ServerRequest();
            serverRequest.setString(JSONLabel.SESSION,
                    CurrentUser.get(getActivity()).getSession());
            serverRequest.setInt(JSONLabel.TYPE, 1);
            serverRequest.setInt(JSONLabel.PAGE, 0);
            AsyncHttpClient client = new AsyncHttpClient();
            client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
            client.get(serverRequest.queryConfirmed(), new ListRefreshHandler(layout));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}