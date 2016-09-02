package com.wisegps.clzx.db;
import java.sql.SQLException;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.wisegps.clzx.entity.CarInfo;

public class DBHelper extends OrmLiteSqliteOpenHelper{
	private final static int DATABASE_VERSION = 2;  
    private static final String DB_NAME = "orm"; 
    private Dao<CarInfo, Integer> carInfoDao;
	  public DBHelper(Context context){
		super(context, DB_NAME, null, DATABASE_VERSION);
	}

	 
	 /**
	  * 创建表 
	  */
	@Override
	public void onCreate(SQLiteDatabase sql, ConnectionSource connectionSource) {
		  try {
			  TableUtils.createTable(connectionSource,  CarInfo.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}  
	}

	 /**
	  * 升级数据库
	  */
	@Override
	public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int arg2,
			int arg3) {
		 try {
			TableUtils.dropTable(connectionSource, CarInfo.class, true);
		} catch (SQLException e) {
			e.printStackTrace();
		}  
         onCreate(sqliteDatabase, connectionSource);  
		
	}
	
	
	

}
