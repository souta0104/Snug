package com.shojishunsuke.kibunnsns.domain.use_case

import com.shojishunsuke.kibunnsns.data.repository.impl.FireStoreDatabaseRepository
import com.shojishunsuke.kibunnsns.data.repository.impl.FirebaseUserRepository
import com.shojishunsuke.kibunnsns.data.repository.impl.RoomPostDateRepository
import com.shojishunsuke.kibunnsns.data.repository.DataBaseRepository
import com.shojishunsuke.kibunnsns.domain.model.Post
import java.util.*

class CalendarFragmentUseCase {
    private val fireStoreRepository: DataBaseRepository = FireStoreDatabaseRepository()
    private val userRepository: FirebaseUserRepository = FirebaseUserRepository()
    private val userId: String = userRepository.getUserId()
    private val postDateRepository: RoomPostDateRepository = RoomPostDateRepository()

    suspend fun loadPostsByDate(date: Calendar): MutableList<Post> {

        val dateStart = date.clone() as Calendar
        dateStart.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        val dateEnd = date.clone() as Calendar
        dateEnd.apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }
        return fireStoreRepository.loadDateRangedCollection(userId, dateStart.time, dateEnd.time)
    }

    val postedDate = postDateRepository.loadDateList()
}