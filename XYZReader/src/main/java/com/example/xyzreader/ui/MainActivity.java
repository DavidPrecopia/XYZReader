package com.example.xyzreader.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.example.xyzreader.R;
import com.example.xyzreader.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity
        implements ListFragment.OnClickListFragment {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        init(savedInstanceState == null);
    }

    private void init(boolean newActivity) {
        if (newActivity) {
            addFragment(ListFragment.getInstance());
        }
    }


    @Override
    public void openDetailFragment(int articleIndex) {
        addFragment(DetailFragment.newInstance(articleIndex));
    }


    private void addFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(binding.fragmentHolder.getId(), fragment)
                .commit();
    }
}
