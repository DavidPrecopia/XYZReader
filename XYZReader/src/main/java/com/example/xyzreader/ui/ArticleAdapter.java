package com.example.xyzreader.ui;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xyzreader.R;
import com.example.xyzreader.databinding.ListItemArticleBinding;
import com.example.xyzreader.datamodel.Article;
import com.example.xyzreader.util.FormatDate;
import com.example.xyzreader.util.GlideApp;

import java.util.List;

final class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {


    interface OnClickListFragment {
        void openDetailFragment(int articleIndex);
    }


    private final List<Article> articlesList;

    private OnClickListFragment onClickListFragment;


    ArticleAdapter(List<Article> articlesList, OnClickListFragment onClickListFragment) {
        this.articlesList = articlesList;
        this.onClickListFragment = onClickListFragment;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArticleViewHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.getContext()), R.layout.list_item_article, parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        holder.bindView(
                articlesList.get(holder.getAdapterPosition())
        );
    }

    @Override
    public int getItemCount() {
        return articlesList.size();
    }


    class ArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ListItemArticleBinding binding;

        ArticleViewHolder(ListItemArticleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }


        private void bindView(Article article) {
            binding.setArticle(article);
            bindThumbnail(article.getThumbnailUrl());
            bindPublishedDate(article.getPublishedDate());
            binding.executePendingBindings();
        }

        private void bindThumbnail(String thumbnailUrl) {
            GlideApp.with(binding.ivThumbnail)
                    .load(thumbnailUrl)
                    .placeholder(R.drawable.ic_image_icon_black)
                    .error(R.drawable.ic_image_icon_black)
                    .into(binding.ivThumbnail);
        }

        private void bindPublishedDate(String publishedDate) {
            binding.publishedDate.setText(
                    FormatDate.getFormattedDate(publishedDate)
            );
        }


        @Override
        public void onClick(View v) {
            onClickListFragment.openDetailFragment(getAdapterPosition());
        }
    }
}