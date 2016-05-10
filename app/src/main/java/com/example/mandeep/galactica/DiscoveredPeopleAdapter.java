package com.example.mandeep.galactica;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by mandeep on 4/5/16.
 */
public class DiscoveredPeopleAdapter extends BaseAdapter {

    private static final String TAG = "DiscoveredPeopleAdapter";
    private final List<Person> people;
    private Context context;

    DiscoveredPeopleAdapter(Context context, List<Person> discovered){
        this.people = discovered;
        this.context = context;
    }

    @Override
    public int getCount() {
        return people.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.discovered_people_list_item, null);

        TextView name = (TextView) rowView.findViewById(R.id.discoveredName);
        TextView score = (TextView) rowView.findViewById(R.id.score);

        name.setText(people.get(position).getName());
        score.setText(String.valueOf(people.get(position).getScore()));

        return rowView;
    }
}
