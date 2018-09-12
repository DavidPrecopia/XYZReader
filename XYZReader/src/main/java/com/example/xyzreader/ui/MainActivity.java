package com.example.xyzreader.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.xyzreader.R;
import com.example.xyzreader.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity
        implements ListFragment.OnClickListFragment {

    private ActivityMainBinding binding;
    private FragmentManager fragmentManager;

    private static final int INVALID_INDEX = -1;
    // This is static to ensure its value does not reset on rotation
    private static int articleIndex = INVALID_INDEX;

    private int singlePaneHolderId;

    private boolean masterDetailLayout;
    private int masterHolderId;
    private int detailHolderId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        init(savedInstanceState == null);
    }

    private void init(boolean newActivity) {
        initFields();
        initLayout(newActivity);
    }

    private void initFields() {
        fragmentManager = getSupportFragmentManager();
        masterDetailLayout = getResources().getBoolean(R.bool.is_master_detail_layout);
        if (masterDetailLayout) {
            masterHolderId = binding.holderMaster.getId();
            detailHolderId = binding.holderDetail.getId();
        } else {
            singlePaneHolderId = binding.fragmentHolder.getId();
        }
    }


    private void initLayout(boolean newActivity) {
        if (isSinglePaneAndNew(newActivity)) {
            initSinglePaneLayout();
        } else if (masterDetailLayout) {
            initDualPaneLayout();
        } else if (! newActivity) {
            initDualToSinglePaneLayout();
        }
    }

    private void initSinglePaneLayout() {
        addFragmentNoBackstack(singlePaneHolderId, ListFragment.getInstance());
    }

    private void initDualPaneLayout() {
        addFragmentNoBackstack(masterHolderId, ListFragment.getInstance());
        if (articleIndex != INVALID_INDEX) {
            openDetailFragment(articleIndex);
        }
    }

    private void initDualToSinglePaneLayout() {
        if (articleIndex != INVALID_INDEX) {
            openDetailFragment(articleIndex);
        } else {
            initSinglePaneLayout();
        }
    }


    @Override
    public void openDetailFragment(int articleIndex) {
        MainActivity.articleIndex = articleIndex;
        if (masterDetailLayout) {
            binding.tvHolderDetailMessage.setVisibility(View.GONE);
            addFragmentNoBackstack(detailHolderId, DetailFragment.newInstance(articleIndex));
        } else {
            addFragment(singlePaneHolderId, DetailFragment.newInstance(articleIndex));
        }
    }


    private void addFragmentNoBackstack(int viewId, Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(viewId, fragment)
                .commit();
    }

    private void addFragment(int viewId, Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(viewId, fragment)
                .addToBackStack(null)
                .commit();
    }


    private boolean isSinglePaneAndNew(boolean newActivity) {
        return ! masterDetailLayout && newActivity;
    }


    /**
     * @return true if Up navigation completed successfully <b>and</b> this Activity was finished, false otherwise.
     */
    @Override
    public boolean onSupportNavigateUp() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
            return false;
        } else {
            return super.onSupportNavigateUp();
        }
    }
}
