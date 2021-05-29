package com.example.techassignment.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.example.techassignment.Models.Repository;
import com.example.techassignment.R;

import java.util.List;

public class RepositoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Repository> repositoryList;

    public RepositoryAdapter(List<Repository> repositoryList, Context context){
        this.repositoryList = repositoryList;
    }

    // ... constructor and member variables

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View repositoryView = inflater.inflate(R.layout.repository, parent, false);

        // Return a new holder instance
        CardViewHolder holder = new CardViewHolder(repositoryView);
        return holder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        initCardItem((CardViewHolder) holder,position);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        if(repositoryList == null)return 0;
        else return repositoryList.size();
    }

    private void initCardItem(CardViewHolder holder,int position){
        Repository repository = repositoryList.get(position);
        holder.author.setText(repository.getName());
        holder.repoName.setText(repository.getFullName());
        holder.description.setText(repository.getDescription());
//        languageColor.setCardBackgroundColor(Color.parseColor(repository.getLanguageColor()));
//        if(repository.getLanguage() != null && !repository.getLanguage().equals("null")){
//            language.setText(repository.getLanguage());
//        }else{
//            language.setText(R.string.not_available);
//        }

//        stars.setText(String.valueOf(repository.getS()));
//        forks.setText(String.valueOf(repository.getForks()));

        RequestOptions requestOptions = new RequestOptions().override(300, 300);
//        Glide.with(getApplicationContext()).asBitmap().apply(requestOptions).load(repository.getOwner().getAvatarUrl()).into(avatar);
    }

    public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        private TextView author;
        private TextView repoName;
        private TextView description;
        private ImageView avatar;
        private LinearLayout descriptionLayout;
        private CardView languageColor;
        private TextView language;
        private TextView stars;
        private TextView forks;

        public CardViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            author = itemView.findViewById(R.id.author);
            repoName = itemView.findViewById(R.id.repo_name);
            description = itemView.findViewById(R.id.description);
            avatar = itemView.findViewById(R.id.avatar);
            descriptionLayout = itemView.findViewById(R.id.description_layout);
            languageColor = itemView.findViewById(R.id.language_color);
            language = itemView.findViewById(R.id.language);
            stars = itemView.findViewById(R.id.stars);
            forks = itemView.findViewById(R.id.forks);
        }

        @Override
        public void onClick(View v) {
            if(descriptionLayout.getVisibility() == View.GONE){
                descriptionLayout.setVisibility(View.VISIBLE);
            }else{
                descriptionLayout.setVisibility(View.GONE);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }

    public void clear() {
        int size = repositoryList.size();
        repositoryList.clear();
        notifyItemRangeRemoved(0, size);
    }
}