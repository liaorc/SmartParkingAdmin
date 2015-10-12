package icat.sjtu.edu.smartparkingadmin;


import android.app.Fragment;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;

import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Ruochen on 2015/8/13.
 */
public class OrderListFragmentGai extends Fragment {

    private static final String TAG = "PARK_QUERY_LIST";
    //private static ListView mListView;
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
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_order, null);
            }
            if( (position%2) == 0 ) {
                convertView.setBackgroundColor(getResources().getColor(R.color.secondary_background));
                //Log.d(TAG, "$$$$$position is: " + position);
            }
            else {
                convertView.setBackgroundColor(getResources().getColor(R.color.main_background));
                //Log.d(TAG, "position is: " + position);
            }

            Order o = getItem(position);

            TextView usernameTextView = (TextView)convertView.findViewById(R.id.order_list_item_usernameTextView);
            //Log.d(TAG, o.getUserId()+"");
            usernameTextView.setText(o.getUserInfo().getName());

            TextView distanceTextView = (TextView)convertView.findViewById(R.id.order_list_item_distanceTextView);
            distanceTextView.setText(MiscUtils.getDistanceDescription(o.getDistance()));

            TextView orderTypeTextView = (TextView)convertView.findViewById(R.id.order_list_item_orderTypeTextView);
            TextView submitTimeTextView = (TextView)convertView.findViewById(R.id.order_list_item_submitTimeTextView);

            TextView feeTextView = (TextView)convertView.findViewById(R.id.order_list_item_feeTextView);
            //Log.d(TAG, o.getSubmitTime().toString());
            if(o.getOrderType() == Order.ORDER_TYPE_APPOINTMENT) {
                ImageView point = (ImageView)convertView.findViewById(R.id.order_list_item_point_imageView);
                point.setImageResource(R.drawable.item_green240x240);

                submitTimeTextView.setText(MiscUtils.getTimeDiffDescription(o.getAppointmentTime(), new Date()) + "到达");
                orderTypeTextView.setText("预约停车");
                if(o.getFee() == 0 ) {
                    feeTextView.setText("—");
                    feeTextView.setTextColor(getResources().getColor(R.color.main_text_color));
                }else {
                    feeTextView.setText(o.getFee() + "元");
                }
            }
            else {
                submitTimeTextView.setText(MiscUtils.getTimeDiffDescription(o.getSubmitTime(), new Date()) + "下单");
                //submitTimeTextView.setTextColor(getResources().getColor(R.color.main_text_color));
                //submitTimeTextView.setTextSize("13sp");
                orderTypeTextView.setText("现在停车");
                feeTextView.setText("—");
                feeTextView.setTextColor(getResources().getColor(R.color.main_text_color));
            }


            //Log.d(TAG, o.getFee() + "");


            ImageButton confirmButton = (ImageButton)convertView.findViewById(R.id.order_list_item_confirmButton);
            confirmButton.setFocusable(false);
            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View parent = (View) v.getParent();
                    ListView listView = (ListView) parent.getParent();
                    final int position = listView.getPositionForView(parent);
                    //OrderAdapter adapter = (OrderAdapter)getListAdapter();
                    Order order = (Order)listView.getItemAtPosition(position);
                    TextView tv = (TextView)parent.findViewById(R.id.order_list_item_submitTimeTextView);
                    Log.d(TAG, "order :" + tv.getText() + "was clicked");
                    Log.d(TAG, "order :" + order.getFee() + "was clicked");

                    try {
                        ServerRequest request = new ServerRequest();
                        request.setString(JSONLabel.SESSION, CurrentUser.get(getActivity()).getSession());
                        request.setInt(JSONLabel.USERID, order.getUserId());
                        request.setInt(JSONLabel.ORDERID, order.getOrderId());
                        AsyncHttpClient client = new AsyncHttpClient();
                        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
                        client.get(request.confirmOrder(), new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                ServerRequest serverRequest = new ServerRequest();
                                try {
                                    serverRequest.setJSONRev(new String(responseBody));
                                    Log.d(TAG, "confirm Order success!");
                                    if (serverRequest.getStatusCode() == 0) {
                                        Toast.makeText(getContext(), R.string.result_succeeded, Toast.LENGTH_SHORT).show();
                                    }
                                    mListView.setRefreshing();
                                    refreshList();
                                } catch (Exception e) {
                                    Log.d(TAG, "Confirm order Error: " + e);
                                } finally {
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Log.d(TAG, "confirm Order failed!");
                            }

                            @Override
                            public void onFinish() {
                                super.onFinish();
                                //mSwipeRefreshLayout.setRefreshing(true);
                               // refreshOrderList(mSwipeRefreshLayout);
                            }
                        });
                        //task.confirmOrder();
                        //OrderList.get(getActivity()).downloadOrderList();
                        //adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        //
                    }
                }
            });

            return convertView;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);


        OrderList.get(getActivity()).downloadOrderList();

        mOrders = OrderList.get(getActivity()).getOrders();

        //OrderAdapter adapter = new OrderAdapter(mOrders);

        //setListAdapter(adapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_list_gai,container, false);
        mListView = (PullToRefreshListView)v.findViewById(R.id.order_list);

        View empty = v.findViewById(R.id.order_list_empty);
        mListView.setEmptyView(empty);

        View header = inflater.inflate(R.layout.header_view_order_list, null, false);

        header.setClickable(false);
        header.setFocusable(false);
        //View emptyView = v.findViewById(R.id.empty);
        //mListView.setEmptyView(emptyView);
        ListView lv = mListView.getRefreshableView();
        //mListView.addHeaderView(header);
        //lv.addHeaderView(header);
        mOrderAdapter = new OrderAdapter(mOrders);
        mListView.setAdapter(mOrderAdapter);
        mListView.getRefreshableView().addHeaderView(header);
        mListView.setScrollEmptyView(true);



        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView lv = (ListView) parent;

                if (position < lv.getHeaderViewsCount())
                    return;
                Order o = (Order) parent.getItemAtPosition(position);
                Log.d(TAG, "test test test");
                TextView time = (TextView) view.findViewById(R.id.order_list_item_submitTimeTextView);
                Log.d(TAG, "order :" + time.getText() + "was clicked");
            }
        });

        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshList();
            }
        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_query_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_qr_scan:
                IntentIntegrator integrator = IntentIntegrator.forFragment(this);
                //IntentIntegrator.forFragment(this).initiateScan();
//                IntentIntegrator integrator = new IntentIntegrator.forFragment(this);
                integrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
                integrator.setOrientationLocked(false);
                integrator.addExtra("PROMPT_MESSAGE", getString(R.string.scan_to_check));
                integrator.initiateScan();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            String contents = result.getContents();
            if (contents != null) {
                try {
                    HttpRequestTask task = new HttpRequestTask();
                    task.setString(JSONLabel.SESSION, CurrentUser.get(getActivity()).getSession());
                    task.setString(JSONLabel.QRCODE, result.getContents().toString());
                    task.checkQRCode();
                    int status = task.getStatusCode();
                    if(status == 0) {
                        Toast.makeText(getActivity(),
                                getString(R.string.qr_result_succeeded) + result.getContents().toString(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    //
                }
                Toast.makeText(getActivity(),
                        getString(R.string.qr_result_failed),
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, getString(R.string.result_succeeded) + result.getContents().toString());
//                Toast.makeText(getActivity(),
//                        getString(R.string.result_succeeded) + result.getContents().toString(),
//                        Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, getString(R.string.result_failed) );
                Toast.makeText(getActivity(),
                        getString(R.string.result_failed),
                        Toast.LENGTH_SHORT).show();
            }
        }
        //Log.d(TAG, "We get here?" );
    }

    class ListRefreshHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            Log.i(TAG, "info: " + (new String(responseBody)));
            ServerRequest serverRequest = new ServerRequest();
            try {
                serverRequest.setJSONRev(new String(responseBody));
                if (serverRequest.getStatusCode() == 0) {
                    OrderList.get(getActivity()).updateOrderList(serverRequest.getData());
                }
            } catch (Exception e){

            } finally {
                //((OrderAdapter) getListAdapter()).notifyDataSetChanged();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            //empty now
        }

        @Override
        public void onFinish() {
            super.onFinish();
            mListView.onRefreshComplete();
        }
    }

    private void refreshList() {
        try {
            ServerRequest serverRequest = new ServerRequest();
            serverRequest.setString(JSONLabel.SESSION,
                    CurrentUser.get(getActivity()).getSession());
            AsyncHttpClient client = new AsyncHttpClient();
            client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
            client.get(serverRequest.queryOrders(), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.i(TAG, "info: " + (new String(responseBody)));
                    ServerRequest serverRequest = new ServerRequest();
                    try {
                        serverRequest.setJSONRev(new String(responseBody));
                        if (serverRequest.getStatusCode() == 0) {
                            OrderList.get(getActivity()).updateOrderList(serverRequest.getData());
                        }
                    } catch (Exception e) {

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
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
