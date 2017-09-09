package palarax.com.logbook.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Class that handles DB creation, upgrading, reading and writing
 *
 * @author Ilya Thai (11972078)
 * @version 1.0
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName(); //TAG used in Logs

    private static DatabaseHelper sInstance;

    private static final String DATABASE_NAME = "database_name";
    private static final String DATABASE_TABLE = "table_name";
    private static final int DATABASE_VERSION = 1;

    // Table Name
    private static final String TABLE_TRAINS = "trains";

    // Trains Table Columns
    private static final String KEY_TRAINS_ID = "id";
    private static final String KEY_TRAINS_PLATFORM = "platform";
    private static final String KEY_TRAINS_ARRIVAL_TIME = "arrival_time";
    private static final String KEY_TRAINS_STATUS = "status";
    private static final String KEY_TRAINS_DESTINATION = "destination";
    private static final String KEY_TRAINS_DESTINATION_TIME = "destination_time";


    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static method "getInstance()" instead.
     *
     * @param context context of current state of application
     */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TRAINS_TABLE = "CREATE TABLE " + TABLE_TRAINS +
                "(" +
                KEY_TRAINS_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_TRAINS_PLATFORM + " TEXT," +
                KEY_TRAINS_ARRIVAL_TIME + " INTEGER," +
                KEY_TRAINS_STATUS + " TEXT," +
                KEY_TRAINS_DESTINATION + " TEXT," +
                KEY_TRAINS_DESTINATION_TIME + " TEXT" +
                ")";
        db.execSQL(CREATE_TRAINS_TABLE);
    }


    //If the db ever needs to be upgraded example
    //private static final String DATABASE_ALTER_TEAM_1 = "ALTER TABLE "+ TABLE_TEAM + " ADD COLUMN " + COLUMN_COACH + " string;";

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion != newVersion) {
            switch (oldVersion) {
                case 1: //TODO upgrade
                case 2: //TODO upgrade
                    break;
                default:
                    // Hope it never gets here -.-
                    db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAINS);
                    onCreate(db);
            }
        }
    }

    /**
     * Add train to the Database
     * @param train train object to add
     */
//    public void addTrain(Train train) {
//        // Create and/or open the database for writing
//        SQLiteDatabase db = getWritableDatabase();
//
//        // It's a good idea to wrap our insert in a transaction.
//        // This helps with performance and ensures
//        // consistency of the database.
//        db.beginTransaction();
//        try {
//            ContentValues values = new ContentValues();
//            values.put(KEY_TRAINS_PLATFORM, train.getPlatform());
//            values.put(KEY_TRAINS_ARRIVAL_TIME, train.getArrivalTime());
//            values.put(KEY_TRAINS_DESTINATION, train.getDestination());
//            values.put(KEY_TRAINS_DESTINATION_TIME, train.getDestinationTimeâ€‹());
//            values.put(KEY_TRAINS_STATUS, train.getStatus());
//
//            // Notice how we haven't specified the primary key.
//            // SQLite auto increments the primary key column.
//            db.insertOrThrow(TABLE_TRAINS, null, values);
//            db.setTransactionSuccessful();
//        } catch (Exception e) {
//            Log.d(TAG, "Error while trying to add trains to database");
//        } finally {
//            db.endTransaction();
//        }
//    }

    /**
     * Check if train exists in the database
     * @param train train object to check
     * @return true if train exists, false if it doesn't
     */
//    public boolean trainExists(Train train){
//        Log.e(TAG,"Train exists");
//        SQLiteDatabase db = this.getReadableDatabase();
//        String Query = "Select * from " + TABLE_TRAINS +
//                " where " + KEY_TRAINS_PLATFORM + " = \"" + train.getPlatform() +
//                "\" and " + KEY_TRAINS_DESTINATION + " = \"" + train.getDestination() +
//                "\" and " + KEY_TRAINS_DESTINATION_TIME + " = \"" +
//                train.getDestinationTimeâ€‹() + "\"";
//
//        Cursor cursor = db.rawQuery(Query, null);
//        if(cursor.getCount() <= 0){
//            cursor.close();
//            return false;
//        }
//        cursor.close();
//        return true;
//
//    }

    /**
     * Update a train
     * @param train train object to update
     * @return train ID
     */
//    public int updateTrain(Train train) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_TRAINS_PLATFORM, train.getPlatform());
//        values.put(KEY_TRAINS_ARRIVAL_TIME, train.getArrivalTime());
//        values.put(KEY_TRAINS_DESTINATION, train.getDestination());
//        values.put(KEY_TRAINS_DESTINATION_TIME, train.getDestinationTimeâ€‹());
//        values.put(KEY_TRAINS_STATUS, train.getStatus());
//
//        return db.update(TABLE_TRAINS, values, KEY_TRAINS_PLATFORM + " = ? AND "+
//                        KEY_TRAINS_DESTINATION + " = ? AND "+
//                        KEY_TRAINS_DESTINATION_TIME + " = ?",
//                new String[] { String.valueOf(train.getPlatform()),
//                        String.valueOf(train.getDestination()),
//                        String.valueOf(train.getDestinationTimeâ€‹())});
//    }

    /**
     * Get all trains from the Database
     * @return an array of trains (that has all trains)
     */
//    public ArrayList<Train> getAllTrains() {
//        ArrayList<Train> trains = new ArrayList<>();
//
//        String TRAINS_SELECT_QUERY =
//                String.format("SELECT * FROM %s",
//                        TABLE_TRAINS);
//
//        // "getReadableDatabase()" and "getWriteableDatabase()"
//        // return the same object (except under low disk space scenarios)
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor cursor = db.rawQuery(TRAINS_SELECT_QUERY, null);
//        try {
//            if (cursor.moveToFirst()) {
//                do {
//
//                    Train newTrain = new Train(
//                            cursor.getString(cursor.getColumnIndex(KEY_TRAINS_PLATFORM)),
//                            cursor.getInt(cursor.getColumnIndex(KEY_TRAINS_ARRIVAL_TIME)),
//                            cursor.getString(cursor.getColumnIndex(KEY_TRAINS_STATUS)),
//                            cursor.getString(cursor.getColumnIndex(KEY_TRAINS_DESTINATION)),
//                            cursor.getString(cursor.getColumnIndex(KEY_TRAINS_DESTINATION_TIME)));
//                    trains.add(newTrain);
//                } while(cursor.moveToNext());
//            }
//        } catch (Exception e) {
//            Log.d(TAG, "Error while trying to get trains from database");
//        } finally {
//            if (cursor != null && !cursor.isClosed()) {
//                cursor.close();
//            }
//        }
//        return trains;
//    }

    /**
     * Delete all train records in the database
     */
    public void deleteAllTrains() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_TRAINS, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all trains");
        } finally {
            db.endTransaction();
        }
    }

}
