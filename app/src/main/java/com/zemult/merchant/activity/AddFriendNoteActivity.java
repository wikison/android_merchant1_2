package com.zemult.merchant.activity;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.friend.UserFriendAddRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.EditFilter;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;
import zema.volley.network.ResponseListener;

/**
 * Created by wikison on 2016/6/22.
 */
public class AddFriendNoteActivity extends BaseActivity {
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.et_add)
    EditText etAdd;
    int friendId;
    UserFriendAddRequest userFriendAddRequest;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_add_friend_request);
    }

    @Override
    public void init() {
        initView();
    }

    private void initView() {
        lhBtnBack.setVisibility(View.VISIBLE);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhBtnRight.setVisibility(View.VISIBLE);
        lhTvTitle.setText("朋友验证");
        friendId = getIntent().getIntExtra("friendId", -1);

        EditFilter.WordFilter(etAdd, 50);

        showKeyBoard();
    }

    private void showKeyBoard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .showSoftInput(view, 0);
        }
    }


    @OnClick({R.id.ll_back, R.id.lh_btn_back, R.id.lh_btn_right})
    public void viewClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
            case R.id.lh_btn_back:
                finish();
                break;
            case R.id.lh_btn_right:
                addFriend();
                break;
        }

    }

    private void addFriend() {
        loadingDialog.show();
        if (userFriendAddRequest != null) {
            userFriendAddRequest.cancel();
        }
        UserFriendAddRequest.Input input = new UserFriendAddRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();    //	用户id
        input.friendId = friendId;    //	好友的用户id
        input.note = etAdd.getText().toString();
        input.convertJosn();

        userFriendAddRequest = new UserFriendAddRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
            }

            @Override
            public void onResponse(Object response) {
                loadingDialog.dismiss();
                if (((CommonResult) response).status == 1) {
                    ToastUtil.showMessage("请求发送成功噜~~~");
                    setResult(RESULT_OK);
                    finish();
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
                ;

            }
        });
        sendJsonRequest(userFriendAddRequest);
    }

}
