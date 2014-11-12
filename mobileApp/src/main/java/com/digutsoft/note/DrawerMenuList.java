package com.digutsoft.note;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

class DrawerMenuList {
    DrawerMenuList(String title, Drawable icon) {
        this.title = title;
        this.icon = icon;
    }

    String title;
    Drawable icon;
}

class DrawerMenuListAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<DrawerMenuList> alDrawerList;

    public DrawerMenuListAdapter(Context mContext, ArrayList<DrawerMenuList> alDrawerList) {
        this.mContext = mContext;
        this.alDrawerList = alDrawerList;
    }

    public int getCount() {
        return alDrawerList.size();
    }

    public Object getItem(int position) {
        return alDrawerList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.drawer_menu, parent, false);

            TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            ImageView ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);

            tvTitle.setText(alDrawerList.get(position).title);
            ivIcon.setImageDrawable(alDrawerList.get(position).icon);
        }

        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }
}