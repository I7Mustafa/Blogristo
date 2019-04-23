package com.example.firebase.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.firebase.Activities.PostDetailsActivity;
import com.example.firebase.Models.Post;
import com.example.firebase.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.myViewHolder> {

    private Context context ;
    private List<Post> mData ;

    public PostAdapter(Context context, List<Post> mData) {
        this.context = context;
        this.mData = mData;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View row = LayoutInflater.from(context).inflate(R.layout.row_post_item , viewGroup , false);
        return new myViewHolder(row);

    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder myViewHolder, int i) {

        myViewHolder.tvTitle.setText(mData.get(i).getTitle());
        Glide.with(context).load(mData.get(i).getPicture()).into(myViewHolder.imgPost);
        Glide.with(context).load(mData.get(i).getUserPhoto()).into(myViewHolder.imgPostProfile);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    class myViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle;
        ImageView imgPost , imgPostProfile;

        myViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.row_Text_Title);
            imgPost = itemView.findViewById(R.id.row_Post_Image);
            imgPostProfile = itemView.findViewById(R.id.row_UserImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent postDetailsActivity = new Intent(context , PostDetailsActivity.class);
                    int position = getAdapterPosition();

                    postDetailsActivity.putExtra("title" , mData.get(position).getTitle());
                    postDetailsActivity.putExtra("PostImage" , mData.get(position).getPicture());
                    postDetailsActivity.putExtra("Description" , mData.get(position).getDescription());
                    postDetailsActivity.putExtra("PostKey" , mData.get(position).getPostKey());
                    postDetailsActivity.putExtra("userPhoto" , mData.get(position).getUserPhoto());
                    long timestamp = (long) mData.get(position).getTimeStamp();
                    postDetailsActivity.putExtra("postDate" , timestamp);
                    context.startActivity(postDetailsActivity);

                }
            });
        }
    }
}