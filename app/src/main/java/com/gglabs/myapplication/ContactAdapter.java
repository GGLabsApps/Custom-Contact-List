package com.gglabs.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GG on 28/11/2017.
 */

public class ContactAdapter extends ArrayAdapter implements Filterable {

    public static final String TAG = "ContactAdapter";

    private Context context;
    private Resources resources;
    private LayoutInflater inflater;
    private List<Contact> contacts;
    private List<Contact> allContacts;
    private ContactsFilter contactsFilter;

    private boolean selectionMode = false;
    private SparseBooleanArray selectedItemPositions;

    ContactAdapter(@NonNull final Context context, @NonNull List<Contact> contacts) {
        super(context, R.layout.list_item_contact, contacts);
        this.context = context;
        this.contacts = contacts;
        this.allContacts = contacts;

        inflater = LayoutInflater.from(context);
        resources = context.getResources();
        selectedItemPositions = new SparseBooleanArray();
    }

    class ViewHolder {
        int position;
        View layBg;

        TextView tvPhoneNum;
        TextView tvName;
        ImageView ivContactImg;
        View ivDial;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        Contact contact;
        contact = contacts.get(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_item_contact, parent, false);

            viewHolder.position = position;
            viewHolder.layBg = (View) convertView.findViewById(R.id.lay_item_bg);

            viewHolder.tvPhoneNum = (TextView) convertView.findViewById(R.id.tv_item_phone_number);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_item_name);
            viewHolder.ivContactImg = (ImageView) convertView.findViewById(R.id.iv_item_contact_img);
            viewHolder.ivDial = convertView.findViewById(R.id.iv_dial);
            viewHolder.ivDial.setTag(contacts.get(position).getPhone());
            viewHolder.ivDial.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + v.getTag()));
                    context.startActivity(i);
                }
            });

            convertView.setTag(viewHolder);
        } else viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.tvPhoneNum.setText(contact.getPhone());
        viewHolder.tvName.setText(contact.getName());
        viewHolder.ivContactImg.setImageResource(contact.getImage());

        int textColor = resources.getColor(contact.getColor());
        viewHolder.tvName.setTextColor(textColor);
        viewHolder.tvPhoneNum.setTextColor(textColor);

        hideOnSelection(viewHolder.ivDial, selectionMode);
//
//        /** Change background color of the selected items in list view  **/
//        int selectionColor = resources.getColor(R.color.color_selected_row);
//        if (selectedItemPositions.get(position)) viewHolder.layBg.setBackgroundColor(selectionColor);
//        else paintRowZebra(viewHolder.layBg, position, R.color.color_light_row, R.color.color_dark_row);

        return convertView;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    private class ContactsFilter extends Filter {

        private List<Contact> filterResults;

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                results.values = contacts;
                results.count = contacts.size();
            } else {
                filterResults = new ArrayList<>();

                for (Contact c : contacts) {
                    if (c.getName().toLowerCase().contains(constraint.toString().toLowerCase()))
                        filterResults.add(c);
                }

                results.values = filterResults;
                results.count = filterResults.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            contacts = (ArrayList<Contact>) results.values;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (contactsFilter == null) contactsFilter = new ContactsFilter();
        return contactsFilter;
    }

    //Toggle selection methods
    public void toggleSelection(int position) {
        setItemSelected(position, !selectedItemPositions.get(position));
    }

    //Put or delete selected position into SparseBooleanArray
    public void setItemSelected(int position, boolean value) {
        if (value) {
            //Log.d(TAG, "toggleSelection(position: " + position + ", value: " + value + ")");
            selectedItemPositions.put(position, value);
        } else
            selectedItemPositions.delete(position);

        notifyDataSetChanged();
    }

    //Remove selected selections
    public void cancelSelection() {
        selectedItemPositions.clear();
        selectionMode = false;

        notifyDataSetChanged();
    }

    //Get total selected count
    public int getSelectedCount() {
        return selectedItemPositions.size();
    }

    //Return all selected ids
    public SparseBooleanArray getSelectedIds() {
        return selectedItemPositions;
    }

    public void startSelectionMode() {
        this.selectionMode = true;
    }

    private void hideOnSelection(View view, boolean hide) {
        if (hide) view.setVisibility(View.INVISIBLE);
        else view.setVisibility(View.VISIBLE);
    }

    private void paintRowZebra(View view, int position, int resColorLight, @ColorRes int resColorDark) {
        if (position % 2 == 0)
            view.setBackgroundResource(resColorLight);
        else view.setBackgroundResource(resColorDark);
    }


}
