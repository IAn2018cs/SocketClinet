package cn.ian2018.socketclinet.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.ian2018.socketclinet.R;
import cn.ian2018.socketclinet.api.bean.GroupData;

/**
 * Created by chenshuai on 2020/8/14
 */
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private List<GroupData> list = new ArrayList<>();

    private OnItemClick onItemClick;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        GroupData groupData = list.get(position);
        holder.nameTv.setText("name: " + groupData.getGroupName());
        holder.ownerTv.setText("创建者: " + groupData.getOwner());
        holder.shareCodeTv.setText("share code: " + groupData.getShareCode());
        holder.itemView.setOnClickListener(v -> onItemClick.onItemClink(list.get(position)));
    }

    public void setList(List<GroupData> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTv;
        private TextView ownerTv;
        private TextView shareCodeTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.tv_name);
            ownerTv = itemView.findViewById(R.id.tv_owner);
            shareCodeTv = itemView.findViewById(R.id.tv_share_code);
        }
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public interface OnItemClick {
        void onItemClink(GroupData groupData);
    }
}
