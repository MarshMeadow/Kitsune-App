package com.azamovhudstc.graphqlanilist.data.network.service

import com.azamovhudstc.graphqlanilist.data.local.BaseService
import com.azamovhudstc.graphqlanilist.data.model.ui_models.DetailedAnimeInfo
import com.azamovhudstc.graphqlanilist.data.model.ui_models.EpisodeWithTitle
import com.azamovhudstc.graphqlanilist.data.model.ui_models.GogoAnimeKeys
import okhttp3.ResponseBody
import retrofit2.http.*

interface GogoAnimeService : BaseService {

    @GET
    suspend fun fetchEpisodeMediaUrl(
        @HeaderMap header: Map<String, String>,
        @Url url: String
    ): ResponseBody

    @GET
    suspend fun fetchAnimeInfo(
        @HeaderMap header: Map<String, String>,
        @Url episodeUrl: String
    ): ResponseBody

    @GET
    @Headers("watchsb:streamsb")
    suspend fun fetchM3u8Url(
        @HeaderMap header: Map<String, String>,
        @Url url: String
    ): ResponseBody

    @GET
    @Headers("X-Requested-With:XMLHttpRequest")
    suspend fun fetchM3u8PreProcessor(
        @HeaderMap header: Map<String, String>,
        @Url url: String
    ): ResponseBody

    @GET(EPISODE_LOAD_URL)
    suspend fun fetchEpisodeList(
        @HeaderMap header: Map<String, String>,
        @Query("ep_start") startEpisode: Int = 0,
        @Query("ep_end") endEpisode: String,
        @Query("id") id: String,
        @Query("default_ep") defaultEp: Int = 0,
        @Query("alias") alias: String
    ): ResponseBody

    @GET("$MAL_SYNC_URL/{id}.json")
    suspend fun getGogoUrlFromAniListId(
        @Path("id") id: Int
    ): DetailedAnimeInfo

    @GET("$EPISODE_TITLES/{id}.json")
    suspend fun getEpisodeTitles(@Path("id") id: Int = 1): EpisodeWithTitle

    @GET(GOGO_KEYS_URL)
    suspend fun getKeys(): GogoAnimeKeys

    companion object {
        const val MAL_SYNC_URL =
            "https://raw.githubusercontent.com/MALSync/MAL-Sync-Backup/master/data/anilist/anime"

        const val EPISODE_TITLES =
            "https://raw.githubusercontent.com/saikou-app/mal-id-filler-list/main/fillers"

        const val EPISODE_LOAD_URL = "https://ajax.gogocdn.net/ajax/load-list-episode"

        /* Used to get the gogoanime keys from the GitHub repo. Thanks to https://github.com/justfoolingaround */
        const val GOGO_KEYS_URL =
            "https://raw.githubusercontent.com/justfoolingaround/animdl-provider-benchmarks/master/api/gogoanime.json"
    }
}
