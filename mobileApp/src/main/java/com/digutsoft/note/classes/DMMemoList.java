package com.digutsoft.note.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.digutsoft.note.DMMemoView;
import com.digutsoft.note.R;

import java.util.ArrayList;

public class DMMemoList {
    public int mId;
    public String mMemoTitle, mMemoContent;
    public boolean mCheckedStatus;

    public DMMemoList(int id, String memoTitle, String memoContent, boolean checkedStatus) {
        mId = id;
        mMemoTitle = memoTitle;
        mMemoContent = memoContent;
        mCheckedStatus = checkedStatus;
    }

    public static class DMMemoListViewHolder {
        CheckBox checkBox;
        TextView textView;
    }

    public static class DMMemoListAdapter extends BaseAdapter {

        Context mContext;
        ArrayList<DMMemoList> alMemoList;

        SharedPreferences sharedPreferences;
        boolean isCheckboxEnabled;

        final int _8dp;

        public DMMemoListAdapter(Context mContext, ArrayList<DMMemoList> alMemoList) {
            this.mContext = mContext;
            this.alMemoList = alMemoList;

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            isCheckboxEnabled = sharedPreferences.getBoolean("st_use_checklist", false);

            final float scale = mContext.getResources().getDisplayMetrics().density;
            _8dp = (int) (8 * scale + 0.5f);
        }

        @Override
        public int getCount() {
            return alMemoList.size();
        }

        @Override
        public Object getItem(int position) {
            return alMemoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final DMMemoListViewHolder memoListViewHolder;
            if (convertView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.list_memo, parent, false);

                memoListViewHolder = new DMMemoListViewHolder();
                memoListViewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
                memoListViewHolder.textView = (TextView) convertView.findViewById(R.id.textView);

                convertView.setTag(memoListViewHolder);
            } else {
                memoListViewHolder = (DMMemoListViewHolder) convertView.getTag();
            }

            DMMemoList list = alMemoList.get(position);

            if (list != null) {
                if (isCheckboxEnabled) {
                    memoListViewHolder.checkBox.setChecked(alMemoList.get(position).mCheckedStatus);
                    memoListViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            DMMemoTools.checkMemo(mContext, DMMemoView.mCategoryName, DMMemoView.getMemoId(position), b);
                        }
                    });
                } else {
                    memoListViewHolder.checkBox.setVisibility(View.GONE);
                    memoListViewHolder.textView.setPadding(_8dp, 0, _8dp, 0);
                }
                memoListViewHolder.textView.setText(alMemoList.get(position).mMemoTitle);
            }

            return convertView;
        }
    }
}