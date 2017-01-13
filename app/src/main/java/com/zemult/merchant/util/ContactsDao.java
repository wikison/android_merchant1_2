package com.zemult.merchant.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.zemult.merchant.bean.ContactDataBean;
import com.zemult.merchant.model.M_Friend;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *         概要: 读取手机上的手机通讯录 <br>
 */
public class ContactsDao {

	private Context context;
	private static final String[] MYPROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.SORT_KEY_PRIMARY, Phone.NUMBER,
			Phone.CONTACT_ID };

	public ContactsDao(Context context) {
		this.context = context;

	}

	/**
	 * see http://blog.chinaunix.net/uid-26930580-id-4137246.html
	 * 
	 * @param listMembers
	 */
	public void getAllContactToFriend(List<M_Friend> listMembers) {
		Uri uri = Phone.CONTENT_URI;
		Cursor cursor = context.getContentResolver().query(uri, MYPROJECTION,
				null, null, Phone.SORT_KEY_PRIMARY);
		try {

			if (cursor.moveToFirst()) {
				do {

					String contact_phone = cursor.getString(2);
					String name = cursor.getString(0);
					String sortKey = getSortKey(cursor.getString(1));
					int contact_id = cursor.getInt(3);

					String number = contact_phone.trim().replace(" ", "");
					if (number.startsWith("+86")) {
						number = number.replace("+86", "");
					}
					if (name != null && isMobileNO(number) == true) {
						M_Friend contact = new M_Friend();
						contact.setFriendId(contact_id);
						contact.setPhone(number) ;
						contact.setName(name);
						listMembers.add(contact);
					}

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				cursor.close();
			}catch (Exception e){
				ToastUtil.showMessage("获取权限失败");
			}
		}
	}


	/**
	 * see http://blog.chinaunix.net/uid-26930580-id-4137246.html
	 *
	 * @param listMembers
	 */
	public void getAllContact(List<ContactDataBean> listMembers) {
		Uri uri = Phone.CONTENT_URI;
		Cursor cursor = context.getContentResolver().query(uri, MYPROJECTION,
				null, null, Phone.SORT_KEY_PRIMARY);
		try {

			if (cursor.moveToFirst()) {
				do {

					String contact_phone = cursor.getString(2);
					String name = cursor.getString(0);
					String sortKey = getSortKey(cursor.getString(1));
					int contact_id = cursor.getInt(3);

					String number = contact_phone.trim().replace(" ", "");
					if (number.startsWith("+86")) {
						number = number.replace("+86", "");
					}
					if (name != null && isMobileNO(number) == true) {
						ContactDataBean contact = new ContactDataBean();
						contact.setId(contact_id);
						contact.phone = number;
						contact.name = name;
						contact.sortKey = sortKey;
						listMembers.add(contact);
					}

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				cursor.close();
			}catch (Exception e){
             ToastUtil.showMessage("获取权限失败");
			}

		}
	}



	/**
	 * 获取sort key的首个字符，如果是英文字母就直接返回，否则返回#。
	 * 
	 * @param sortKeyString
	 *            数据库中读取出的sort key
	 * @return 英文字母或者#
	 */
	private static String getSortKey(String sortKeyString) {
		String key = sortKeyString.substring(0, 1).toUpperCase();
		if (key.matches("[A-Z]")) {
			return key;
		}
		return "#";
	}

	private static boolean isMobileNO(String mobiles) {

		boolean flag = false;
		try {
			Pattern regex = Pattern
					.compile("^[1]([3-8][0-9]{1}|59|58|88|89)[0-9]{8}$");
			Matcher matcher = regex.matcher(mobiles);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	public List<ContactDataBean> removeDuplicateData(List<ContactDataBean> cList) {
		List<ContactDataBean> cList2 = new ArrayList<ContactDataBean>();
		String phone = "";
		for (ContactDataBean data : cList) {

			if (phone.equals(data.getPhone()) == false) {
				cList2.add(data);
				phone = data.getPhone();
			}
		}
		return cList2;
	}


	public List<M_Friend> removeDuplicateDataToFriend(List<M_Friend> cList) {
		List<M_Friend> cList2 = new ArrayList<M_Friend>();
		String phone = "";
		for (M_Friend data : cList) {

			if (phone.equals(data.getPhone()) == false) {
				cList2.add(data);
				phone = data.getPhone();
			}
		}
		return cList2;
	}

}
