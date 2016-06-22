/*
 *     Copyright (c) 2015 GuDong
 *
 *     Permission is hereby granted, free of charge, to any person obtaining a copy
 *     of this software and associated documentation files (the "Software"), to deal
 *     in the Software without restriction, including without limitation the rights
 *     to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *     copies of the Software, and to permit persons to whom the Software is
 *     furnished to do so, subject to the following conditions:
 *
 *     The above copyright notice and this permission notice shall be included in all
 *     copies or substantial portions of the Software.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *     IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *     FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *     AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *     LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *     OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *     SOFTWARE.
 */

package com.gudong.appkit.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gudong.appkit.R;
import com.gudong.appkit.dao.AppEntity;
import com.gudong.appkit.utils.FormatUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by mao on 15/7/8.
 */
public class AppInfoListAdapter extends RecyclerView.Adapter<AppInfoListAdapter.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {
    private List<AppEntity> mListData;
    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private Context mContext;
    private boolean isBrief = true;

    private IClickPopupMenuItem mClickPopupMenuItem;
    private IClickListItem mClickListItem;

    public AppInfoListAdapter(Context context,List<AppEntity> listData,boolean isBrief) {
        this(context,isBrief);
        this.mListData = listData;

    }

    public AppInfoListAdapter(Context context,boolean isBrief){
        mContext = context;
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        this.mListData = new ArrayList<>();
        this.isBrief = isBrief;
    }

    public void update(List<AppEntity> listData){
       if(this.mListData.isEmpty()){
           this.mListData.addAll(listData);
           notifyItemRangeInserted(0,listData.size());
       }else{
           this.mListData.clear();
           this.mListData.addAll(listData);
           //notifyItemRangeChanged(0,listData.size());
           notifyDataSetChanged();
       }
    }

    public void update(AppEntity entity){
        int index = getListData().indexOf(entity);
        if(index>0){
            getListData().remove(index);
            getListData().add(index,entity);
            notifyDataSetChanged();
        }
    }

    public void clear(){
        notifyItemRangeRemoved(0,mListData.size());
        this.mListData.clear();
    }

    public void addItem(int position,AppEntity entity){
        this.mListData.add(position,entity);
        notifyItemInserted(position);
    }

    public void removeItem(AppEntity entity){
        int position = mListData.indexOf(entity);
        if(position >=0 ){
            this.mListData.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void setBriefMode(boolean isBrief){
        this.isBrief = isBrief;
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
        final AppEntity entity = mListData.get(position);
        if(entity == null)return;

        Bitmap bitmap = BitmapFactory.decodeByteArray(entity.getAppIconData(),0,entity.getAppIconData().length);
        holder.ivIcon.setImageBitmap(bitmap);
        holder.tvName.setText(entity.getAppName());

        holder.tvVersion.setVisibility(isBrief?View.GONE:View.VISIBLE);
        holder.tvPackName.setVisibility(isBrief?View.GONE:View.VISIBLE);

        if(!isBrief){
            holder.tvVersion.setText(FormatUtil.formatVersionName(entity));
            holder.tvPackName.setText(entity.getPackageName());
        }


        holder.ivIcon.setOnClickListener(this);
        holder.ivIcon.setTag(entity);
        holder.view.setOnClickListener(this);
        holder.view.setTag(entity);
        holder.ivOverFlow.setOnClickListener(this);
        holder.ivOverFlow.setTag(entity);
        holder.rlRoot.setOnLongClickListener(this);
        holder.rlRoot.setTag(entity);
    }

    private void showBriefWithAnim(){}

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
            case R.id.rl_item:
                if (mClickListItem == null) return;
                mClickListItem.onClickListItemContent(v,entity);
                break;
            case R.id.iv_over_flow:
                showPopMenu(entity, v);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        AppEntity entity = (AppEntity) v.getTag();
        switch (v.getId()){
            case R.id.rl_item:
                mClickListItem.onLongClickListItem(v,entity);
                break;
        }
        return false;
    }

    public interface IClickPopupMenuItem{
        void onClickMenuItem(int itemId,AppEntity entity);
    }

    public interface IClickListItem{
        void onClickListItemContent(View view,AppEntity entity);
        void onClickListItemIcon(View iconView,AppEntity entity);
        void onLongClickListItem(View iconView,AppEntity entity);
    }


    @Override
    public int getItemCount() {
        return mListData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public View view;
        public ImageView ivIcon;
        public RelativeLayout rlRoot;
        public LinearLayout llAppInfo;
        public ImageView ivOverFlow;
        private TextView tvName;
        private TextView tvVersion;
        private TextView tvPackName;
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
            rlRoot = (RelativeLayout) view.findViewById(R.id.rl_item);
            llAppInfo = (LinearLayout) view.findViewById(R.id.ll_app_info);
            ivOverFlow = (ImageView) view.findViewById(R.id.iv_over_flow);
            tvName = (TextView) view.findViewById(android.R.id.text1);
            tvVersion = (TextView) view.findViewById(R.id.tv_version);
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
        return mListData;
    }

}
