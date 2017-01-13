package com.zemult.merchant.im.sample;

import com.alibaba.mobileim.contact.IYWContact;

import java.util.List;

/**
 * Created by mayongge on 15-11-2.
 */
public interface ISelectContactListener {

    public void onSelectCompleted(List<IYWContact> contacts, int selectedtype);//selectedtype 1单聊 2群聊
}
