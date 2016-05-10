package com.example.mandeep.galactica.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.mandeep.galactica.R;
import com.example.mandeep.galactica.music.MusicAPI.MusicSearchResult.Artist;

import java.util.List;

/**
 * Created by mandeep on 2/5/16.
 */
public class MatchMusicAdapter extends BaseExpandableListAdapter {

    private static final String TAG = "MatchMusicAdapter";
    private String[] groups = {"Common likes", "Recommended music"};
    private String[] emptyGroups = {"No common likes", "No suggestions at the moment"};
    private Context context;
    List<String> otherGuysInterests;
    private List<Artist> intersection;
    private List<Artist> firstHopMatch;
    public MatchMusicAdapter(Context context, List<String> interests) {
        this.context = context;
        this.otherGuysInterests = interests;

        intersection = MusicInterestsModel.getInstance().getIntersectionWith(otherGuysInterests);
        firstHopMatch = MusicInterestsModel.getInstance().getFirstHopMatches(otherGuysInterests);
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
            convertView = inflator.inflate(R.layout.music_match_group_title, null);
        }

        TextView lblListHeader = (TextView) convertView .findViewById(R.id.listMusicGroupHeader);

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
            convertView = inflator.inflate(R.layout.music_match_list_item, null);
        }

        Artist artistBeingPopulated;
        if (groupPosition == 0){
            artistBeingPopulated = intersection.get(childPosition);
        }else {
            artistBeingPopulated = firstHopMatch.get(childPosition);
        }
        TextView title = (TextView) convertView.findViewById(R.id.artistMatchTitle);
        title.setText(artistBeingPopulated.name);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
