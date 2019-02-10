package mobile.samplelistload.ui

import kotlinx.coroutines.*
import mobile.samplelistload.db.IDatabaseHandler

class MainPresenter(private val db: IDatabaseHandler,
                    private val mView: MainContract.View?): MainContract.Presenter {

    override fun loadItemsForPage(page: Int) {
        mView?.showLoadingIndicator()
        GlobalScope.launch(Dispatchers.IO) {
            delay(3000)
            mView?.onGetNextPage(db.getPage(page))
            mView?.hideErrorView()
            mView?.hideLoadingIndicator()
        }
    }

    override suspend fun clearDb() : Boolean {
        val task = GlobalScope.async(Dispatchers.IO) {
            db.clearDB()
        }
        return task.await()
    }
}