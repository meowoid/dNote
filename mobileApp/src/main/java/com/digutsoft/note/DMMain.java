package com.digutsoft.note;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import com.digutsoft.note.classes.DMMemoList;
import com.digutsoft.note.classes.DMMemoTools;

import java.util.ArrayList;

public class DMMain extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private DrawerLayout mDrawerLayout;
    private boolean isSmallScreen = false;
    public static CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (mDrawerLayout != null) {
            isSmallScreen = true;
            mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
            mNavigationDrawerFragment.setUp(R.id.navigation_drawer, mDrawerLayout);
        }

        try {
            if (getIntent().hasExtra("mvCategoryName")) {
                mTitle = getIntent().getStringExtra("mvCategoryName");
            } else {
                mTitle = DMMemoTools.getCategoryList(this).get(0);
            }
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, new DMMemoView())
                .commit();
        onSectionAttached(position);
    }

    public void onSectionAttached(int number) {
        mTitle = DMMemoTools.getCategoryList(this).get(number);
        getSupportActionBar().setTitle(mTitle);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(isSmallScreen) {
            if (!mNavigationDrawerFragment.isDrawerOpen()) {
                getMenuInflater().inflate(R.menu.memoview, menu);
                restoreActionBar();
                return true;
            }
        } else {
            getMenuInflater().inflate(R.menu.memoview, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_new_note:
                Intent intNewNote = new Intent(DMMain.this, DMNewNote.class);
                intNewNote.putExtra("saveCategory", mTitle);
                startActivity(intNewNote);
                return true;
            case R.id.action_share_category:
                ArrayList<DMMemoList> alMemoList = DMMemoTools.getMemoList(DMMain.this, mTitle.toString());
                StringBuilder sbMemoList = new StringBuilder();
                if (alMemoList.size() == 0) {
                    Toast.makeText(DMMain.this, R.string.mv_share_category_nothing_to_share, Toast.LENGTH_LONG).show();
                    return true;
                }
                sbMemoList.append(mTitle);
                sbMemoList.append(": ");
                for (int i = 0; i < alMemoList.size(); i++) {
                    sbMemoList.append(alMemoList.get(i).mMemoContent);
                    if (i != alMemoList.size() - 1) sbMemoList.append(", ");
                }
                Intent intShare = new Intent(android.content.Intent.ACTION_SEND);
                intShare.setType("text/plain");
                intShare.putExtra(android.content.Intent.EXTRA_TEXT, sbMemoList.toString());
                startActivity(Intent.createChooser(intShare, getResources().getString(R.string.mv_share_category)));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed() {
        if(isSmallScreen && mNavigationDrawerFragment.isDrawerOpen())
            mDrawerLayout.closeDrawers();
        else
            finish();
    }
}
