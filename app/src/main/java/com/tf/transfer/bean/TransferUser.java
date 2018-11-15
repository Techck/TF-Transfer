package com.tf.transfer.bean;

import java.io.Serializable;

public class TransferUser implements Serializable{

	private String username;
	private static TransferUser transferUser;
	
	public static TransferUser getInstance(){
		if(transferUser == null){
			synchronized (TransferUser.class) {
				if (transferUser == null)
					transferUser = new TransferUser();
			}
		}
		return transferUser;
	}

	private TransferUser() {
		super();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
}
