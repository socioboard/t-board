package com.socioboard.t_board_pro.util;

public class ModelUserDatas {

	String userid;
	String username;
	String userAcessToken;
	String usersecretKey;
	String userimage;
	String emailId;

	public String getEmailId() {
		return emailId;
	}

	public String getUsersecretKey() {
		return usersecretKey;
	}

	public void setUsersecretKey(String usersecretKey) {
		this.usersecretKey = usersecretKey;
	}

	public ModelUserDatas(String userid, String username,
			String userAcessToken, String usersecretKey, String userimage,
			String emailId) {
		super();
		this.userid = userid;
		this.username = username;
		this.userAcessToken = userAcessToken;
		this.usersecretKey = usersecretKey;
		this.userimage = userimage;
		this.emailId = emailId;
	}

	@Override
	public String toString() {
		return "ModelUserDatas [userid=" + userid + ", username=" + username
				+ ", userAcessToken=" + userAcessToken + ", usersecretKey="
				+ usersecretKey + ", userimage=" + userimage + ", emailId="
				+ emailId + "]";
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getUserimage() {
		return userimage;
	}

	public void setUserimage(String userimage) {
		this.userimage = userimage;
	}

	public String getUserAcessToken() {
		return userAcessToken;
	}

	public void setUserAcessToken(String userAcessToken) {
		this.userAcessToken = userAcessToken;
	}

	public ModelUserDatas() {
	}

	/**
	 * @return the userid
	 */
	public String getUserid() {
		return userid;
	}

	/**
	 * @param userid
	 *            the userid to set
	 */
	public void setUserid(String userid) {
		this.userid = userid;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the level
	 */

}
