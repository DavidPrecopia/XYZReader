package com.example.xyzreader.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.xyzreader.R;
import com.example.xyzreader.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity
        implements ListFragment.OnClickListFragment {

    private ActivityMainBinding binding;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        init(savedInstanceState == null);
    }

    private void init(boolean newActivity) {
        fragmentManager = getSupportFragmentManager();

        if (newActivity) {
            addFragment(ListFragment.getInstance());
        }
    }


    @Override
    public void openDetailFragment(int articleIndex) {
//        Fragment fragment = getResources().getBoolean(R.bool.is_master_detail_layout)
//                ? DetailFragment.newInstance(articleIndex)
//                : DetailViewPagerFragment.getInstance(articleIndex);
        addFragment(DetailFragment.newInstance(articleIndex));
    }


    private void addFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(binding.fragmentHolder.getId(), fragment)
                .addToBackStack(null)
                .commit();
    }

//    /**
//     * @return true if Up navigation completed successfully <b>and</b> this Activity was finished, false otherwise.
//     */
//    @Override
//    public boolean onSupportNavigateUp() {
//        if (fragmentManager.getBackStackEntryCount() > 0) {
//            fragmentManager.popBackStack();
//            return false;
//        } else {
//            return super.onSupportNavigateUp();
//        }
//    }
}
