package com.example.mandeep.galactica.movies;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mandeep.galactica.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import info.movito.themoviedbapi.model.MovieDb;

/**
 * Created by mandeep on 9/4/16.
 */
public class MatchMoviesAdapter extends BaseExpandableListAdapter {

    private String[] groups = {"Common likes", "Recommended movies"};
    private String[] emptyGroups = {"No common likes", "No suggestions at the moment"};
    private Context context;
    List<Integer> otherGuysInterests;
    private final List<MovieDb> intersection;
    private final List<MovieDb> firstHopMatch;

    public MatchMoviesAdapter(Context context, List<Integer> otherGuysInterests) {
        this.context = context;
        this.otherGuysInterests = otherGuysInterests;

        intersection = MovieInterestsModel.getInstance().getIntersectionWith(otherGuysInterests);
        firstHopMatch = MovieInterestsModel.getInstance().getFirstHopMatches(otherGuysInterests);
    }

    @Override
    public int getGroupCount() {
        return groups.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (groupPosition == 0) return intersection.size();
        else return firstHopMatch.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (groupPosition == 0) return intersection.get(childPosition);
        else return firstHopMatch.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.movie_match_group_title, null);
        }

        TextView lblListHeader = (TextView) convertView .findViewById(R.id.listGroupHeader);

        if (groupPosition == 0){
            if(!intersection.isEmpty())
                lblListHeader.setText(groups[0]);
            else
                lblListHeader.setText(emptyGroups[0]);
        }
        else if (groupPosition == 1){
            if(!firstHopMatch.isEmpty())
                lblListHeader.setText(groups[1]);
            else
                lblListHeader.setText(emptyGroups[1]);
        }

        ExpandableListView eLV = (ExpandableListView) parent;
        eLV.expandGroup(groupPosition);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.movie_match_list_item, null);
        }

        MovieDb movieBeingPopulated;
        if (groupPosition == 0){
            movieBeingPopulated = intersection.get(childPosition);
        }else {
            movieBeingPopulated = firstHopMatch.get(childPosition);
        }
        TextView title = (TextView) convertView.findViewById(R.id.matchTitle);
        title.setText(movieBeingPopulated.getTitle());

        ImageView poster = (ImageView) convertView.findViewById(R.id.matchPoster);
        if (!TextUtils.isEmpty(movieBeingPopulated.getPosterPath())) {
            Uri posterUri = new Uri.Builder()
                    .scheme("http")
                    .authority("image.tmdb.org")
                    .appendPath("t").appendPath("p").appendPath("w342")
                    .appendEncodedPath(movieBeingPopulated.getPosterPath()).build();

            Picasso.with(context).load(posterUri).fit().into(poster);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
