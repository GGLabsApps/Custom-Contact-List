package com.gglabs.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GG on 13/12/2017.
 */

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> implements Filterable {

    private static final String TAG = "RecycleAdapter";

    public MainActivity mainActivity;
    private Context context;


    private RecyclerClickListener onClickListener;
    private RecyclerView recyclerView;
    private MultiChoiceHelper multiChoiceHelper;
    private ContactsFilter contactsFilter;
    private List<Contact> contacts;
    private DbHandler db;

    public RecycleAdapter(MainActivity activity) {
        this.mainActivity = activity;
        this.context = activity;

        db = new DbHandler(context);
        contacts = db.getAllContacts();

        recyclerView = mainActivity.rv;
        multiChoiceHelper = new MultiChoiceHelper(activity, this);
        onClickListener = new RecyclerClickListener();
    }

    public class ViewHolder extends MultiChoiceHelper.ViewHolder {
        private CardView rootLayout;
        private View layIvDial;
        private TextView tvName, tvPhoneNum;
        private ImageView ivContactImg;

        ViewHolder(View itemView) {
            super(itemView);
            this.rootLayout = (CardView) itemView;
            tvName = (TextView) itemView.findViewById(R.id.tv_item_name);
            tvPhoneNum = (TextView) itemView.findViewById(R.id.tv_item_phone_number);
            ivContactImg = (ImageView) itemView.findViewById(R.id.iv_item_contact_img);
            layIvDial = (View) itemView.findViewById(R.id.lay_iv_dial);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        multiChoiceHelper.setMultiChoiceModeListener(new MultiChoiceListener());
        View v = inflater.inflate(R.layout.list_item_contact, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.bind(multiChoiceHelper, position);
        holder.setOnClickListener(onClickListener);
        holder.layIvDial.setOnClickListener(onClickListener);
        holder.layIvDial.setTag(position);

        final Contact contact = contacts.get(position);

        if (holder.isMultiChoiceActive())
            holder.layIvDial.setVisibility(View.INVISIBLE);
        else if (holder.layIvDial.getVisibility() == View.INVISIBLE)
            holder.layIvDial.setVisibility(View.VISIBLE);

        if (multiChoiceHelper.isItemChecked(position))
            holder.rootLayout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.color_selected_row));
        else
            holder.rootLayout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.color_dark_row));

        holder.tvName.setText(contact.getName());
        holder.tvPhoneNum.setText(contact.getPhone());
        holder.ivContactImg.setImageResource(contact.getImage());

        int textColor = ContextCompat.getColor(context, contact.getColor());
        holder.tvName.setTextColor(textColor);
        holder.tvPhoneNum.setTextColor(textColor);
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void add(int position, Contact contact) {
        //Log.d(TAG, "add(" + position + ") to contacts.size() = " + contacts.size());
        db.insert(contact);
        contacts.add(contact);
        mainActivity.updateUI();
        notifyItemInserted(position);
    }

    public void add(Contact contact) {
        db.insert(contact);
        contacts.add(contact);
        mainActivity.updateUI();
        notifyItemInserted(contacts.size());
    }

    public void remove(Contact contact) {
        int position = contacts.indexOf(contact);

        db.delete(contacts.get(position));
        contacts.remove(position);
        mainActivity.updateUI();
        notifyItemRemoved(position);
    }

    public void remove(int position) {
        db.delete(contacts.get(position));
        contacts.remove(position);
        mainActivity.updateUI();
        notifyItemRemoved(position);
    }

    public void update(int position, Contact newContact) {
        db.update(newContact);
        contacts.set(position, newContact);
        notifyItemChanged(position);
    }

    private void removeContacts(SparseBooleanArray checkedIds){
        for (int i = checkedIds.size() - 1; i >= 0; i--) {
            if (!checkedIds.valueAt(i)) continue;
            Contact contact = contacts.get(checkedIds.keyAt(i));
            remove(contact);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    class MultiChoiceListener implements MultiChoiceHelper.MultiChoiceModeListener {

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            mode.setTitle(multiChoiceHelper.getCheckedItemCount() + " Selected");
            notifyDataSetChanged();
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.delete_multiple, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.option_delete:

                    removeContacts(multiChoiceHelper.getCheckedItemPositions());
                    mode.finish();
                    return true;

                case R.id.option_search:
//                    Toast.makeText(context, "Search clicked", Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            multiChoiceHelper.clearChoices(false);
            notifyItemRangeChanged(0, contacts.size());
        }
    }

    private class RecyclerClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rootLayout:
                    if (multiChoiceHelper.isMultiChoiceActive())
                        mainActivity.editContact(recyclerView.getChildLayoutPosition(v));
                    break;

                case R.id.lay_iv_dial:
                    mainActivity.dialContact((int) v.getTag());
                    break;
            }
        }
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (contactsFilter == null) contactsFilter = new ContactsFilter();
        return contactsFilter;
    }

    private class ContactsFilter extends Filter {

        private List<Contact> buContacts = contacts;

        @Override
        protected FilterResults performFiltering(CharSequence charSeq) {
            FilterResults results = new FilterResults();

            if (charSeq == null || charSeq.length() == 0) {
                results.values = buContacts;
                results.count = buContacts.size();
            } else {
                List<Contact> filterResults = new ArrayList<>();

                String name;
                String phone;
                for (Contact c : buContacts) {
                    name = c.getName().toLowerCase();
                    phone = c.getPhone();

                    if (name.contains(charSeq.toString().toLowerCase()) ||
                            phone.contains(charSeq.toString()))
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


/*    private class SelectionMode {
        boolean isSelectionMode = false;
        SparseBooleanArray selectedIds;

        SelectionMode() {
            selectedIds = new SparseBooleanArray();
        }
    }

    void setSelectionMode(boolean selectionMode) {
        //Log.d(TAG, "setSelectionMode(" + selectionMode + ")");
        this.selectionMode.isSelectionMode = selectionMode;

        if (!selectionMode) getSelectedIds().clear();
        notifyItemRangeChanged(0, getItemCount());
    }

    boolean isSelectionMode() {
        return selectionMode.isSelectionMode;
    }

    void toggleItemSelected(int position) {
        boolean isSelected = !selectionMode.selectedIds.get(position);

        if (isSelected) selectionMode.selectedIds.put(position, isSelected);
        else selectionMode.selectedIds.delete(position);
        notifyItemChanged(position);
    }

    boolean isItemSelected(int position) {
        return selectionMode.selectedIds.get(position);
    }

    int getSelectedCount() {
        return selectionMode.selectedIds.size();
    }

    SparseBooleanArray getSelectedIds() {
        return selectionMode.selectedIds;
    }*/

}
