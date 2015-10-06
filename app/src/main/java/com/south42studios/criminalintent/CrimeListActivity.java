package com.south42studios.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by Jsin on 8/30/2015.
 */
public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
