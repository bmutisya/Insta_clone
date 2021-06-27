package com.example.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.CommentActivity;
import com.example.myapplication.Model.Post;
import com.example.myapplication.Model.User;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PostAdapter extends  RecyclerView.Adapter<PostAdapter.Viewholder> {

    private Context mContext;
    private List<Post> mPost;
    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @NotNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.Viewholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PostAdapter.Viewholder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        Post post = mPost.get(position);
        Picasso.get().load(post.getImageurl()).into(holder.postImage);
        holder.description.setText(post.getDescription());

        FirebaseDatabase.getInstance().getReference().child("users").child(post.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user.getImageurl().equals("default")) {
                    holder.imageProfile.setImageResource(R.mipmap.ic_launcher);
                } else {

                    Picasso.get().load(user.getImageurl()).placeholder(R.mipmap.ic_launcher).into(holder.imageProfile);
                }
                holder.username.setText(user.getUsername());
                holder.author.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        //isLiked(post.getPostid(),holder.like);


        //noOfLikes(post.getPostid(), holder.noOfLikes);
        //getComments(post.getPostid(),holder.noOfComments);



         holder.like.setOnClickListener(new View.OnClickListener() {
          @Override
           public void onClick(View v) {
                if (holder.like.getTag().equals("like")) {
                    //firebase querry so as to like the image
               FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);
              } else {
                    //user has already liked the image so dislike
                 FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid()).child(firebaseUser.getUid()).removeValue();

              }

            }
        });
         holder.comment.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent =new Intent(mContext, CommentActivity.class);
                 intent.putExtra("postId",post.getPostid());
                 intent.putExtra("authorId",post.getPublisher());
                 mContext.startActivity(intent);
             }
         });
         holder.noOfComments.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent =new Intent(mContext, CommentActivity.class);
                 intent.putExtra("postId",post.getPostid());
                 intent.putExtra("authorId",post.getPublisher());
                 mContext.startActivity(intent);

             }
         });


    }

    @Override
    public int getItemCount() {

        return mPost.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        public ImageView imageProfile;
        public ImageView postImage;
        public ImageView like;
        public ImageView comment;
        public ImageView save;
        public ImageView more;
        public TextView username;
        public TextView noOfLikes;
        public TextView author;
        public TextView noOfComments;
        SocialTextView description;

        public Viewholder(@NonNull @NotNull View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.image_profile);
            postImage = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            more = itemView.findViewById(R.id.more);
            username = itemView.findViewById(R.id.username);

            noOfLikes = itemView.findViewById(R.id.no_of_likes);
            author = itemView.findViewById(R.id.author);
            noOfComments = itemView.findViewById(R.id.no_of_comments);
            description = itemView.findViewById(R.id.description);
        }
    }

    //method to check if the post is liked
  private void isLiked(String postId, final ImageView imageView) {

      //inside the Likes  look for the post with the postId.
      FirebaseDatabase.getInstance().getReference().child("Likes").child (postId).addValueEventListener(new ValueEventListener() {

          @Override
          public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
              if(snapshot.child(firebaseUser.getUid()).exists()) { //if means it has already been liked by the user
                  imageView.setImageResource(R.drawable.ic_liked);// change the like image to a red heart
                  imageView.setTag("liked");// change
              } else {
                  imageView.setImageResource(R.drawable.ic_like);
                  imageView.setTag("like");
              }

          }

          @Override
          public void onCancelled(@NonNull @NotNull DatabaseError error) {

          }
      });

    }
    private void noOfLikes(String postId, final TextView text){
        FirebaseDatabase.getInstance().getReference().child("Likes").child ( postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                text.setText(snapshot.getChildrenCount() + "Likes");

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
    private  void getComments (String postId,final TextView text){

        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                text.setText("View All" + snapshot.getChildrenCount() + "Comments");
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }
}