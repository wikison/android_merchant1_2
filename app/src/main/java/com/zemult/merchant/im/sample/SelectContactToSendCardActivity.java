package com.zemult.merchant.im.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.util.YWLog;
import com.alibaba.mobileim.contact.IYWContact;
import com.zemult.merchant.R;
import com.zemult.merchant.fragment.SfriendFragment;

import java.util.ArrayList;
import java.util.List;

public class SelectContactToSendCardActivity extends FragmentActivity {

    private static final String TAG = "SelectContactToSendCardActivity";

    private YWIMKit mIMKit;
//    private ContactsFragment mFragment;
    private SfriendFragment mFragment;
    ArrayList userIdlist=new ArrayList();
    int selectedtype;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity_select_contact);

        mIMKit = LoginSampleHelper.getInstance().getIMKit();
        selectedtype=getIntent().getIntExtra("selectedtype",1);
        initTitle();
        createFragment();
        YWLog.i(TAG, "onCreate");
    }

    private void initTitle(){
        RelativeLayout titleBar = (RelativeLayout) findViewById(R.id.title_bar);
        titleBar.setBackgroundColor(Color.parseColor("#FFA726"));
        titleBar.setVisibility(View.VISIBLE);

        TextView titleView = (TextView) findViewById(R.id.title_self_title);
        TextView leftButton = (TextView) findViewById(R.id.left_button);
        leftButton.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.demo_common_back_btn_white, 0, 0, 0);
        leftButton.setTextColor(Color.WHITE);
        leftButton.setText("取消");
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        titleView.setTextColor(Color.WHITE);
        titleView.setText("选择联系人");


        TextView rightButton = (TextView) findViewById(R.id.right_button);
        rightButton.setText("完成");
        rightButton.setTextColor(Color.WHITE);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<IYWContact> list = mFragment.getSelectIYWContact();
                if (list != null && list.size() > 0){
                    ChattingOperationCustomSample.selectContactListener.onSelectCompleted(list,selectedtype);
                    finish();
                }
            }
        });
    }

    private void createFragment(){
//        mFragment =mIMKit.getContactsFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString(ContactsFragment.SEND_CARD, ContactsFragment.SEND_CARD);
//        mFragment.setArguments(bundle);
//        getSupportFragmentManager().beginTransaction().replace(R.id.contact_list_container, mFragment).commit();

        mFragment=new SfriendFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("userIdlist",userIdlist);
        mFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.contact_list_container, mFragment).commit();
    }
}
