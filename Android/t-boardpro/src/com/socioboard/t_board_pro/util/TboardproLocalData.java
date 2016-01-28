package com.socioboard.t_board_pro.util;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TboardproLocalData extends SQLiteOpenHelper {

	// /////////////////////////////////////////////////////

	public static final String db_name = "cencryptedtbpb.db";

	public static final String table_name = "twittermanytable";

	public static final String table_followers = "table_followers";

	public static final String table_stats = "table_stats";

	public static final String table_dm_records = "table_dm_records";

	public static final String table_dm_sending_records = "table_dm_sending_records";

	public static final String sch_table_name = "schedullertable";

	public static final String KEY_UserID = "userid";

	public static final String KEY_Userimage = "userimage";

	public static final String KEY_Username = "username";

	public static final String KEY_UserAcessToken = "useracesstoken";

	public static final String KEY_UserSecretKey = "usersecretkey";

	public static final String KEY_SchTID = "schtid";

	public static final String KEY_Tweet = "tweet";

	public static final String KEY_TwtTime = "tweettimestamp";

	public static final String KEY_followers_ids = "recent_ids";

	public static final String KEY_DM_sent_ids = "dm_ids";

	public static final String KEY_DM_sending_ids = "dm_ids";

	// ////////////////////////////////////////////////////////

	public static final String KEY_FOLLOWING = "followings";
	public static final String KEY_NONFOLLWERS = "nonfollowers";
	public static final String KEY_FOLLOWERS = "followers";
	public static final String KEY_MUTUAL = "mutuals";
	public static final String KEY_TIMESTAMP = "timestamp";

	// ////////////////////////////////////////////////////////

	public TboardproLocalData(Context context) {

		super(context, db_name, null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		onCreate(db);
	}

	public void CreateTable() {

		String querry = "CREATE TABLE IF NOT EXISTS " + table_name + "("
				+ KEY_UserID + " TEXT," + KEY_Userimage + " TEXT,"
				+ KEY_Username + " TEXT," + KEY_UserAcessToken + " TEXT,"
				+ KEY_UserSecretKey + " TEXT)";

		String querry2 = "CREATE TABLE IF NOT EXISTS " + sch_table_name + "("
				+ KEY_SchTID + " INTEGER," + KEY_UserID + " TEXT," + KEY_Tweet
				+ " TEXT," + KEY_TwtTime + " INTEGER)";

		String querry3 = "CREATE TABLE IF NOT EXISTS " + table_dm_records + "("
				+ KEY_DM_sent_ids + " TEXT," + KEY_UserID + " TEXT)";

		String querry4 = "CREATE TABLE IF NOT EXISTS "
				+ table_dm_sending_records + "(" + KEY_DM_sending_ids
				+ " TEXT," + KEY_UserID + " TEXT)";

		String querry5 = "CREATE TABLE IF NOT EXISTS " + table_followers + "("
				+ KEY_followers_ids + " TEXT," + KEY_UserID + " TEXT)";

		String querry6 = "CREATE TABLE IF NOT EXISTS " + table_stats + "("
				+ KEY_FOLLOWING + " TEXT," + KEY_FOLLOWERS + " TEXT,"
				+ KEY_MUTUAL + " TEXT," + KEY_NONFOLLWERS + " TEXT,"
				+ KEY_TIMESTAMP + " TEXT," + KEY_UserID + " TEXT)";

		SQLiteDatabase database = this.getWritableDatabase();

		database.execSQL(querry);

		database.execSQL(querry2);

		database.execSQL(querry3);

		database.execSQL(querry4);

		database.execSQL(querry5);

		database.execSQL(querry6);

		System.out.println("CreateTable " + querry);

		System.out.println("CreateTable2 " + querry2);

		System.out.println("querry3 " + querry3);

		System.out.println("querry4 " + querry4);

		System.out.println("querry5 " + querry5);

		System.out.println("querry6 " + querry6);

	}

	public void addNewUserAccount(ModelUserDatas modelUserDatas) {

		// String query = "INSERT INTO " + table_name + "";
		SQLiteDatabase database = this.getWritableDatabase();

		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_UserID, modelUserDatas.getUserid());
		contentValues.put(KEY_Userimage, modelUserDatas.getUserimage());
		contentValues.put(KEY_Username, modelUserDatas.getUsername());
		contentValues.put(KEY_UserAcessToken,
				modelUserDatas.getUserAcessToken());
		contentValues.put(KEY_UserSecretKey, modelUserDatas.getUsersecretKey());

		database.insert(table_name, null, contentValues);

		System.out.println("addNewUserAccount " + contentValues);

	}

	public ModelUserDatas getUserData(String userId) {

		ModelUserDatas modelUserDatas = null;

		String query = "SELECT * FROM " + table_name + " WHERE " + KEY_UserID
				+ " = '" + userId + "'";

		SQLiteDatabase database = this.getReadableDatabase();

		Cursor cursor = database.rawQuery(query, null);

		if (cursor.moveToFirst()) {

			modelUserDatas = new ModelUserDatas();
			modelUserDatas.setUserid(cursor.getString(0));
			modelUserDatas.setUserimage(cursor.getString(1));
			modelUserDatas.setUsername(cursor.getString(2));
			modelUserDatas.setUserAcessToken(cursor.getString(3));
			modelUserDatas.setUsersecretKey(cursor.getString(4));

		}

		return modelUserDatas;
	}

	public HashMap<String, ModelUserDatas> getAllUsersData() {

		String query = "SELECT * FROM " + table_name;

		HashMap<String, ModelUserDatas> allUserDetails = new HashMap<String, ModelUserDatas>();

		System.out.println(query);

		SQLiteDatabase database = this.getReadableDatabase();

		Cursor cursor = database.rawQuery(query, null);

		ModelUserDatas modelUserDatas;

		if (cursor.moveToFirst()) {

			do {

				modelUserDatas = new ModelUserDatas();
				modelUserDatas.setUserid(cursor.getString(0));
				modelUserDatas.setUserimage(cursor.getString(1));
				modelUserDatas.setUsername(cursor.getString(2));
				modelUserDatas.setUserAcessToken(cursor.getString(3));
				modelUserDatas.setUsersecretKey(cursor.getString(4));
				allUserDetails.put(cursor.getString(0), modelUserDatas);

			} while (cursor.moveToNext());
		}

		return allUserDetails;
	}

	public ArrayList<ModelUserDatas> getAllUsersDataArlist() {

		String query = "SELECT * FROM " + table_name;

		ArrayList<ModelUserDatas> allUserDetails = new ArrayList<ModelUserDatas>();

		System.out.println(query);

		SQLiteDatabase database = this.getReadableDatabase();

		Cursor cursor = database.rawQuery(query, null);

		ModelUserDatas modelUserDatas;

		if (cursor.moveToFirst()) {

			do {

				modelUserDatas = new ModelUserDatas();
				modelUserDatas.setUserid(cursor.getString(0));
				modelUserDatas.setUserimage(cursor.getString(1));
				modelUserDatas.setUsername(cursor.getString(2));
				modelUserDatas.setUserAcessToken(cursor.getString(3));
				modelUserDatas.setUsersecretKey(cursor.getString(4));
				allUserDetails.add(modelUserDatas);

			} while (cursor.moveToNext());
		}

		return allUserDetails;
	}

	public void updateUserData(ModelUserDatas modelUserDatas) {

		SQLiteDatabase database = this.getWritableDatabase();

		String updateQuery = "UPDATE " + table_name + " SET " + KEY_Username
				+ " = '" + modelUserDatas.getUsername() + "' , "
				+ KEY_Userimage + " = '" + modelUserDatas.getUserimage()
				+ "' , " + KEY_UserSecretKey + " = '"
				+ modelUserDatas.getUsersecretKey() + "' , "
				+ KEY_UserAcessToken + " = '"
				+ modelUserDatas.getUserAcessToken() + "' " + " WHERE "
				+ KEY_UserID + " = '" + modelUserDatas.getUserid() + "'";

		System.out.println("updateQuery " + modelUserDatas);

		database.execSQL(updateQuery);

	}

	public void updateUserData(String userId, String KEY, String dataValue) {

		SQLiteDatabase database = this.getWritableDatabase();

		String updateQuery = "UPDATE " + table_name + " SET " + KEY + " = '"
				+ dataValue + "' " + " WHERE " + KEY_UserID + " = '" + userId
				+ "'";

		System.out.println("updateUserData" + userId);

		database.execSQL(updateQuery);

	}

	public void updateUserDataField(String userId, String KEY, String value) {

		SQLiteDatabase database = this.getWritableDatabase();

		String updateQuery = "UPDATE " + table_name +

		" SET " + KEY + " = '" + value + "'"

		+ " WHERE "

		+ KEY_UserID + " = '" + userId + "'";

		System.out.println("updateUserDataField" + userId);

		database.execSQL(updateQuery);

	}

	public void deleteAllRows() {

		SQLiteDatabase database = this.getWritableDatabase();

		String query = "DELETE FROM " + table_name;

		System.out.println(query);

		database.execSQL(query);

	}

	public void deleteThisUserData(String userID) {

		SQLiteDatabase database = this.getWritableDatabase();

		String query = "DELETE FROM " + table_name + " WHERE " + KEY_UserID
				+ " = " + userID;

		System.out.println(query);

		database.execSQL(query);

	}

	public ArrayList<String> getAllIds() {

		String query = "SELECT " + KEY_UserID + "  FROM " + table_name;

		ArrayList<String> allUserIDs = new ArrayList<String>();

		System.out.println(query);

		SQLiteDatabase database = this.getReadableDatabase();

		Cursor cursor = database.rawQuery(query, null);

		if (cursor.moveToFirst()) {

			do {

				allUserIDs.add(cursor.getString(0));

			} while (cursor.moveToNext());
		}

		return allUserIDs;
	}

	// SCHEDULLED TWEET;

	public void addNewSchedulledTweet(SchTweetModel schTweetModel) {

		SQLiteDatabase database = this.getWritableDatabase();

		ContentValues contentValues = new ContentValues();

		contentValues.put(KEY_UserID, schTweetModel.getUserID());

		contentValues.put(KEY_SchTID, schTweetModel.getTweetId());

		contentValues.put(KEY_TwtTime, schTweetModel.getTweettime());

		contentValues.put(KEY_Tweet, schTweetModel.getTweet());

		database.insert(sch_table_name, null, contentValues);

		System.out.println("addNewSchedulledTweet " + contentValues);

	}

	public SchTweetModel getSchedulledTweet(String schId) {

		SchTweetModel schTweetModel = null;

		String query = "SELECT * FROM " + sch_table_name + " WHERE "
				+ KEY_SchTID + " = '" + schId + "'";

		SQLiteDatabase database = this.getReadableDatabase();

		Cursor cursor = database.rawQuery(query, null);

		if (cursor.moveToFirst()) {

			int tweetId = Integer.parseInt(cursor.getString(0));

			String Userid;

			Userid = cursor.getString(1);

			String tweet = cursor.getString(2);

			long tweettime = new Long(cursor.getString(3));

			schTweetModel = new SchTweetModel(tweet, tweettime, Userid, tweetId);

		}

		return schTweetModel;
	}

	public ArrayList<SchTweetModel> getAllSchedulledTweet() {

		String query = "SELECT * FROM " + sch_table_name;

		ArrayList<SchTweetModel> allschTweets = new ArrayList<SchTweetModel>();

		System.out.println(query);

		SQLiteDatabase database = this.getReadableDatabase();

		Cursor cursor = database.rawQuery(query, null);

		SchTweetModel schTweetModel;

		if (cursor.moveToFirst()) {

			do {

				int tweetId = Integer.parseInt(cursor.getString(0));

				String Userid;

				Userid = cursor.getString(1);

				String tweet = cursor.getString(2);

				long tweettime = new Long(cursor.getString(3));

				schTweetModel = new SchTweetModel(tweet, tweettime, Userid,
						tweetId);

				allschTweets.add(schTweetModel);

			} while (cursor.moveToNext());
		}

		return allschTweets;

	}

	public void deleteThisTweet(int schid) {

		SQLiteDatabase database = this.getWritableDatabase();

		String query = "DELETE FROM " + sch_table_name + " WHERE " + KEY_SchTID
				+ " = " + schid;

		System.out.println(query);

		database.execSQL(query);

	}

	public ArrayList<String> getAllSentIDs(String Userid) {

		ArrayList<String> listIDs = new ArrayList<String>();

		String query = "SELECT * FROM " + table_dm_records + " WHERE "
				+ KEY_UserID + " = '" + Userid + "'";

		SQLiteDatabase liteDatabase = this.getReadableDatabase();

		Cursor cursor = liteDatabase.rawQuery(query, null);

		if (cursor.moveToFirst()) {

			do {

				listIDs.add(cursor.getString(0));

			} while (cursor.moveToNext());

		}

		System.out.println("listIDs ==== " + listIDs);

		return listIDs;
	}

	public void addNewDMsentId(String id, String Userid) {

		SQLiteDatabase liteDatabase = this.getWritableDatabase();

		ContentValues contentValues = new ContentValues();

		contentValues.put(KEY_DM_sent_ids, id);

		contentValues.put(KEY_UserID, Userid);

		liteDatabase.insert(table_dm_records, null, contentValues);

		System.out.println("Added id ==== " + id);

	}

	public ArrayList<String> getAllSendingIDs(String Userid) {

		ArrayList<String> listIDs = new ArrayList<String>();

		String query = "SELECT * FROM " + table_dm_sending_records + " WHERE "
				+ KEY_UserID + " = '" + Userid + "'";

		SQLiteDatabase liteDatabase = this.getReadableDatabase();

		Cursor cursor = liteDatabase.rawQuery(query, null);

		if (cursor.moveToFirst()) {

			do {

				listIDs.add(cursor.getString(0));

			} while (cursor.moveToNext());

		}

		System.out.println("listIDs ==== " + listIDs);

		return listIDs;
	}

	public void addNewDMsendingId(String id, String Userid) {

		SQLiteDatabase liteDatabase = this.getWritableDatabase();

		ContentValues contentValues = new ContentValues();

		contentValues.put(KEY_DM_sending_ids, id);

		contentValues.put(KEY_UserID, Userid);

		liteDatabase.insert(table_dm_sending_records, null, contentValues);

		System.out.println("Added id ==== " + id);

	}

	public void deleteThisDMSendingId(String dmId, String userId) {

		String query = "DELETE FROM " + table_dm_sending_records + " WHERE "
				+ KEY_UserID + " = '" + userId + "' AND " + KEY_DM_sending_ids
				+ " = '" + dmId + "'";

		SQLiteDatabase liteDatabase = this.getReadableDatabase();

		liteDatabase.execSQL(query);

		System.out.println(" ==== deleteThisDMSendingId ==== " + query);

	}

	// Recent

	public void addFollwersIds(String jsonIds, String Userid) {

		SQLiteDatabase liteDatabase = this.getWritableDatabase();

		ContentValues contentValues = new ContentValues();

		contentValues.put(KEY_followers_ids, jsonIds);

		contentValues.put(KEY_UserID, Userid);

		liteDatabase.insert(table_followers, null, contentValues);

		System.out.println("addFollwersIds ==== ");

	}

	public String getAllFollowersIDs(String Userid) {

		String jsonResult = null;

		String query = "SELECT * FROM " + table_followers + " WHERE "
				+ KEY_UserID + " = '" + Userid + "'";

		SQLiteDatabase liteDatabase = this.getReadableDatabase();

		Cursor cursor = liteDatabase.rawQuery(query, null);

		if (cursor.moveToFirst()) {

			jsonResult = cursor.getString(0);

		}

		System.out.println("jsonResult ==== " + jsonResult);

		return jsonResult;
	}

	public void deleteAllFollowers(String userID) {

		String query = "DELETE FROM " + table_followers + " WHERE "
				+ KEY_UserID + " = '" + userID + "'";

		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

		sqLiteDatabase.execSQL(query);

	}

	public void addUserEntity(EntityModel entityModel, String userId) {

		SQLiteDatabase liteDatabase = this.getWritableDatabase();

		ContentValues contentValues = new ContentValues();

		contentValues.put(KEY_FOLLOWING, entityModel.getFollowings());

		contentValues.put(KEY_FOLLOWERS, entityModel.getFollowers());

		contentValues.put(KEY_MUTUAL, entityModel.getMutuals());

		contentValues.put(KEY_NONFOLLWERS, entityModel.getNonfollwers());

		contentValues.put(KEY_TIMESTAMP, entityModel.getMillis());

		contentValues.put(KEY_UserID, MainSingleTon.currentUserModel.getUserid());

		liteDatabase.insert(table_stats, null, contentValues);

		System.out.println("entityModel = "+entityModel);
		
	}

	public ArrayList<EntityModel> getAllUsersEntity(String userId) {

		ArrayList<EntityModel> entityModels = new ArrayList<EntityModel>();

		String sql = "SELECT * FROM " + table_stats + " WHERE " + KEY_UserID
				+ " = '" + userId + "'";

		SQLiteDatabase database = this.getReadableDatabase();

		Cursor cursor = database.rawQuery(sql, null);

		if (cursor.moveToFirst()) {

			do {

				EntityModel entityModel = new EntityModel();
				
 				entityModel.setFollowings(cursor.getLong(0));
				entityModel.setFollowers(cursor.getLong(1));
				entityModel.setMutuals(cursor.getLong(2));
				entityModel.setNonfollwers(cursor.getLong(3));
 				entityModel.setMillis(cursor.getLong(4));

				entityModels.add(entityModel);

			} while (cursor.moveToNext());

		}

		return entityModels;
	}

}
