package com.digutsoft.note;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DMMemoView extends Fragment {

    private String mCategoryName;
    private ListView lvMemoList;
    private EditText etMemoContent;
    private Button btAddMemo;
    private SharedPreferences sharedPreferences;

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.frg_main, container, false);

        mCategoryName = DMMain.mTitle.toString();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean isEnterSaveEnabled = sharedPreferences.getBoolean("st_enter_save", false);

        etMemoContent = (EditText) rootView.findViewById(R.id.etMemoContent);

        etMemoContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return NavigationDrawerFragment.isDrawerOpen;
            }
        });

        if (isEnterSaveEnabled) {
            etMemoContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_DONE) {
                        switch (DMMemoTools.saveMemo(getActivity(), mCategoryName, etMemoContent.getText().toString())) {
                            case 0:
                                etMemoContent.setText(null);
                                reloadMemo();
                                break;
                            case 1:
                                Toast.makeText(getActivity(), R.string.mv_save_fail, Toast.LENGTH_LONG).show();
                                break;
                            case 2:
                                Toast.makeText(getActivity(), R.string.mv_save_fail_empty, Toast.LENGTH_LONG).show();
                                break;
                        }
                        return true;
                    }
                    return false;
                }
            });

            etMemoContent.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                        switch (DMMemoTools.saveMemo(getActivity(), mCategoryName, etMemoContent.getText().toString())) {
                            case 0:
                                etMemoContent.setText(null);
                                reloadMemo();
                                break;
                            case 1:
                                Toast.makeText(getActivity(), R.string.mv_save_fail, Toast.LENGTH_LONG).show();
                                break;
                            case 2:
                                Toast.makeText(getActivity(), R.string.mv_save_fail_empty, Toast.LENGTH_LONG).show();
                                break;
                        }
                        return true;
                    }
                    return false;
                }
            });
        }

        btAddMemo = (Button) rootView.findViewById(R.id.btAddMemo);
        btAddMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (DMMemoTools.saveMemo(getActivity(), mCategoryName, etMemoContent.getText().toString())) {
                    case 0:
                        etMemoContent.setText(null);
                        reloadMemo();
                        break;
                    case 1:
                        Toast.makeText(getActivity(), R.string.mv_save_fail, Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        Toast.makeText(getActivity(), R.string.mv_save_fail_empty, Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });

        lvMemoList = (ListView) rootView.findViewById(R.id.lvMemoList);
        lvMemoList.setEmptyView(rootView.findViewById(R.id.tvMemoEmpty));

        lvMemoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int memoId = DMMemoTools.getMemoIndex(getActivity(), mCategoryName).get(i);
                String mMemoTitle = DMMemoTools.getMemo(getActivity(), mCategoryName, memoId, true);
                final String mMemoContent = DMMemoTools.getMemo(getActivity(), mCategoryName, memoId, false);

                AlertDialog.Builder adbMemoPopup = new AlertDialog.Builder(getActivity());
                adbMemoPopup.setTitle(mMemoTitle);
                adbMemoPopup.setMessage(mMemoContent);
                adbMemoPopup.setPositiveButton(R.string.mv_popup_share, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intShare = new Intent(android.content.Intent.ACTION_SEND);
                        intShare.setType("text/plain");
                        intShare.putExtra(android.content.Intent.EXTRA_TEXT, mMemoContent);
                        startActivity(Intent.createChooser(intShare, getResources().getString(R.string.mv_popup_share)));
                    }
                });
                adbMemoPopup.setNegativeButton(R.string.mv_popup_close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                });
                adbMemoPopup.show();
            }
        });

        lvMemoList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int memoId = DMMemoTools.getMemoIndex(getActivity(), mCategoryName).get(i);

                AlertDialog.Builder adbMemoPopupMenu = new AlertDialog.Builder(getActivity());
                adbMemoPopupMenu.setTitle(DMMemoTools.getMemo(getActivity(), mCategoryName, memoId, true));
                adbMemoPopupMenu.setItems(new String[]{getResources().getString(R.string.mv_popup_menu_delete),
                                getResources().getString(R.string.mv_popup_menu_copy),
                                getResources().getString(R.string.mv_popup_menu_edit),
                                getResources().getString(R.string.mv_popup_menu_set_title),
                                getResources().getString(R.string.mv_popup_menu_move_category),
                                getResources().getString(R.string.mv_popup_menu_pin)},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {
                                    case 0: {   //Delete
                                        int deleteMemo = DMMemoTools.deleteMemo(getActivity(), mCategoryName, memoId);
                                        if (deleteMemo == 0) {
                                            reloadMemo();
                                        } else {
                                            Toast.makeText(getActivity(), R.string.mv_popup_menu_delete_fail, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    break;
                                    case 1: {   //Copy
                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                                            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                                            clipboard.setText(DMMemoTools.getMemo(getActivity(), mCategoryName, memoId, false));
                                        } else {
                                            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                                            android.content.ClipData clip = android.content.ClipData.newPlainText("dMemo", DMMemoTools.getMemo(getActivity(), mCategoryName, memoId, false));
                                            clipboard.setPrimaryClip(clip);
                                        }
                                        Toast.makeText(getActivity(), R.string.mv_popup_menu_copy_success, Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                    case 2: {   //Edit
                                        String memoTitle = DMMemoTools.getMemo(getActivity(), mCategoryName, memoId, true);
                                        String memoContent = DMMemoTools.getMemo(getActivity(), mCategoryName, memoId, false);
                                        if (memoTitle.startsWith(memoContent) || memoContent.length() < 20) {
                                            final EditText etNewMemo = new EditText(getActivity());
                                            etNewMemo.setText(memoContent);
                                            etNewMemo.requestFocus();
                                            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                                            inputMethodManager.showSoftInputFromInputMethod(getActivity().getCurrentFocus().getWindowToken(), 0);
                                            AlertDialog.Builder adbAddTitle = new AlertDialog.Builder(getActivity());
                                            adbAddTitle.setTitle(R.string.mv_popup_menu_edit);
                                            adbAddTitle.setView(etNewMemo);
                                            adbAddTitle.setPositiveButton(R.string.mv_popup_menu_edit_save, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    String mNewMemo = etNewMemo.getText().toString();
                                                    switch (DMMemoTools.editMemo(getActivity(), mCategoryName, memoId, mNewMemo)) {
                                                        case 0:
                                                            reloadMemo();
                                                            break;
                                                        case 1:
                                                            Toast.makeText(getActivity(), R.string.mv_popup_menu_edit_fail, Toast.LENGTH_LONG).show();
                                                            break;
                                                        case 2:
                                                            Toast.makeText(getActivity(), R.string.mv_popup_menu_edit_fail_empty, Toast.LENGTH_LONG).show();
                                                            break;
                                                    }
                                                }
                                            });
                                            adbAddTitle.setNegativeButton(R.string.mv_popup_menu_edit_close, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {}
                                            });
                                            adbAddTitle.show();
                                        } else {
                                            Intent intEditNote = new Intent(getActivity(), DMNewNote.class);
                                            intEditNote.putExtra("saveCategory", mCategoryName);
                                            intEditNote.putExtra("editingNoteId", memoId);
                                            startActivity(intEditNote);
                                        }
                                    }
                                    break;
                                    case 3: {   //Set title
                                        final EditText etMemoTitle = new EditText(getActivity());
                                        etMemoTitle.setText(DMMemoTools.getMemo(getActivity(), mCategoryName, memoId, true));
                                        etMemoTitle.requestFocus();
                                        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                                        inputMethodManager.showSoftInputFromInputMethod(getActivity().getCurrentFocus().getWindowToken(), 0);
                                        AlertDialog.Builder adbAddTitle = new AlertDialog.Builder(getActivity());
                                        adbAddTitle.setTitle(R.string.mv_popup_menu_set_title);
                                        adbAddTitle.setView(etMemoTitle);
                                        adbAddTitle.setPositiveButton(R.string.mv_popup_menu_set_title_save, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                String mMemoTitle = etMemoTitle.getText().toString();
                                                switch(DMMemoTools.setMemoTitle(getActivity(), mCategoryName, memoId, mMemoTitle)) {
                                                    case 0:
                                                        reloadMemo();
                                                        break;
                                                    case 1:
                                                        Toast.makeText(getActivity(), R.string.mv_popup_menu_set_title_fail, Toast.LENGTH_LONG).show();
                                                        break;
                                                    case 2:
                                                        Toast.makeText(getActivity(), R.string.mv_popup_menu_set_title_fail_too_long, Toast.LENGTH_LONG).show();
                                                        break;
                                                }
                                            }
                                        });
                                        adbAddTitle.setNegativeButton(R.string.mv_popup_menu_set_title_close, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {}
                                        });
                                        adbAddTitle.show();
                                    }
                                    break;
                                    case 4: {   //Move category
                                        final ArrayAdapter<String> aaCategoryList = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, DMMemoTools.getCategoryList(getActivity()));
                                        AlertDialog.Builder adbCategoryList = new AlertDialog.Builder(getActivity());
                                        adbCategoryList.setTitle(R.string.mv_popup_menu_move_category);
                                        adbCategoryList.setAdapter(aaCategoryList, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                int saveMemo = DMMemoTools.saveMemo(getActivity(), aaCategoryList.getItem(i), DMMemoTools.getMemo(getActivity(), mCategoryName, memoId, false));
                                                int deleteMemo = DMMemoTools.deleteMemo(getActivity(), mCategoryName, memoId);
                                                if (saveMemo + deleteMemo == 0) {
                                                    Toast.makeText(getActivity(), String.format(getResources().getString(R.string.mv_popup_menu_move_category_success), aaCategoryList.getItem(i)), Toast.LENGTH_SHORT).show();
                                                    reloadMemo();
                                                } else {
                                                    Toast.makeText(getActivity(), R.string.mv_popup_menu_move_category_fail, Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                        adbCategoryList.show();
                                    }
                                    break;
                                    case 5: {   //Pin in notification center
                                        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                                        Notification notification;
                                        Intent intent = new Intent(getActivity(), DMMain.class);
                                        intent.putExtra("mvCategoryName", mCategoryName);
                                        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

                                        String memoTitle = DMMemoTools.getMemo(getActivity(), mCategoryName, memoId, true);
                                        String memoContent = DMMemoTools.getMemo(getActivity(), mCategoryName, memoId, false);

                                        String notiTicker, notiTitle, notiContent;

                                        if (memoTitle.startsWith(memoContent)) {
                                            notiTicker = memoContent;
                                            notiTitle = getResources().getString(R.string.mv_popup_menu_pin_title);
                                            notiContent = memoContent;
                                        } else {
                                            notiTicker = memoTitle;
                                            notiTitle = memoTitle;
                                            notiContent = memoContent;
                                        }

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                            notification = new NotificationCompat.Builder(getActivity())
                                                    .setContentTitle(notiTitle)
                                                    .setContentText(notiContent)
                                                    .setTicker(notiTicker)
                                                    .setContentIntent(pendingIntent)
                                                    .setSmallIcon(R.drawable.ic_launcher)
                                                    .build();
                                        } else {
                                            notification = new Notification(R.drawable.ic_launcher, null, System.currentTimeMillis());
                                            notification.tickerText = notiTicker;
                                            notification.setLatestEventInfo(getActivity(), notiTitle, notiContent, pendingIntent);
                                        }

                                        notificationManager.notify(1, notification);
                                    }
                                    break;
                                }
                            }
                        });
                adbMemoPopupMenu.show();
                return true;
            }
        });

        return rootView;
    }

    public void onResume() {
        super.onResume();
        reloadMemo();
        boolean isEnterSaveEnabled = sharedPreferences.getBoolean("st_enter_save", false);
        if (isEnterSaveEnabled) {
            etMemoContent.setSingleLine();
            etMemoContent.setImeOptions(EditorInfo.IME_ACTION_DONE);
            btAddMemo.setVisibility(View.GONE);
        } else {
            etMemoContent.setSingleLine(false);
            etMemoContent.setImeOptions(EditorInfo.IME_ACTION_NONE);
            btAddMemo.setVisibility(View.VISIBLE);
        }
    }

    private void reloadMemo() {
        super.onResume();
        try {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1,
                    DMMemoTools.getMemoList(getActivity(), mCategoryName, true));
            lvMemoList.setAdapter(arrayAdapter);
        } catch (SQLiteException e) {
            mCategoryName = DMMemoTools.getCategoryList(getActivity()).get(0);
            DMMain.mTitle = mCategoryName;
            reloadMemo();
        }
    }
}