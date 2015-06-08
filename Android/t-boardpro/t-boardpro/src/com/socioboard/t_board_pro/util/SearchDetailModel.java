package com.socioboard.t_board_pro.util;

import java.util.ArrayList;

public class SearchDetailModel {

	ArrayList<ToFollowingModel> searchList = new ArrayList<ToFollowingModel>();
	
	String searchText;

	public ArrayList<ToFollowingModel> getSearchList() {
		return searchList;
	}

	public void setSearchList(ArrayList<ToFollowingModel> searchList) {
		this.searchList = searchList;
	}

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public SearchDetailModel(ArrayList<ToFollowingModel> searchList,
			String searchText) {
		super();
		this.searchList = searchList;
		this.searchText = searchText;
	}
	
	public SearchDetailModel( ) {
		searchList = new ArrayList<ToFollowingModel>();
		searchText = "";
	}
	
}
