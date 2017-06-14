package com.socioboard.t_board_pro.util;

public class EntityModel {

	long followers;
	long followings;
	long mutuals;
	long nonfollwers;
    long millis; 
     
	@Override
	public String toString() {
		
		return "EntityModel [followers=" + followers + ", followings="
				+ followings + ", mutuals=" + mutuals + ", nonfollwers="
				+ nonfollwers + ", millis=" + millis + "]";
 	}

	public EntityModel(long followers, long followings, long mutuals,
			long nonfollwers ) {
		super();
		this.followers = followers;
		this.followings = followings;
		this.mutuals = mutuals;
		this.nonfollwers = nonfollwers;
 	}

	public long getMillis() {
		return millis;
	}

	public void setMillis(long millis) {
		this.millis = millis;
	}

	public EntityModel() {
 	}

	public long getFollowers() {
		return followers;
	}

 	public void setFollowers(long followers) {
		this.followers = followers;
	}

	public long getFollowings() {
		return followings;
	}

	public void setFollowings(long followings) {
		this.followings = followings;
	}

	public long getMutuals() {
		return mutuals;
	}

	public void setMutuals(long mutuals) {
		this.mutuals = mutuals;
	}

	public long getNonfollwers() {
		return nonfollwers;
	}

	public void setNonfollwers(long nonfollwers) {
		this.nonfollwers = nonfollwers;
	}

}
