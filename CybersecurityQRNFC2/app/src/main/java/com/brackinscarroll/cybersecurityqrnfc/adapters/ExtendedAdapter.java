package com.brackinscarroll.cybersecurityqrnfc.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Julian on 4/12/2015.
 */
public class ExtendedAdapter extends BaseAdapter
{

    private LayoutInflater _layoutInflater;
    private List<String> _messages = new ArrayList<String>(100);
    private int _messageCount;

    public ExtendedAdapter(LayoutInflater layoutInflater) {
        this._layoutInflater = layoutInflater;
    }

    public void addMessage(String message) {
        _messageCount++;
        _messages.add( "Message [" + _messageCount + "]: " + message );
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return _messages == null ? 0 : _messages.size();
    }

    @Override
    public Object getItem(int position) {
        return _messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = _layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        TextView view = (TextView)convertView.findViewById(android.R.id.text1);
        view.setText((CharSequence)getItem(position));
        return convertView;
    }
}
