package com.gudong.appkit.adapter;

import android.content.Context;
import android.support.v7.internal.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gudong.appkit.R;
import com.gudong.appkit.entity.AppEntity;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by mao on 15/7/8.
 */
public class AppInfoListAdapter extends RecyclerView.Adapter<AppInfoListAdapter.ViewHolder> implements View.OnClickListener {

    private List<AppEntity>listData;
    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private Context mContext;

    private IClickPopupMenuItem mClickPopupMenuItem;
    private IClickListItem mClickListItem;

    public AppInfoListAdapter(Context context,List<AppEntity> listData) {
        mContext = context;
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        this.listData = listData;
    }

    public void update(List<AppEntity> listData){
        this.listData = listData;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        view.setBackgroundResource(mBackground);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final AppEntity entity = listData.get(position);
        if(entity == null)return;

        holder.ivIcon.setImageBitmap(entity.getAppIcon());
        holder.tvName.setText(entity.getAppName());
        holder.tvPackName.setText(entity.getPackageName());

        holder.ivIcon.setOnClickListener(this);
        holder.ivIcon.setTag(entity);
        holder.llAppInfo.setOnClickListener(this);
        holder.llAppInfo.setTag(entity);
        holder.ivOverFlow.setOnClickListener(this);
        holder.ivOverFlow.setTag(entity);
    }

    /**
     * 显示弹出式菜单
     * @param entity
     * @param ancho
     */
    private void showPopMenu(final AppEntity entity,View ancho) {
        PopupMenu popupMenu = new PopupMenu(mContext,ancho);
        popupMenu.getMenuInflater().inflate(R.menu.item_pop_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(mClickPopupMenuItem!=null){
                    mClickPopupMenuItem.onClickMenuItem(item.getItemId(),entity);
                }
                return false;
            }
        });

        makePopForceShowIcon(popupMenu);

        popupMenu.show();
    }

    //使用反射让popupMenu 显示菜单icon
    private void makePopForceShowIcon(PopupMenu popupMenu) {
        try {
            Field mFieldPopup=popupMenu.getClass().getDeclaredField("mPopup");
            mFieldPopup.setAccessible(true);
            MenuPopupHelper mPopup = (MenuPopupHelper) mFieldPopup.get(popupMenu);
            mPopup.setForceShowIcon(true);
        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View v) {
        AppEntity entity = (AppEntity) v.getTag();
        switch (v.getId()){
            case R.id.iv_icon:
                if (mClickListItem == null) return;
                mClickListItem.onClickListItemIcon(v,entity);
                break;
            case R.id.ll_app_info:
                if (mClickListItem == null) return;
                mClickListItem.onClickListItemContent(entity);
                break;
            case R.id.iv_over_flow:
                showPopMenu(entity, v);
                break;
        }
    }

    public interface IClickPopupMenuItem{
        void onClickMenuItem(int itemId,AppEntity entity);
    }

    public interface IClickListItem{
        void onClickListItemContent(AppEntity entity);
        void onClickListItemIcon(View iconView,AppEntity entity);
    }


    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public View view;
        public ImageView ivIcon;
        public LinearLayout llAppInfo;
        public ImageView ivOverFlow;
        private TextView tvName;
        private TextView tvPackName;
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
            llAppInfo = (LinearLayout) view.findViewById(R.id.ll_app_info);
            ivOverFlow = (ImageView) view.findViewById(R.id.iv_over_flow);
            tvName = (TextView) view.findViewById(android.R.id.text1);
            tvPackName = (TextView) view.findViewById(android.R.id.text2);
        }
    }

    public void setClickPopupMenuItem(IClickPopupMenuItem mClickPopupMenuItem) {
        this.mClickPopupMenuItem = mClickPopupMenuItem;
    }

    public void setClickListItem(IClickListItem mClickListItem) {
        this.mClickListItem = mClickListItem;
    }

    public List<AppEntity>getListData(){
        return listData;
    }

}
