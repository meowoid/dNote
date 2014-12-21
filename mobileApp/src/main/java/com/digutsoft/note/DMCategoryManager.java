package com.digutsoft.note;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.digutsoft.note.classes.DMMemoTools;

public class DMCategoryManager extends ActionBarActivity {

    ListView lvCategoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_manager);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra("popupCategoryName")) {
            showPopupMenu(getIntent().getStringExtra("popupCategoryName"));
        }

        lvCategoryList = (ListView) findViewById(R.id.lvCategoryList);
        lvCategoryList.setEmptyView(findViewById(R.id.tvCategoryEmpty));

        Button btCreateCategory = (Button) findViewById(R.id.btCreateCategory);
        btCreateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText etCategoryName = (EditText) findViewById(R.id.etCategoryName);
                switch (DMMemoTools.createCategory(DMCategoryManager.this, etCategoryName.getText().toString())) {
                    case 0:
                        etCategoryName.setText(null);
                        reloadCategory();
                        break;
                    case 1:
                        Toast.makeText(DMCategoryManager.this, R.string.cm_create_fail, Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        Toast.makeText(DMCategoryManager.this, R.string.cm_create_fail_reserved, Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        Toast.makeText(DMCategoryManager.this, R.string.cm_create_fail_too_long, Toast.LENGTH_LONG).show();
                        break;
                    case 4:
                        Toast.makeText(DMCategoryManager.this, R.string.cm_create_fail_empty, Toast.LENGTH_LONG).show();
                        break;
                    case 5:
                        Toast.makeText(DMCategoryManager.this, R.string.cm_create_fail_already_exists, Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });

        lvCategoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showPopupMenu(lvCategoryList.getItemAtPosition(i).toString());
            }
        });

        lvCategoryList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                showPopupMenu(lvCategoryList.getItemAtPosition(i).toString());
                return true;
            }
        });
    }

    public void onResume() {
        super.onResume();
        reloadCategory();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showPopupMenu(final String mCategoryName) {
        AlertDialog.Builder adbCategoryPopupMenu = new AlertDialog.Builder(DMCategoryManager.this);
        adbCategoryPopupMenu.setTitle(mCategoryName);
        adbCategoryPopupMenu.setItems(new String[]{getResources().getString(R.string.cm_popup_menu_rename),
                        getResources().getString(R.string.cm_popup_menu_delete)},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                final EditText etNewCategoryName = new EditText(DMCategoryManager.this);
                                etNewCategoryName.setText(mCategoryName);
                                etNewCategoryName.setImeOptions(EditorInfo.IME_ACTION_DONE);
                                etNewCategoryName.setSingleLine();
                                etNewCategoryName.requestFocus();
                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                                inputMethodManager.showSoftInputFromInputMethod(getCurrentFocus().getWindowToken(), 0);
                                AlertDialog.Builder adbRenameCategory = new AlertDialog.Builder(DMCategoryManager.this);
                                adbRenameCategory.setTitle(R.string.cm_popup_menu_rename);
                                adbRenameCategory.setView(etNewCategoryName);
                                adbRenameCategory.setPositiveButton(R.string.cm_popup_menu_rename_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        switch (DMMemoTools.renameCategory(DMCategoryManager.this, mCategoryName, etNewCategoryName.getText().toString())) {
                                            case 0:
                                                reloadCategory();
                                                break;
                                            case 1:
                                                Toast.makeText(DMCategoryManager.this, R.string.cm_popup_menu_rename_fail, Toast.LENGTH_LONG).show();
                                                break;
                                            case 2:
                                                Toast.makeText(DMCategoryManager.this, R.string.cm_popup_menu_rename_fail_already_exists, Toast.LENGTH_LONG).show();
                                                break;
                                            case 3:
                                                Toast.makeText(DMCategoryManager.this, R.string.cm_popup_menu_rename_fail_same_name, Toast.LENGTH_LONG).show();
                                                break;
                                            case 4:
                                                Toast.makeText(DMCategoryManager.this, R.string.cm_popup_menu_rename_fail_empty, Toast.LENGTH_LONG).show();
                                                break;
                                            case 5:
                                                Toast.makeText(DMCategoryManager.this, R.string.cm_popup_menu_rename_fail_reserved, Toast.LENGTH_LONG).show();
                                                break;
                                            case 6:
                                                Toast.makeText(DMCategoryManager.this, R.string.cm_popup_menu_rename_fail_too_long, Toast.LENGTH_LONG).show();
                                                break;
                                        }
                                    }
                                });
                                adbRenameCategory.setNegativeButton(R.string.cm_popup_menu_rename_cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                });
                                adbRenameCategory.show();
                                break;
                            case 1:
                                AlertDialog.Builder adbDeleteConfirm = new AlertDialog.Builder(DMCategoryManager.this);
                                adbDeleteConfirm.setTitle(R.string.cm_popup_menu_delete);
                                adbDeleteConfirm.setMessage(R.string.cm_popup_menu_delete_prompt);
                                adbDeleteConfirm.setPositiveButton(R.string.cm_popup_menu_delete_prompt_yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        switch (DMMemoTools.deleteCategory(DMCategoryManager.this, mCategoryName)) {
                                            case 0:
                                                reloadCategory();
                                                break;
                                            case 1:
                                                Toast.makeText(DMCategoryManager.this, R.string.cm_popup_menu_delete_failed, Toast.LENGTH_LONG).show();
                                                break;
                                            case 2:
                                                Toast.makeText(DMCategoryManager.this, R.string.cm_popup_menu_delete_failed_last_one, Toast.LENGTH_LONG).show();
                                                break;
                                        }
                                    }
                                });
                                adbDeleteConfirm.setNegativeButton(R.string.cm_popup_menu_delete_prompt_no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                });
                                adbDeleteConfirm.show();
                                break;
                        }

                    }
                });
        adbCategoryPopupMenu.show();
    }

    private void reloadCategory() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(DMCategoryManager.this,
                android.R.layout.simple_list_item_1,
                DMMemoTools.getCategoryList(DMCategoryManager.this));
        lvCategoryList.setAdapter(arrayAdapter);
    }
}
