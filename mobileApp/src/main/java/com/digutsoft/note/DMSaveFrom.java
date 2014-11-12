package com.digutsoft.note;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class DMSaveFrom extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_from);

        getSupportActionBar().setTitle(R.string.save_from);

        final ListView lvCategoryList = (ListView) findViewById(R.id.lvCategoryList);
        ArrayAdapter<String> aaCategoryList = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                DMMemoTools.getCategoryList(this));
        lvCategoryList.setAdapter(aaCategoryList);
        lvCategoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (DMMemoTools.saveMemo(DMSaveFrom.this, lvCategoryList.getItemAtPosition(i).toString(), getIntent().getStringExtra(Intent.EXTRA_TEXT))) {
                    case 0:
                        Toast.makeText(DMSaveFrom.this, R.string.sf_save_success, Toast.LENGTH_SHORT).show();
                        Intent intOpenMemoView = new Intent(DMSaveFrom.this, DMMain.class);
                        intOpenMemoView.putExtra("mvCategoryName", lvCategoryList.getItemAtPosition(i).toString());
                        startActivity(intOpenMemoView);
                        finish();
                        break;
                    case 1:
                        Toast.makeText(DMSaveFrom.this, R.string.mv_save_fail, Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        Toast.makeText(DMSaveFrom.this, R.string.mv_save_fail_empty, Toast.LENGTH_LONG).show();
                        break;
                }

            }
        });
    }
}
