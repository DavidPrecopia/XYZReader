package com.example.xyzreader.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;

import com.example.xyzreader.R;
import com.example.xyzreader.databinding.ActivityArticleDetailBinding;
import com.example.xyzreader.datamodel.Article;

import java.util.List;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity {

    private ActivityArticleDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_article_detail);

//        initViewPager();
    }

//    private void initViewPager() {
//        ViewPager viewPager = binding.viewPager;
//
//        List<Article> articlesList = getIntent().getParcelableArrayListExtra(ArticleDetailActivity.class.getSimpleName() + "list");
//        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), articlesList);
//        viewPager.setAdapter(pagerAdapter);
//
//        int index = getIntent().getIntExtra(ArticleDetailActivity.class.getSimpleName() + "index", 0);
//        viewPager.setCurrentItem(index, false);
//
//        viewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.margin_normal));
//        viewPager.setPageMarginDrawable(R.color.lightGray);
//    }


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
