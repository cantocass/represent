package com.familyfunctional.whorepresentsme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {

    public static final String KEY_MEMBER_LIST = "keyMemberList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        ArrayList<Member> members = getIntent().getExtras().getParcelableArrayList(KEY_MEMBER_LIST);
        MemberAdapter memberAdapter = new MemberAdapter(this, members);
        ListView listView = (ListView) findViewById(R.id.results_list);
        listView.setAdapter(memberAdapter);
    }
}
