package com.corazza.fosco.lumenGame.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.corazza.fosco.lumenGame.R;
import com.corazza.fosco.lumenGame.helpers.Consts;
import com.corazza.fosco.lumenGame.helpers.Palette;
import com.corazza.fosco.lumenGame.schemes.SchemeInfo;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Simone on 14/05/2016.
 */
public class SchemeStickyListAdapter extends ArrayAdapter<SchemeInfo> implements StickyListHeadersAdapter {


    public SchemeStickyListAdapter(Context context, List<SchemeInfo> objects) {
        super(context, R.layout.scheme_sticky_adapter, objects);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder viewHolder;

        if (view == null) {
            view = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.scheme_sticky_adapter, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.itemView = (TextView) view.findViewById(R.id.nameTextView);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        SchemeInfo item = getItem(position);
        if (item!= null) {
            viewHolder.itemView.setText(item.getName());
            if(item.getResult() != null){
                viewHolder.itemView.setTextColor(Palette.get().getMain(Palette.Gradiation.NORMAL));
            } else {
                viewHolder.itemView.setTextColor(Palette.get().getAnti(Palette.Gradiation.LUMOUS));
            }
        }

        return view;
    }

    @Override
    public View getHeaderView(int i, View view, ViewGroup parent) {

        HeaderViewHolder viewHolder;

        if (view == null) {
            view = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.scheme_sticky_header_adapter, parent, false);

            viewHolder = new HeaderViewHolder();
            viewHolder.itemView = (TextView) view.findViewById(R.id.headerTextView);

            view.setTag(viewHolder);
        } else {
            viewHolder = (HeaderViewHolder) view.getTag();
        }

        SchemeInfo item = getItem(i);
        if (item != null) {
            viewHolder.itemView.setText(item.getSector());
        }

        return view;


    }




    @Override
    public long getHeaderId(int i) {
        SchemeInfo item = getItem(i);
        return item != null && item.getSector() != null ? item.getSector().hashCode() : 0;
    }

    public int getPositionFromCode(String code) {
        String myCode = "";
        int position = 0;
        while(!code.equals(myCode) && position < getCount()){
            SchemeInfo item = getItem(position);
            if(item != null) myCode = item.getCode();
            position++;
        }
        return position-1;
    }

    private static class ViewHolder {
        private TextView itemView;
    }

    private static class HeaderViewHolder {
        private TextView itemView;
    }
}
