package Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import commonObj.chatMsgObj;
import commonObj.userObj;
import emotiontest.zhh.rxjavachatroom.R;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    ArrayList<chatMsgObj> chatModelList;

    public ChatAdapter(ArrayList<chatMsgObj> dataList){
        this.chatModelList = dataList;
    }


    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        userObj mUser = userObj.getInstance();
        chatMsgObj chatModel = chatModelList.get(position);
        if(chatModel.getUserId()!=mUser.getId()){
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.leftNameTextView.setVisibility(View.VISIBLE);
            holder.leftContentTextView.setVisibility(View.VISIBLE);
            holder.leftNameTextView.setText(Integer.toString(chatModel.getUserId()));
            holder.leftContentTextView.setText(chatModel.getMsg());
        }else {
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.rightNameTextView.setVisibility(View.VISIBLE);
            holder.rightContentTextView.setVisibility(View.VISIBLE);
            holder.rightNameTextView.setText(Integer.toString(chatModel.getUserId()));
            holder.rightContentTextView.setText(chatModel.getMsg());
        }
    }

    @Override
    public int getItemCount() {
        return chatModelList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView leftNameTextView;
        TextView leftContentTextView;
        LinearLayout leftLayout;

        TextView rightNameTextView;
        TextView rightContentTextView;
        LinearLayout rightLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            leftNameTextView = (TextView) itemView.findViewById(R.id.left_name);
            leftContentTextView = (TextView) itemView.findViewById(R.id.left_content);
            leftLayout = (LinearLayout) itemView.findViewById(R.id.leftChatFace);

            rightNameTextView = (TextView) itemView.findViewById(R.id.right_name);
            rightContentTextView = (TextView) itemView.findViewById(R.id.right_content);
            rightLayout = (LinearLayout) itemView.findViewById(R.id.rightChatFace);
        }
    }

}
