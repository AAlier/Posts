package mobile.samplelistload.ui

import android.content.Intent
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_error_loading.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mobile.samplelistload.GeneratorService
import mobile.samplelistload.model.PostModel
import mobile.samplelistload.R
import mobile.samplelistload.SampleApplication

class MainActivity : BaseActivity(), MainContract.View {
    lateinit var mLayoutManager: LinearLayoutManager
    lateinit var mPresenter: MainContract.Presenter
    lateinit var mAdapter: Adapter
    lateinit var mService: Intent
    var currentPage: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initSetup()

        btnReload.setOnClickListener {
            loadingNextPage()
            errorLayout?.visibility = View.GONE
        }

        swipeRefreshLayout.setOnRefreshListener {
            when(it){
                SwipyRefreshLayoutDirection.TOP -> {
                    currentPage = 0
                }
            }
            loadingNextPage()
        }
        simulateErrorWithDelay()
    }

    private fun initSetup() {
        mLayoutManager = LinearLayoutManager(this);
        recyclerView.layoutManager = mLayoutManager;

        mAdapter = Adapter()
        recyclerView.adapter = mAdapter

        val dividerItemDecoration = DividerItemDecoration(this, mLayoutManager.orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)

        mService = Intent(this, GeneratorService::class.java)
        mPresenter = MainPresenter(SampleApplication.database!!, this)
    }

    private fun simulateErrorWithDelay() {
        GlobalScope.launch {
            delay(3000)
            runOnUiThread {
                showErrorLoading()
            }
        }
    }

    @MainThread
    override fun showLoadingIndicator() {
        swipeRefreshLayout?.setRefreshing(true)
    }

    @MainThread
    override fun hideLoadingIndicator() {
        swipeRefreshLayout?.setRefreshing(false)
    }

    override fun onResume() {
        super.onResume()
        startService(mService)
    }

    override fun onPause() {
        super.onPause()
        stopService(mService)
    }

    private fun loadingNextPage() {
        mPresenter.loadItemsForPage(currentPage)
    }

    @MainThread
    override fun onGetNextPage(list: ArrayList<PostModel>) {
        recyclerView.post {
            if (currentPage == 0) {
                mAdapter.updateList(list)
                shortToast("${list.size} new item${if (list.size > 1) "s" else ""}")
            } else {
                mAdapter.addNewPosts(list)
                shortToast("${list.size} more items")
            }
            if (!list.isEmpty()) {
                currentPage++
            }
        }
    }

    @MainThread
    private fun showErrorLoading() {
        errorLayout?.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    @MainThread
    override fun onError(message: String) {
        shortToast(message)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.clearDb()
    }
}