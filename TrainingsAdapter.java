package fr.gerdevstudio.runningapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by NasTV on 05/01/2016.
 */
public class TrainingsAdapter extends ArrayAdapter<Training> {

    public TrainingsAdapter(Context context, int textViewResourceId, List<Training> items) {
        super(context, textViewResourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.listview_training_element, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.date = (TextView) convertView.findViewById(R.id.date);
            viewHolder.distance = (TextView) convertView.findViewById(R.id.distance);
            viewHolder.duree = (TextView) convertView.findViewById(R.id.duree);
            viewHolder.vitesse = (TextView) convertView.findViewById(R.id.vitesse);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Training item = getItem(position);
        if (item != null) {
            // Filling views of view holder
            viewHolder.date.setText(DateUtils.dateToString((item.getDate())));
            viewHolder.distance.setText(StringUtils.distanceToString(item.getDistance()));
            viewHolder.duree.setText(StringUtils.timeToString(item.getDuree()));
            viewHolder.vitesse.setText(StringUtils.speedToString(item.getVitesse()));
        }

        return convertView;
    }

    private static class ViewHolder {

        private TextView date;
        private TextView distance;
        private TextView duree;
        private TextView vitesse;
    }
}
