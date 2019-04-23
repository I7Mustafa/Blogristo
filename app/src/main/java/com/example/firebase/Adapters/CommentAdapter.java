package com.example.firebase.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.firebase.Models.Comment;
import com.example.firebase.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CoViewHolder> {

    private Context context ;
    private List<Comment> cData;

    public CommentAdapter (Context context, List<Comment> commentList) {
        this.context = context;
        this.cData = commentList;
    }

    @NonNull
    @Override
    public CoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View row = LayoutInflater.from(context).inflate(R.layout.row_post_comment , viewGroup ,false);
        return new CoViewHolder(row);

    }

    @Override
    public void onBindViewHolder(@NonNull CoViewHolder coViewHolder, int i) {

        coViewHolder.tvComment_UserName.setText(cData.get(i).getUname());
        coViewHolder.tvComment.setText(cData.get(i).getContent());
        Glide.with(context).load(cData.get(i).getUimg()).into(coViewHolder.ivComment_UserImage);

    }

    @Override
    public int getItemCount() {
        return cData.size();
    }

    class CoViewHolder extends RecyclerView.ViewHolder {

        TextView tvComment_UserName , tvComment , tvComment_Time ;
        ImageView ivComment_UserImage ;

        CoViewHolder(@NonNull View itemView) {
            super(itemView);

            tvComment = itemView.findViewById(R.id.tvComment);
            tvComment_UserName = itemView.findViewById(R.id.tvComment_UserName);
            tvComment_Time = itemView.findViewById(R.id.tvComment_Time);

            ivComment_UserImage = itemView.findViewById(R.id.ivComment_userPhoto);
        }
    }

}
