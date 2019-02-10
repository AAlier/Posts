package mobile.samplelistload.ui

import kotlinx.coroutines.*
import mobile.samplelistload.db.IDatabaseHandler

class MainPresenter(private val db: IDatabaseHandler,
                    private val mView: MainContract.View?): MainContract.Presenter {

    override fun loadItemsForPage(page: Int) {
        mView?.showLoadingIndicator()
        GlobalScope.launch {
            delay(3000)
            mView?.onGetNextPage(db.getPage(page))
            mView?.hideLoadingIndicator()
        }
    }

    override fun clearDb(){
        GlobalScope.launch {
            db.closeDB()
        }
    }
}