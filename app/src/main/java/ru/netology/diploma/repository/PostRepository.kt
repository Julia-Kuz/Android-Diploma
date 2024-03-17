package ru.netology.diploma.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.diploma.dto.Event
import ru.netology.diploma.dto.Job
import ru.netology.diploma.dto.Post
import ru.netology.diploma.dto.UserResponse
import ru.netology.diploma.model.AttachmentModel

interface PostRepository {

    val data: Flow<List<Post>>
    val eventData: Flow<List<Event>>
    val userList: Flow<List<UserResponse>>
    val wall: Flow<List<Post>>
    val jobs: Flow <List<Job>>

    suspend fun getAll()
    suspend fun save(post: Post)
    suspend fun likeById(id: Int, flag: Boolean)
    suspend fun removeById(id: Int)
    suspend fun saveWithAttachment(post: Post, attachmentModel: AttachmentModel)
    suspend fun updatePlayer()
    suspend fun updateIsPlaying (postId: Int, isPlaying: Boolean)

    suspend fun getUserById (id: Int): UserResponse
    suspend fun getAllUsers ()
    suspend fun updateUsers(user: UserResponse, isSelected: Boolean)
    suspend fun deselectUsers (isSelected: Boolean)

    suspend fun getAllEvents()
    suspend fun saveEvent(event: Event)
    suspend fun likeEventById(id: Int, flag: Boolean)
    suspend fun removeEventById(id: Int)
    suspend fun saveEventWithAttachment(event: Event, attachmentModel: AttachmentModel)
    suspend fun updateIsPlayingEvent (postId: Int, isPlaying: Boolean)

    suspend fun getWall(authorId: Int)
    suspend fun updateIsPlayingWall (postId: Int, isPlaying: Boolean)

    suspend fun getJobs (userId: Int)
    suspend fun getMyJobs ()
    suspend fun createJob (job: Job)
    suspend fun removeJobById (id: Int)

}