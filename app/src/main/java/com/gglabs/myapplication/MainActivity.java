package com.gglabs.myapplication;

import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_ADD_CONTACT = 22;
    private static final int REQUEST_CODE_EDIT_CONTACT = 23;

    Toolbar toolbar;
    ActionBar actionBar;
    SearchQueryListener searchListener;

    TextView tvNoItems;

    RecyclerView rv;
    RecycleAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.contacts);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        searchListener = new SearchQueryListener();
        init();
    }

    private void init() {
        tvNoItems = (TextView) findViewById(R.id.tv_no_items);
        rv = (RecyclerView) findViewById(R.id.rv_contacts);

        rv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);

        adapter = new RecycleAdapter(this);
        rv.setAdapter(adapter);

        updateUI();
    }

    public void dialContact(int position){
        Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse(
                "tel:" + adapter.getContacts().get(position).getPhone()));
        startActivity(i);
    }

    public void editContact(int position) {
        Contact selectedContact = adapter.getContacts().get(position);

        Intent iEdit = new Intent(MainActivity.this, ManageContactActivity.class);
        iEdit.putExtra("edit_contact", selectedContact);
        iEdit.putExtra("edit_contact_pos", position);
        startActivityForResult(iEdit, REQUEST_CODE_EDIT_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ADD_CONTACT) {
            if (resultCode == RESULT_OK) {
                Contact newContact = (Contact) data.getSerializableExtra("new_contact");

                adapter.add(newContact);
                updateUI();
            }
        }

        if (requestCode == REQUEST_CODE_EDIT_CONTACT) {
            if (resultCode == RESULT_OK) {
                Contact editedContact = (Contact) data.getSerializableExtra("update_contact");
                int contactPos = data.getIntExtra("update_contact_pos", -1);
                adapter.update(contactPos, editedContact);
            }
        }
    }

    public void updateUI() {
        int visibility = tvNoItems.getVisibility();
        if (adapter.getItemCount() > 0) {
            if (visibility != View.GONE) tvNoItems.setVisibility(View.GONE);
        } else if (visibility != View.VISIBLE) tvNoItems.setVisibility(View.VISIBLE);
    }

    public void populateListview() {
        List<Contact> newContacts = new ArrayList<>();

        newContacts.add(new Contact("Hulio", "0557542358", R.drawable.ic_male, R.color.color_green));
        newContacts.add(new Contact("Pedro", "0587385409", R.drawable.ic_male, R.color.color_black));
        newContacts.add(new Contact("Peach", "0527593464", R.drawable.ic_female, R.color.color_red));
        newContacts.add(new Contact("Marioina", "0527593464", R.drawable.ic_female, R.color.color_blue));
        newContacts.add(new Contact("Bean", "0543578458", R.drawable.ic_bird, R.color.color_blue));
        newContacts.add(new Contact("The Bird", "0507854275", R.drawable.ic_bird, R.color.color_green));
        newContacts.add(new Contact("Some Guy", "0575456767", R.drawable.ic_contact_black, R.color.color_black));
        newContacts.add(new Contact("Santa", "0554258725", R.drawable.ic_contact_black, R.color.color_red));
        Collections.shuffle(newContacts);

        for (Contact c : newContacts) adapter.add(c);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        toolbar.inflateMenu(R.menu.main);

        MenuItem item = menu.findItem(R.id.option_search);
        SearchView searchView = (SearchView) item.getActionView();

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(searchListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_add:
                startActivityForResult(new Intent(this, ManageContactActivity.class), REQUEST_CODE_ADD_CONTACT);
                return true;

            case R.id.option_search:
                return true;

            case R.id.option_delete:
/*                if (mActionMode == null) {
                    mActionMode = startActionMode(actionModeCallBacks);
                    mActionMode.setTitle(adapter.getSelectedCount() + " " + getString(R.string.selected));
                }*/
                return true;

            case R.id.option_populate_list:
                populateListview();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class SearchQueryListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            adapter.getFilter().filter(newText);
            return false;
        }
    }

}
