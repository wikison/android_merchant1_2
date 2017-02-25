package com.zemult.merchant.im.sample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.YWAccount;
import com.alibaba.mobileim.channel.constant.YWProfileSettingsConstants;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.contact.YWContactManager;
import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.YWConversation;
import com.alibaba.mobileim.fundamental.widget.WXNetworkImageView;
import com.alibaba.mobileim.fundamental.widget.WxAlertDialog;
import com.alibaba.mobileim.utility.IMNotificationUtils;
import com.zemult.merchant.R;
import com.zemult.merchant.app.base.MBaseActivity;

public class ContactSettingActivity extends MBaseActivity {

    private WXNetworkImageView contactHead;
    private TextView contactShowName;
    private ImageView msgRemindSwitch;
    private RelativeLayout clearMsgRecordLayout;
    private String appKey;
    private String userId;
    private int msgRecFlag = YWProfileSettingsConstants.RECEIVE_PEER_MSG;
    private YWAccount account;

    private YWContactManager contactManager;
    private IYWContact contact;

    private IYWConversationService conversationService;
    private YWConversation conversation;
    private Handler uiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent != null) {
            appKey = intent.getStringExtra("AppKey");
            userId = intent.getStringExtra("UserId");
        }
        uiHandler = new Handler(Looper.getMainLooper());
        account = LoginSampleHelper.getInstance().getIMKit().getIMCore();
        contactManager = (YWContactManager) LoginSampleHelper.getInstance().getIMKit().getContactService();
        contact = contactManager.getContactProfileInfo(userId, appKey);
        conversationService = LoginSampleHelper.getInstance().getIMKit().getConversationService();
        conversation = conversationService.getConversationByUserId(userId);
        if(contactManager != null) {
            msgRecFlag = contactManager.getMsgRecFlagForContact(userId, appKey);
        }
        setContentView(R.layout.demo_activity_contact_setting);
        initViews();
    }

    public static Intent getContactSettingActivityIntent(Context context, String appKey, String userId) {
        Intent intent = new Intent(context, ContactSettingActivity.class);
        intent.putExtra("AppKey", appKey);
        intent.putExtra("UserId", userId);
        return intent;
    }

    private void initViews() {
        contactHead = (WXNetworkImageView) findViewById(R.id.head);
        contactHead.setImageUrl(contact.getAvatarPath());
//        imageManager.loadCircleImage(, contactHead);

        contactShowName = (TextView) findViewById(R.id.contact_show_name);
        if (contact != null){
            contactShowName.setText(contact.getShowName());
        }else{
            contactShowName.setText(userId);
        }

        msgRemindSwitch = (ImageView) findViewById(R.id.receive_msg_remind_switch);
        clearMsgRecordLayout = (RelativeLayout) findViewById(R.id.clear_msg_record);
        if(msgRecFlag != YWProfileSettingsConstants.RECEIVE_PEER_MSG_NOT_REMIND) {
            msgRemindSwitch.setImageResource(R.mipmap.on_switch);
        } else {
            msgRemindSwitch.setImageResource(R.mipmap.off_switch);
        }
        msgRemindSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMsgRecType();
            }
        });
        clearMsgRecordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearMsgRecord();
            }
        });
        initTitle();
    }

    private void initTitle() {
        View titleView = findViewById(R.id.title_bar);
        titleView.setVisibility(View.VISIBLE);
        titleView.setBackgroundColor(Color.parseColor("#ff333333"));//#ff333333

        TextView leftButton = (TextView) findViewById(R.id.left_button);
        leftButton.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.back_btn, 0, 0, 0);
        leftButton.setText("");
//        leftButton.setTextColor(Color.parseColor("#FFA726"));
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView title = (TextView) findViewById(R.id.title_self_title);
        title.setText("聊天设置");
        title.setTextColor(Color.WHITE);
    }

    private void setMsgRecType() {
        if (contact == null){
            return;
        }
        if(msgRecFlag != YWProfileSettingsConstants.RECEIVE_PEER_MSG_NOT_REMIND) {
            contactManager.setContactMsgRecType(contact, YWProfileSettingsConstants.RECEIVE_PEER_MSG_NOT_REMIND, 10, new SettingsCallback());
        } else {
            contactManager.setContactMsgRecType(contact, YWProfileSettingsConstants.RECEIVE_PEER_MSG, 10, new SettingsCallback());
        }
    }

    class SettingsCallback implements IWxCallback {

        @Override
        public void onError(int code, String info) {
            IMNotificationUtils.getInstance().showToast("onError:"+ " code: " + code + "info:" + info, ContactSettingActivity.this);
        }

        @Override
        public void onProgress(int progress) {

        }

        @Override
        public void onSuccess(Object... result) {
            if(contact != null) {
                msgRecFlag = contactManager.getMsgRecFlagForContact(contact);
            } else {
                msgRecFlag = contactManager.getMsgRecFlagForContact(userId, appKey);
            }
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(msgRecFlag != YWProfileSettingsConstants.RECEIVE_PEER_MSG_NOT_REMIND) {
                        msgRemindSwitch.setImageResource(R.mipmap.on_switch);
                    } else {
                        msgRemindSwitch.setImageResource(R.mipmap.off_switch);
                    }
//                    IMNotificationUtils.showToast(
//                            "onSuccess:" + msgRecFlag,
//                            ContactSettingActivity.this);
                }
            });
        }
    }

    protected void clearMsgRecord() {
        String message = "清空的消息再次漫游时不会出现。你确定要清空聊天消息吗？";
//        if (mConversation.getConversationType() == ConversationType.WxConversationType.Room) {
//            message = getResources().getString(
//                    R.string.clear_roomchatting_msg_confirm);
//        }
        AlertDialog.Builder builder = new WxAlertDialog.Builder(ContactSettingActivity.this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                conversation.getMessageLoader().deleteAllMessage();
                                IMNotificationUtils.getInstance().showToast("记录已清空",
                                        ContactSettingActivity.this);
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
        builder.create().show();
    }

}
