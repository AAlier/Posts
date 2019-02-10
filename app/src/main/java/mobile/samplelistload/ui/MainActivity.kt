package mobile.samplelistload.ui

import android.content.Intent
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.MenuItem.SHOW_AS_ACTION_ALWAYS
import android.view.View
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_error_loading.*
import kotlinx.coroutines.*
import mobile.samplelistload.GeneratorService
import mobile.samplelistload.SampleApplication
import mobile.samplelistload.model.PostModel
import mobile.samplelistload.util.RecyclerItemClickListener
import mobile.samplelistload.R

class MainActivity : BaseActivity(), MainContract.View, RecyclerItemClickListener.OnItemClickListener {
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
            when (it) {
                SwipyRefreshLayoutDirection.TOP -> {
                    currentPage = 0
                }
            }
            loadingNextPage()
        }
        simulateErrorWithDelay()

        recyclerView.addOnItemTouchListener(
            RecyclerItemClickListener(this, recyclerView, this)
        )
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menu.add(0, 0, 0, "Clear")
            .setShortcut('0', 'c')
            .setShowAsAction(SHOW_AS_ACTION_ALWAYS)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            0 -> {
                GlobalScope.launch{
                    if (mPresenter.clearDb())
                        longToast("Successfully cleared database")
                    else
                        longToast("Error clearing database")
                    // Reload data if necessary
                    reloadData()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(view: View, position: Int) {
        recyclerView.smoothScrollToPosition(0);
    }

    override fun onLongItemClick(view: View, position: Int) {}

    private fun reloadData() {
        currentPage = 0
        loadingNextPage()
    }

    override fun showLoadingIndicator() {
        runOnUiThread { swipeRefreshLayout?.setRefreshing(true) }
    }

    override fun hideLoadingIndicator() {
        runOnUiThread { swipeRefreshLayout?.setRefreshing(false) }
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
        runOnUiThread {
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
    }

    override fun hideErrorView(){
        runOnUiThread {
            errorLayout?.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }

    private fun showErrorLoading() {
        runOnUiThread {
            errorLayout?.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }

    override fun onError(message: String) {
        shortToast(message)
    }
}

/*
*   Pagination
*   recyclerView.addOnScrollListener(recyclerViewOnScrollListener);
*
*   In case if we need to load without pulling bottom refresh layout
*
    val recyclerViewOnScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = mLayoutManager.getChildCount()
                val totalItemCount = mLayoutManager.getItemCount()
                val firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isLastPage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGE_SIZE
                    ) {
                        loadingNextPage()
                    }
                }
            }
        }
* */