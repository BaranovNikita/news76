package ru.krista.nbaranov.news76;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.krista.nbaranov.news76.helpers.Utils;


public class NewsAdapter extends ArrayAdapter<News> {
    private static class ViewHolder {
        TextView title;
        TextView date;
    }

    public NewsAdapter(Context context, ArrayList<News> users) {
        super(context, R.layout.newslistrow, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        News news = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.newslistrow, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.itemTitle);
            viewHolder.date = (TextView) convertView.findViewById(R.id.itemDate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(news.getTitle());
        viewHolder.date.setText(news.getDate());
        return convertView;
    }


}
