package mobile.samplelistload

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.*
import mobile.samplelistload.db.IDatabaseHandler
import mobile.samplelistload.model.PostModel
import mobile.samplelistload.util.MockData
import java.util.*

class GeneratorService : Service() {
    var db: IDatabaseHandler? = SampleApplication.database
    var TAG = "GeneratorService"
    var job: Job? = null
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        generateData()
    }

    private fun generateData() {
        job = GlobalScope.launch(Dispatchers.IO) {
            delay(1000)
            db?.addPost(MockData.generateData())
            generateData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
        job = null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }
}