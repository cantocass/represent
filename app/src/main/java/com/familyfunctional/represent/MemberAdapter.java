package com.familyfunctional.represent;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MemberAdapter extends ArrayAdapter<Member> {
    WeakReference<Context> contextWeakReference;

    public MemberAdapter(Context context, ArrayList<Member> members) {
        super(context, 0, members);
        contextWeakReference = new WeakReference<>(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Member member = getItem(position);
// Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_results, parent, false);
            viewHolder.honorific = (TextView) convertView.findViewById(R.id.honorific);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.partyAndState = (TextView) convertView.findViewById(R.id.party_state);
            viewHolder.call = (ImageView) convertView.findViewById(R.id.call);
            viewHolder.navigate = (ImageView) convertView.findViewById(R.id.navigate);
            viewHolder.browse = (ImageView) convertView.findViewById(R.id.browse);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.honorific.setText(determineIsRepOrSen(member.getDistrict()));
        viewHolder.name.setText(member.getName());
        viewHolder.partyAndState.setText(member.getParty() + "-" + member.getState());

        viewHolder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("tel:" + member.getPhone());
                Intent callIntent = new Intent(Intent.ACTION_DIAL, uri);
                callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (callIntent.resolveActivity(v.getContext().getPackageManager()) != null) {
                    v.getContext().startActivity(callIntent);
                }
            }
        });

        viewHolder.navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("google.navigation:q=" + Uri.encode(member.getOffice()));
                Intent navIntent = new Intent(Intent.ACTION_VIEW, uri);
                navIntent.setPackage("com.google.android.apps.maps");
                if (navIntent.resolveActivity(v.getContext().getPackageManager()) != null) {
                    v.getContext().startActivity(navIntent);
                }
            }
        });

        viewHolder.browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(normalizeLink(member.link));
                Intent browseIntent = new Intent(Intent.ACTION_VIEW, uri);
                if (browseIntent.resolveActivity(v.getContext().getPackageManager()) != null) {
                    v.getContext().startActivity(browseIntent);
                }
            }
        });

        return convertView;
    }

    private String normalizeLink(String link) {
        if (link.contains("https")) {
            link = link.replace("https", "http");
        }
        return link;
    }

    private String determineIsRepOrSen(String district) {
        if (district.contains("Seat")) {
            return contextWeakReference.get().getString(R.string.senator);
        } else {
            return contextWeakReference.get().getString(R.string.representative);
        }
    }

    private static class ViewHolder {
        TextView honorific;
        TextView name;
        TextView partyAndState;
        ImageView call;
        ImageView navigate;
        ImageView browse;
    }
}
