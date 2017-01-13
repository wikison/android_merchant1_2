package com.zemult.merchant.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import com.zemult.merchant.R;
import com.zemult.merchant.app.MAppCompatActivity;

public class ConversationFragmentActivity extends MAppCompatActivity {
    private Fragment conversationFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_fragment);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction = fragmentManager.beginTransaction();
        if (conversationFragment == null) {
            conversationFragment=MainActivity.mIMKit.getConversationFragment()  ;
            transaction.add(R.id.content, conversationFragment);
        } else {
            transaction.show(conversationFragment);
        }
        transaction.commitAllowingStateLoss();
    }
}
