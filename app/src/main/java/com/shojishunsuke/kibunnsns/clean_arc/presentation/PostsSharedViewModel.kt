package com.shojishunsuke.kibunnsns.clean_arc.presentation

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shojishunsuke.kibunnsns.clean_arc.data.NaturalLanguageAnalysisRepository
import com.shojishunsuke.kibunnsns.clean_arc.domain.PostsSharedUseCase
import com.shojishunsuke.kibunnsns.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class PostsSharedViewModel(context: Context) : ViewModel() {

    private val _currentPosted = MutableLiveData<Post>()
    private val _postsList = MutableLiveData<List<Post>>()
    private val useCase: PostsSharedUseCase = PostsSharedUseCase(NaturalLanguageAnalysisRepository(context))

    val currentPosted: LiveData<Post> get() = _currentPosted
    val postsList: LiveData<List<Post>> get() = _postsList

    init {
        GlobalScope.launch {
            val wholePosts = useCase.loadWholePosts()

            launch(Dispatchers.IO ) {
                _postsList.postValue(wholePosts)
            }
        }
    }

    fun onPost(content: String) {
        GlobalScope.launch {

            val post = useCase.generatePost(content)
            val relatedPosts = useCase.loadRelatedPosts(post)

            launch(Dispatchers.IO) {

                _currentPosted.postValue(post)
                _postsList.postValue(relatedPosts)
            }

        }
    }


    fun onPostSelected(post: Post) {
        GlobalScope.launch {

            val relatedPosts = useCase.loadRelatedPosts(post)

            launch(Dispatchers.IO) {

                _currentPosted.postValue(post)
                _postsList.postValue(relatedPosts)
            }
        }
    }

    fun formatDate(date: Date): String = useCase.formatDate(date)
}