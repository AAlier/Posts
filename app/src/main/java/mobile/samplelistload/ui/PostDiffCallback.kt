package mobile.samplelistload.ui

import android.support.v7.util.DiffUtil
import mobile.samplelistload.model.PostModel

class PostDiffCallback(
    private val mOldPosts: List<PostModel>,
    private val mNewPosts: List<PostModel>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return mOldPosts.size
    }

    override fun getNewListSize(): Int {
        return mNewPosts.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldPosts[oldItemPosition].id == mNewPosts[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldPost = mOldPosts[oldItemPosition]
        val newPost = mNewPosts[newItemPosition]
        return oldPost.title.equals(newPost.title) &&
                oldPost.author.equals(newPost.author) &&
                oldPost.id == newPost.id
    }
}