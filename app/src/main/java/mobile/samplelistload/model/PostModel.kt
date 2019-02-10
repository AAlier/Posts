package mobile.samplelistload.model

import java.util.*

data class PostModel(var title: String,
                     var author: String,
                     var publishedAt: Date){
    var id: Int? = 0
}
