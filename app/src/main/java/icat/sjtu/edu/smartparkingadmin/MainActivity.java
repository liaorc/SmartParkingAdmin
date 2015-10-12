package icat.sjtu.edu.smartparkingadmin;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v13.app.FragmentTabHost;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Ruochen on 2015/9/22.
 */
public class MainActivity extends Activity {

    private FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(
                mTabHost.newTabSpec("List1").setIndicator("新订单", null),
                OrderListFragmentGai.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("List2").setIndicator("进行中", null),
                ConfirmedListFragmentGai.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("List3").setIndicator("已完成", null),
                CompletedListFragmentGai.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("UserInfo").setIndicator("用户", null),
                UserInfoFragment.class, null);

        ActionBar actionBar = getActionBar();
        //actionBar.setDisplayShowHomeEnabled(false);
        //actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        View v = getLayoutInflater().inflate(R.layout.actionbar_custom, null);
        actionBar.setCustomView(v, lp);
        //actionBar.setTitle("智能停车");
        //actionBar.setSubtitle("Inbox");
    }
}
