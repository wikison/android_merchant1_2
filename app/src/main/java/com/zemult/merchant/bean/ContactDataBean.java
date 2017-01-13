package com.zemult.merchant.bean;

import java.io.Serializable;

/**
 *         概要：手机上通讯录 <br>
 *
 */
public class ContactDataBean implements Serializable {
	public int id;
	public String name;
	public String phone;
	public String sortKey;
	public boolean isIvited;

	public boolean isIvited() {
		return isIvited;
	}

	public void setIvited(boolean ivited) {
		isIvited = ivited;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSortKey() {
		return sortKey;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof ContactDataBean) {
			ContactDataBean u = (ContactDataBean) obj;
			return this.name.equals(u.name) && this.phone.equals(phone)
					&& this.sortKey.equals(sortKey);
		}
		return super.equals(obj);
	}

}
