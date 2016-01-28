package com.socioboard.t_board_pro.ui;

/**
 * Created by d4ddy-lild4rk on 11/8/14.
 */
public class Items {

	private String title;// Name
	
	private String item_new;
	
	private int icon;
	
	public Items() {

	}

	public Items(String title, String item_new, int icon) {
		
 		this.title = title;
		this.item_new = item_new;
		this.icon = icon;

	}
	
	public Items(String title,   int icon ) {

		this.title = title;
 		this.icon = icon;
		 
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public String getItem_new() {
		return item_new;
	}

	public void setItem_new(String item_new) {
		this.item_new = item_new;
	}
}
