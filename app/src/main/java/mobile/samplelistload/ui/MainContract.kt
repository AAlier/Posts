package mobile.samplelistload.ui

import mobile.samplelistload.model.PostModel
import mobile.samplelistload.util.ProgressBar

interface MainContract {
    interface View : ProgressBar  {
        fun onGetNextPage(list: ArrayList<PostModel>)
        fun onError(message: String)
        fun hideErrorView()
    }

    interface Presenter {
        fun loadItemsForPage(page: Int)
        suspend fun clearDb(): Boolean
    }
}