package mobile.samplelistload.ui

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_list.view.*
import mobile.samplelistload.model.PostModel
import mobile.samplelistload.util.format
import mobile.samplelistload.R

class Adapter(private val list: ArrayList<PostModel> = ArrayList()) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = list.get(position)
        holder.itemView.apply {
            post_title.text = post.title
            post_author.text = post.author
            date_posted.text = post.publishedAt.format()
            indexView.text = (position + 1).toString()
        }
    }

    fun addNewPosts(newList: ArrayList<PostModel>) {
        val sizeBefore = this.list.size + 1
        this.list.addAll(newList)
        notifyItemRangeInserted(sizeBefore, newList.size)
    }

    /**
     * ID of the POST meant to be unique otherwise it will override existing
     * post from the database. Depending on the requirements we can remove duplication
     * before adding to database or just remove from the list and show unique posts
     * in the list
     */

    private fun addNewPostsWithoutDuplication(newList: ArrayList<PostModel>) {
        newList.addAll(this.list)
        val hashSet = HashSet<PostModel>()
        hashSet.addAll(newList)
        this.list.clear()
        this.list.addAll(hashSet)
        notifyItemChanged(0, this.list.size)
    }

    fun updateList(newList: ArrayList<PostModel>) {
        val diffCallback = PostDiffCallback(this.list, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.list.clear()
        this.list.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    fun clearList() {
        this.list.clear()
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}