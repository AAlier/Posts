package mobile.samplelistload

import android.app.Application
import mobile.samplelistload.db.DatabaseHandler
import mobile.samplelistload.db.IDatabaseHandler
import mobile.samplelistload.model.PostModel
import java.util.*

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        database = DatabaseHandler(this)
    }

    companion object {
        var database: IDatabaseHandler? = null
    }
}