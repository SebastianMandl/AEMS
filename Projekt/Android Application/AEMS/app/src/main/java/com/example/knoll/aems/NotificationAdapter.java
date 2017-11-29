package com.example.knoll.aems;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by knoll on 26.11.2017.
 */

public class NotificationAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> elemTitle;
    private ArrayList<String> elemInfo;
    private ArrayList<String> elemType;
    private ArrayList<Integer> elemPicture;

    public NotificationAdapter(Context context, ArrayList<String> elemTitle, ArrayList<String> elemInfo, ArrayList<String> elemType, ArrayList<Integer> elemPicture) {
        this.context = context;
        this.elemTitle = elemTitle;
        this.elemInfo = elemInfo;
        this.elemType = elemType;
        this.elemPicture = elemPicture;
    }


    @Override
    public int getCount() {
        return elemTitle.size();
    }

    @Override
    public Object getItem(int position) {
        return elemTitle.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = View.inflate(context, R.layout.notification_list_style, null);
        }

        ImageView images = (ImageView) convertView.findViewById(R.id.imageView);
        TextView title = (TextView) convertView.findViewById(R.id.textViewTitle);
        TextView info = (TextView) convertView.findViewById(R.id.textViewInformation);
        TextView notType = (TextView) convertView.findViewById(R.id.textViewNotificationType);

        images.setImageResource(elemPicture.get(position));
        title.setText(elemTitle.get(position));
        info.setText(elemInfo.get(position));
        notType.setText(elemType.get(position));

        return convertView;
    }
}
