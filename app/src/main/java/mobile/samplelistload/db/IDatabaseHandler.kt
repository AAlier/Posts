package mobile.samplelistload.db

import mobile.samplelistload.model.PostModel

interface IDatabaseHandler {
    fun addPost(post: PostModel)
    fun getPage(page: Int, itemCount: Int = 20): ArrayList<PostModel>
    fun clearDB() : Boolean
    fun closeDB()
}