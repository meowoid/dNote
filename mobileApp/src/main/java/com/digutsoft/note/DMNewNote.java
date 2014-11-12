package com.digutsoft.note;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;


public class DMNewNote extends ActionBarActivity {

    EditText etNoteTitle, etNoteContent;
    String mSaveCategory;
    boolean mIsEditMode;
    int mEditingNoteId;

    AlertDialog.Builder adbCancelMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_note);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(!getIntent().hasExtra("saveCategory")) {
            Toast.makeText(DMNewNote.this, R.string.new_note_category_not_defined, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        mSaveCategory = getIntent().getStringExtra("saveCategory");

        etNoteTitle = (EditText) findViewById(R.id.etNoteTitle);
        etNoteContent = (EditText) findViewById(R.id.etNoteContent);

        if(getIntent().hasExtra("editingNoteId")) {
            mIsEditMode = true;
            mEditingNoteId = getIntent().getIntExtra("editingNoteId", -1);

            if(mEditingNoteId == -1) {
                //TODO: failed to get note id
                finish();
                return;
            }

            getSupportActionBar().setTitle(R.string.mv_popup_menu_edit);
            etNoteTitle.setText(DMMemoTools.getMemo(DMNewNote.this, mSaveCategory, mEditingNoteId, true));
            etNoteContent.setText(DMMemoTools.getMemo(DMNewNote.this, mSaveCategory, mEditingNoteId, false));
        }

        adbCancelMessage = new AlertDialog.Builder(DMNewNote.this);
        adbCancelMessage.setMessage(R.string.new_note_cancel_message);
        adbCancelMessage.setPositiveButton(R.string.new_note_cancel_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        adbCancelMessage.setNegativeButton(R.string.new_note_cancel_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                adbCancelMessage.show();
                return true;
            case R.id.action_save:
                if(mIsEditMode) {
                    switch (DMMemoTools.editMemo(DMNewNote.this, mSaveCategory, mEditingNoteId, etNoteTitle.getText().toString(), etNoteContent.getText().toString())) {
                        case 0:
                            Toast.makeText(DMNewNote.this, R.string.new_note_edited, Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        case 1:
                            Toast.makeText(DMNewNote.this, R.string.mv_popup_menu_edit_fail, Toast.LENGTH_LONG).show();
                            break;
                        case 2:
                            Toast.makeText(DMNewNote.this, R.string.mv_popup_menu_edit_fail_empty, Toast.LENGTH_LONG).show();
                            break;
                        case 3:
                            Toast.makeText(DMNewNote.this, R.string.mv_popup_menu_edit_fail_title_too_long, Toast.LENGTH_LONG).show();
                            break;
                    }
                } else {
                    switch (DMMemoTools.saveMemo(DMNewNote.this, mSaveCategory, etNoteTitle.getText().toString(), etNoteContent.getText().toString())) {
                        case 0:
                            Toast.makeText(DMNewNote.this, R.string.new_note_saved, Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        case 1:
                            Toast.makeText(DMNewNote.this, R.string.mv_save_fail, Toast.LENGTH_LONG).show();
                            break;
                        case 2:
                            Toast.makeText(DMNewNote.this, R.string.mv_save_fail_empty, Toast.LENGTH_LONG).show();
                            break;
                    }
                }
                return true;
            case R.id.action_cancel:
                adbCancelMessage.show();
                return true;
            default:
                return onOptionsItemSelected(item);
        }
    }

    public void onBackPressed() {
        adbCancelMessage.show();
    }
}
