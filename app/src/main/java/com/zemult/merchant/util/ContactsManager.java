package com.zemult.merchant.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 联系人
 */
public class ContactsManager {

    private static ContactsManager instance;
    Set<String> contactDataSet=new HashSet();

    public static ContactsManager instance() {
        if (instance == null) {
            instance = new ContactsManager();
        }
        return instance;
    }


    public Set getContactDataSet() {
            String[] contactDataArry  = SlashHelper.getSettingString(SlashHelper.User.Key.CONTACTSLIST,"").split(",");
        for(int i=0;0<contactDataArry.length;i++){
            contactDataSet.add(contactDataArry[i]);
        }
       return contactDataSet ;
    }

    public void saveContactDataSet(String phoneNo) {
        contactDataSet.add(phoneNo);
        String contacts= Arrays.toString( contactDataSet.toArray()).replace("[","").replace("]","");
        SlashHelper.setSettingString(SlashHelper.User.Key.CONTACTSLIST,contacts);
    }
}
