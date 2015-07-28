package com.aset.dey.peptalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by dey on 29-Jul-15.
 */
public class MessageAdaptor extends ArrayAdapter<ParseObject> {
    protected Context mContext;
    protected List<ParseObject> mMessages;
    public MessageAdaptor(Context context,List<ParseObject> messages)
    {super(context,R.layout.message_item,messages);
        mContext = context;
        mMessages= messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);//Layout inflater converts xml to views in android
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.messageIcon);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.senderLabel);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }
        ParseObject message = mMessages.get(position);
        if(message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)) {
            holder.iconImageView.setImageResource(R.drawable.ic_action_photo);
        }
        else
        {
            holder.iconImageView.setImageResource(R.drawable.ic_action_name);
        }
        holder.nameLabel.setText(message.getString(ParseConstants.KEY_SENDER_NAME));
            return null;
    }
   public static class ViewHolder//holds data to be displayed in custom layout
   {
       ImageView iconImageView;
       TextView nameLabel;

   }
}
