package com.hammersmith.thetinhluok.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.net.ParseException;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hammersmith.thetinhluok.ApiClient;
import com.hammersmith.thetinhluok.ApiInterface;
import com.hammersmith.thetinhluok.R;
import com.hammersmith.thetinhluok.model.Comment;
import com.hammersmith.thetinhluok.model.Reply;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Chan Thuon on 9/12/2016.
 */
public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.MyViewHolder> {
    private List<Reply> replies;
    private Context context;
    private Activity activity;
    private static String today;

    public ReplyAdapter(Activity activity, List<Reply> replies) {
        this.activity = activity;
        this.replies = replies;
        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public ReplyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reply_list_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(ReplyAdapter.MyViewHolder holder, final int position) {
        final Uri uri = Uri.parse(replies.get(position).getProfile());
        context = holder.profile.getContext();
        Picasso.with(context).load(uri).into(holder.profile);
        holder.name.setText(replies.get(position).getName());
        holder.comment.setText(replies.get(position).getReply());
        holder.createAt.setText(getTimeStamp(replies.get(position).getCreateAt()));

    }

    @Override
    public int getItemCount() {
        return replies.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView profile, profileReply;
        TextView name, comment, createAt, nameReply, reply;
        LinearLayout lReply;

        public MyViewHolder(View itemView) {
            super(itemView);
            profile = (ImageView) itemView.findViewById(R.id.profile);
            profileReply = (ImageView) itemView.findViewById(R.id.profileReply);
            name = (TextView) itemView.findViewById(R.id.name);
            comment = (TextView) itemView.findViewById(R.id.comment);
            createAt = (TextView) itemView.findViewById(R.id.createdAt);
            nameReply = (TextView) itemView.findViewById(R.id.nameReply);
            reply = (TextView) itemView.findViewById(R.id.reply);
            lReply = (LinearLayout) itemView.findViewById(R.id.lReply);
        }
    }

    public static String getTimeStamp(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = "";

        today = today.length() < 2 ? "0" + today : today;

        try {
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(date);
            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
            String date1 = format.format(date);
            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }

}