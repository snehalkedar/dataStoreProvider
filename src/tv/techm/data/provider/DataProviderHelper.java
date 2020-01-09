package tv.techm.data.provider;

import java.util.ArrayList;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.os.Bundle;
import android.util.Log;

public class DataProviderHelper extends SQLiteOpenHelper{
	private static final String TAG = "DataProviderHelper";
	
	private static final String DATABASE_NAME = "datastore.db";
	
	static final int DATABASE_VERSION = 1;
	
	static final String AUTHORITY ="tv.techm.data.provider";
	
	private static DataProviderHelper mSingleton = null;
	
	ArrayList<String> createTableList = new ArrayList<String>();
	ArrayList<String> destroyTableList = new ArrayList<String>();
	
	public static synchronized DataProviderHelper getInstance (Context context){
		Log.v(TAG, "getInstance()");
		if(mSingleton == null){
			mSingleton = new DataProviderHelper(context);
		}
		return mSingleton;
	}
	
	public DataProviderHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.v(TAG, "  ----   DataProviderHelper constructor()");
		initBaseTables();
	}

	public void onCreate(SQLiteDatabase sdb) {
		Log.v(TAG, " -----   onCreate");
		if(createTableList!=null)
			for(int i=0; i<createTableList.size();i++)
				sdb.execSQL(createTableList.get(i));
	}
	
	public void onUpgrade(SQLiteDatabase sdb, int oldVersion, int newVersion) {
		Log.v(TAG, "----   onUpgrade");
		Log.v(TAG, "Upgrading database which will destroy all old data");
		if(destroyTableList!=null) {
			for(int k=0;k<destroyTableList.size();k++) {
				String DROP_QUERY = "DROP TABLE IF EXISTS " + destroyTableList.get(k) + ";";
				sdb.execSQL(DROP_QUERY);
			}
		}
        onCreate(sdb);
	}
	

	private void initBaseTables() {
		Log.v(TAG, "initBaseTables");
		String AuthTable = new String("CREATE TABLE auth (_id INTEGER PRIMARY KEY, key TEXT, value TEXT );");
		String DeviceTable = "CREATE TABLE device (_id INTEGER PRIMARY KEY, timestamp TEXT, device_id TEXT, serial_no TEXT, subscriber_id TEXT," +
				" shared_key TEXT, tvressetting TEXT, tvresolutionenum TEXT, aspectratio TEXT );";
		String LauncherTable = "CREATE TABLE launcher (_id INTEGER PRIMARY KEY, type TEXT NOT NULL, title TEXT, url TEXT, screen INTEGER," +
				" x INTEGER, y INTEGER, package TEXT, iconname TEXT, changeflag INTEGER);" ;
			
		createTableList.add(AuthTable);
		createTableList.add(DeviceTable);
		createTableList.add(LauncherTable);
		
		destroyTableList.add("auth");
		destroyTableList.add("device");
		destroyTableList.add("launcher");
	}
}

