package tv.techm.data.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class DataProvider extends ContentProvider{
	DataProviderHelper mDpHelper;
	
	static final String PARAM_NOTIFY = "notify";
	
	@Override
	public boolean onCreate() {
		mDpHelper = DataProviderHelper.getInstance(getContext());
		return true;
	}
	
	@Override
	public Uri insert (Uri uri, ContentValues cv) {
		DbArguments dbargs = new DbArguments(uri);
		
		SQLiteDatabase db = mDpHelper.getWritableDatabase();
		
		long rowid = db.insert(dbargs.table, null, cv);
		if ( rowid <= 0 )
			return null;
		
		uri = ContentUris.withAppendedId(uri, rowid);
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return uri;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projectionIn, String selection, String[] selectionArgs, String sortOrder){
		DbArguments dbargs = new DbArguments(uri, selection, selectionArgs);
		
		SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
		qBuilder.setTables(dbargs.table);
		
		SQLiteDatabase db = mDpHelper.getReadableDatabase();
		Cursor queryCursor = qBuilder.query(db, projectionIn, dbargs.where, dbargs.whereargs, null, null, sortOrder);
		
		queryCursor.setNotificationUri(getContext().getContentResolver(), uri);
		
		return queryCursor;
	}
	
	public Cursor rawQuery(String sql, String[] selectionArgs){
		SQLiteDatabase db = mDpHelper.getReadableDatabase();
		Cursor queryCursor = db.rawQuery(sql, selectionArgs);
		return queryCursor;
	}

	
	@Override
	public int update(Uri uri, ContentValues cv, String selection, String[] selectionArgs){
		DbArguments dbargs = new DbArguments(uri, selection, selectionArgs);
		SQLiteDatabase db = mDpHelper.getWritableDatabase();
		
		int count = db.update(dbargs.table, cv, dbargs.where, dbargs.whereargs);
		
		if (count > 0)
			getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs){
		DbArguments dbargs = new DbArguments(uri, selection, selectionArgs);
		SQLiteDatabase db = mDpHelper.getWritableDatabase();
		
		int count = db.delete(dbargs.table, dbargs.where, dbargs.whereargs);
		
		if(count > 0)
			getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
	}
	
	@Override
	public String getType(Uri uri){
		DbArguments dbargs = new DbArguments(uri,null,null);
		if(TextUtils.isEmpty(dbargs.where))
			return "vnd.android.cursor.dir/" + dbargs.table;
		else
			return "vnd.android.cursor.item/" + dbargs.table;
	}
	
	static class DbArguments {
        public final String table;
        public final String where;
        public final String[] whereargs;

        DbArguments(Uri url, String where, String[] args) {
            if (url.getPathSegments().size() == 1) {
                this.table = url.getPathSegments().get(0);
                this.where = where;
                this.whereargs = args;
            } else if (url.getPathSegments().size() != 2) {
                throw new IllegalArgumentException("Invalid URI: " + url);
            } else if (!TextUtils.isEmpty(where)) {
                throw new UnsupportedOperationException 
                    ("WHERE clause not supported: " + url);
            } else {
                this.table = url.getPathSegments().get(0);
                this.where = "_id=" + ContentUris.parseId(url);                
                this.whereargs = null;
            }
        }

        DbArguments(Uri url) {
            if (url.getPathSegments().size() == 1) {
                table = url.getPathSegments().get(0);
                where = null;
                whereargs = null;
            } else {
                throw new IllegalArgumentException("Invalid URI: " + url);
            }
        }
    }
}

