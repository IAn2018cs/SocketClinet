package cn.ian2018.socketclinet.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.ian2018.socketclinet.R;
import cn.ian2018.socketclinet.db.data.MsgInfo;


/**
 * Created by Administrator on 2017/1/24/024.
 */

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {

    private List<MsgInfo> mMsgList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout liftLayout;
        private LinearLayout rightLayout;
        private TextView leftMsg;
        private TextView rightMsg;
        private TextView tv_left_info;
        private TextView tv_right_info;

        public ViewHolder(View itemView) {
            super(itemView);
            liftLayout = (LinearLayout) itemView.findViewById(R.id.left_layout);
            rightLayout = (LinearLayout) itemView.findViewById(R.id.right_layout);
            leftMsg = (TextView) itemView.findViewById(R.id.left_msg);
            rightMsg = (TextView) itemView.findViewById(R.id.right_msg);
            tv_left_info = (TextView) itemView.findViewById(R.id.tv_left_info);
            tv_right_info = (TextView) itemView.findViewById(R.id.tv_right_info);
        }
    }

    public MsgAdapter(List<MsgInfo> msgList) {
        mMsgList = msgList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MsgInfo msg = mMsgList.get(position);
        // 如果是收到消息 就显示左边的布局 隐藏右边的布局
        if (msg.getType() == MsgInfo.TYPE_RECEIVED) {
            holder.liftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);

            holder.leftMsg.setText(msg.getMsg());
            holder.tv_left_info.setText(msg.getIp() + "  " + msg.getFormatTime());
        }
        // 如果是发送消息 就显示右边的布局 隐藏左边的布局
        if (msg.getType() == MsgInfo.TYPE_SEND) {
            holder.liftLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);

            holder.rightMsg.setText(msg.getMsg());
            holder.tv_right_info.setText(msg.getIp() + "  " + msg.getFormatTime());
        }
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }
}
