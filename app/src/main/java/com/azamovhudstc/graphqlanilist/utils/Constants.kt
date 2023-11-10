package com.azamovhudstc.graphqlanilist.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

class Constants {
    companion object
    {
        const val ANIME_TITLE: String = "ANIME_TITLE"
        const val EPISODE_DETAILS: String = "episodeInfo"
        const val MAL_ID = "anilist_id"

        // Base URLS
        const val GOGO_BASE_URL = "https://gogoanime.gr"
        const val ENIME_BASE_URL = "https://api.enime.moe"
        const val BILI_URL = "https://api-vn.kaguya.app/server"


        // Network Requests Header
        const val USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36"

        const val ORIGIN = "https://gogoanime.gg/"
        const val REFERER = "https://goload.pro/"
        const val ANILIST_API_URL = "https://graphql.anilist.co"

        fun showSnack(view: View?, message: String?) {
            view?.let {
                val snack =
                    Snackbar.make(view, message ?: "Error Occurred", Snackbar.LENGTH_LONG)
                if (!snack.isShown) {
                    snack.show()
                }
            }
        }

        fun getNetworkHeader(): Map<String, String> {
            return mapOf("referer" to REFERER, "origin" to ORIGIN, "user-agent" to USER_AGENT)
        }
    }

}