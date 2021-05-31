package com.example.techassignment.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.techassignment.Models.Repository;
import com.example.techassignment.R;

import java.util.List;

public class RepositoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Repository> repositoryList;
    private Context context;
    public RepositoryAdapter(List<Repository> repositoryList, Context context){
        this.repositoryList = repositoryList;
        this.context = context;
    }

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

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        initCardItem((CardViewHolder) holder,position);
    }


    @Override
    public int getItemCount() {
        if(repositoryList == null)return 0;
        else return repositoryList.size();
    }

    private void initCardItem(final CardViewHolder holder, final int position){
        Repository repository = repositoryList.get(position);
        holder.author.setText(repository.getName());
        holder.repoName.setText(repository.getFullName());
        holder.description.setText(repository.getDescription());
        if(repository.getLanguage() != null && !repository.getLanguage().equals("null")){
            holder.language.setText(repository.getLanguage());
        }else{
            holder.language.setText(R.string.not_available);
        }

        holder.stars.setText(String.valueOf(repository.getStargazersCount()));
        holder.forks.setText(String.valueOf(repository.getForks()));

        RequestOptions requestOptions = new RequestOptions().override(300, 300);
        Glide.with(context).asBitmap().apply(requestOptions).load(repository.getOwner().getAvatarUrl()).into(holder.avatar);

        if(repositoryList.get(position).isExpended()){
            holder.descriptionLayout.setVisibility(View.VISIBLE);
        }else{
            holder.descriptionLayout.setVisibility(View.GONE);
        }

        holder.repository.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(repositoryList.get(position).isExpended()){
                    holder.descriptionLayout.setVisibility(View.GONE);
                    repositoryList.get(position).setExpended(false);
                }else{
                    holder.descriptionLayout.setVisibility(View.VISIBLE);
                    repositoryList.get(position).setExpended(true);
                }
            }
        });
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout repository;
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

            repository = itemView.findViewById(R.id.repository);
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

    }
    public void clear() {
        int size = repositoryList.size();
        repositoryList.clear();
        notifyItemRangeRemoved(0, size);
    }
}