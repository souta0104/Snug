package com.shojishunsuke.kibunnsns.clean_arc.domain

import com.shojishunsuke.kibunnsns.clean_arc.data.FireStoreDatabaseRepository
import com.shojishunsuke.kibunnsns.model.Post

class DetailPostsFragmentUsecase(basePost: Post) {
    private val fireStoreRepository = FireStoreDatabaseRepository()
    private var hasMoreSameActPost = true
    private var sameActPrevPost: Post
    private var wideRangePrevPost: Post
    init {
        sameActPrevPost = basePost
        wideRangePrevPost = Post(sentiScore = basePost.sentiScore)
    }


    suspend fun loadPosts(): List<Post> {
        val wideRangeCollection = loadWideRangeCollection()
        val result = mutableListOf<Post>().apply {
            addAll(wideRangeCollection)
        }
        if (hasMoreSameActPost) {
            val sameActCollection = loadSameActCollection()
            result.addAll(sameActCollection)
        }

        return result.shuffled()
    }

    private suspend fun loadWideRangeCollection(): List<Post> {
        val posts = fireStoreRepository.loadWideRangeNextCollection(wideRangePrevPost)
        if (posts.isNotEmpty()) wideRangePrevPost = posts.last()
        return posts
    }

    private suspend fun loadSameActCollection(): List<Post> {
        val posts = fireStoreRepository.loadSortedNextCollection(sameActPrevPost)
        if (posts.isNotEmpty()) sameActPrevPost = posts.last()
        if (posts.size < 12) hasMoreSameActPost = false

        return posts
    }
}