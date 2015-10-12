package icat.sjtu.edu.smartparkingadmin;


import android.app.Fragment;

/**
 * Created by Ruochen on 2015/8/6.
 */
public class OrderListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new OrderListFragment();
    }

}
