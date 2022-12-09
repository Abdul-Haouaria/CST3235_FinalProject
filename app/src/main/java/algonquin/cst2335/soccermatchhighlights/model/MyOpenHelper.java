package algonquin.cst2335.soccermatchhighlights.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MyOpenHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public static final String DATABASE = "MatchesDatabase";
    public static final String TABLE_NAME = "Match";
    public static final String COL_TITLE = "TitleID";
    public static final String COL_MEAL_NAME = "MatchTitle";
    public static final String COL_VIDEO = "Video";

    public MyOpenHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create table MyData ( _id INTEGER PRIMARY KEY AUTOINCREMENT, Message TEXT, SendOrReceive INTEGER);
        // String result = String.format(" %s %s %s", "FirstString" , "10", "10.0" );

        //                                      //TABLE_NAME               take care of id numbers
        db.execSQL( String.format( "Create table %s ( %s INTEGER PRIMARY KEY, %s TEXT, %s  TEXT);"
                , TABLE_NAME, COL_TITLE, COL_MEAL_NAME, COL_VIDEO ) );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table if exists MatchesDatabase"); //deletes the current data
        //create a new table:

        this.onCreate(db);
    }
}