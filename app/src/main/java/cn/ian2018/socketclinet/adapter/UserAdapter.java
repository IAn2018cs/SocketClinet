package cn.ian2018.socketclinet.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.ian2018.socketclinet.AppContext;
import cn.ian2018.socketclinet.R;
import cn.ian2018.socketclinet.api.bean.UserData;
import cn.ian2018.socketclinet.util.SPUtil;

/**
 * Created by chenshuai on 2020/8/7
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<UserData> list = new ArrayList<>();

    private OnItemClick onItemClick;
    private Context context;

    public UserAdapter() {
        this.context = AppContext.context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        UserData userData = list.get(position);
        holder.textView.setText(userData.getUserId());
        holder.itemView.setOnClickListener(v -> onItemClick.onItemClink(list.get(position)));
    }

    public void setList(List<UserData> list) {
        this.list.clear();
        for (UserData userData : list) {
            if (TextUtils.equals(SPUtil.getId(context), userData.getUserId())) {
                continue;
            }
            this.list.add(userData);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_user);
        }
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public interface OnItemClick {
        void onItemClink(UserData userData);
    }
}
