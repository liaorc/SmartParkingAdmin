package icat.sjtu.edu.smartparkingadmin;


import android.app.Fragment;

/**
 * Created by Ruochen on 2015/8/4.
 */
public class LoginActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new LoginFragment();
    }

}
