package com.example.xyzreader.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xyzreader.R;
import com.example.xyzreader.databinding.FragmentViewPagerBinding;
import com.example.xyzreader.datamodel.Article;

import java.util.List;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ViewPagerFragment extends Fragment {

    private FragmentViewPagerBinding binding;

    private ArticleViewModel viewModel;

    public static final String ARGUMENT_ID_ARTICLE_INDEX = "article_index_id";


    public ViewPagerFragment() {
    }


    static ViewPagerFragment getInstance(int articleIndex) {
        ViewPagerFragment fragment = new ViewPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARGUMENT_ID_ARTICLE_INDEX, articleIndex);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_pager, container, false);

        init();

        return binding.getRoot();
    }

    private void init() {
        initViewModel();
        initViewPager();
    }

    private void initViewModel() {
        ArticleViewModelFactory factory = new ArticleViewModelFactory(getActivity().getApplication());
        viewModel = ViewModelProviders.of(getActivity(), factory).get(ArticleViewModel.class);
    }

    private void initViewPager() {
        ViewPager viewPager = binding.viewPager;

        MyPagerAdapter pagerAdapter = new MyPagerAdapter(
                getChildFragmentManager(),
                viewModel.getArticlesList().getValue()
        );

        viewPager.setAdapter(pagerAdapter);

        int index = getArguments().getInt(ARGUMENT_ID_ARTICLE_INDEX);
        viewPager.setCurrentItem(index, false);

        viewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.margin_normal));
        viewPager.setPageMarginDrawable(R.color.lightGray);
    }


    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        private final List<Article> articlesList;

        MyPagerAdapter(FragmentManager fm, List<Article> articlesList) {
            super(fm);
            this.articlesList = articlesList;
        }

        @Override
        public Fragment getItem(int position) {
            return DetailFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return articlesList.size();
        }
    }
}
