package mobile.samplelistload.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mobile.samplelistload.BuildConfig
import mobile.samplelistload.model.PostModel
import mobile.samplelistload.util.MockData
import java.util.*

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, BuildConfig.DATABASE_NAME, null, BuildConfig.DATABASE_VERSION), IDatabaseHandler {
    private val TAG = "DatabaseHandler"
    private val TABLE_TITLE_POST = "post"
    private val KEY_ID = "id"
    private val KEY_TITLE = "post_title"
    private val KEY_AUTHOR = "post_author"
    private val KEY_DATE = "post_publish_date"
    private val dbWrite = this.writableDatabase
    private val dbRead = this.readableDatabase

    override fun onCreate(db: SQLiteDatabase?) {
        Log.i(TAG, "Creating database")
        val CREATE_POSTS_TABLE = createPostTable()
        db?.execSQL(CREATE_POSTS_TABLE)
        GlobalScope.launch(Dispatchers.IO) {
            delay(1000)
            generateMockData()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    override fun addPost(post: PostModel) {
        dbWrite.beginTransaction()
        val values = ContentValues()
        values.put(KEY_TITLE, post.title)
        values.put(KEY_AUTHOR, post.author)
        values.put(KEY_DATE, post.publishedAt.time)
        dbWrite.insert(TABLE_TITLE_POST, null, values)
        dbWrite.setTransactionSuccessful()
        dbWrite.endTransaction()
    }

    override fun getPage(page: Int, itemCount: Int): ArrayList<PostModel> {
        val postList = ArrayList<PostModel>()
        val selectQuery = getSelectQueryForPage(page, itemCount)
        val cursor = dbRead.rawQuery(selectQuery, null)
        dbRead.beginTransaction()
        if (cursor.moveToFirst()) {
            do {
                postList.add(getPostFromCursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        dbRead.setTransactionSuccessful()
        dbRead.endTransaction()
        return postList
    }

    private fun createPostTable() : String {
        return ("CREATE TABLE $TABLE_TITLE_POST( " +
                "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "$KEY_TITLE TEXT, " +
                "$KEY_AUTHOR TEXT, " +
                "$KEY_DATE TEXT)")
    }

    private fun getSelectQueryForPage(page: Int, itemCount: Int): String {
        return "SELECT * FROM $TABLE_TITLE_POST " +
                "ORDER BY $KEY_ID " +
                "LIMIT ${page * itemCount}, ${itemCount};"
    }

    private fun getPostFromCursor(cursor: Cursor): PostModel {
        val ID = cursor.getInt(cursor.getColumnIndex(KEY_ID))
        val title = "${cursor.getString(cursor.getColumnIndex(KEY_TITLE))} $ID"
        val author = "${cursor.getString(cursor.getColumnIndex(KEY_AUTHOR))} $ID"
        val date = Date(cursor.getLong(cursor.getColumnIndex(KEY_DATE)))
        val post = PostModel(title, author, date)
        post.id = ID
        return post
    }

    override fun closeDB() {
        dbWrite.beginTransaction()
        dbWrite.delete(TABLE_TITLE_POST, null, null)
        dbWrite.setTransactionSuccessful()
        dbWrite.endTransaction()
        this.close()
    }

    private fun generateMockData() {
        for (i in 1 until 21)
            addPost(MockData.generateData())
    }
}