package com.socioboard.t_board_pro.dialog;

import com.socioboard.t_board_pro.util.ToFollowingModel;

import android.app.Dialog;
import android.content.Context;

public class ShowUserListDialog {

	Dialog dialog;

	Context context;

	String fetchUsersUrl;

	ToFollowingModel followingModel;

	public ShowUserListDialog(Context context, String fetchUsersUrl,
			ToFollowingModel followingModel) {
		this.context = context;
		this.fetchUsersUrl = fetchUsersUrl;
		this.followingModel = followingModel;
		
	}

	
	void intitDialog(){
		
		dialog = new Dialog(context);
				
		
	}
}
