package mobile.samplelistload.util

import mobile.samplelistload.model.PostModel
import java.util.*

object MockData {
    fun generateData(): PostModel {
        val title = "Post"
        val author = "Author"
        val calendar = Calendar.getInstance()
        return PostModel(title, author, calendar.time)
    }
}