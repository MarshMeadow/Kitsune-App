/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.ui.screens.detail.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AniListMedia
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AnimePlayingDetails
import com.azamovhudstc.graphqlanilist.data.model.ui_models.Media
import com.azamovhudstc.graphqlanilist.data.network.rest.jikan.Data
import com.azamovhudstc.graphqlanilist.data.network.rest.jikan.Images
import com.azamovhudstc.graphqlanilist.data.network.rest.jikan.Jpg
import com.azamovhudstc.graphqlanilist.databinding.FragmentAnimeWatchPageBinding
import com.azamovhudstc.graphqlanilist.source.AnimeSource
import com.azamovhudstc.graphqlanilist.source.SourceSelector
import com.azamovhudstc.graphqlanilist.ui.activity.PlayerActivity
import com.azamovhudstc.graphqlanilist.ui.adapter.AnimeWatchAdapter
import com.azamovhudstc.graphqlanilist.ui.adapter.EpisodesAdapter
import com.azamovhudstc.graphqlanilist.utils.*
import com.azamovhudstc.graphqlanilist.viewmodel.AnimeWatchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt

@AndroidEntryPoint
class AnimeWatchPage() :
    Fragment() {
    private var end: Int? = null
    private lateinit var epList: MutableList<String>
    private lateinit var epType: String
    private var _binding: FragmentAnimeWatchPageBinding? = null
    private val binding get() = _binding!!
    private var isOnlyOne = false
    private var style = 0
    var screenWidth = 0
    private val model by viewModels<AnimeWatchViewModel>()
    private lateinit var headerAdapter: AnimeWatchAdapter
    private lateinit var episodeAdapter: EpisodesAdapter
    private lateinit var list: ArrayList<Data>
    private lateinit var epIndex: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAnimeWatchPageBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var media  =requireArguments().getSerializable("data") as AniListMedia
        headerAdapter = AnimeWatchAdapter(this)
        episodeAdapter = EpisodesAdapter(emptyList(), 0)
        val animeSource: AnimeSource =
            SourceSelector(requireContext()).getSelectedSource("allanime")

        lifecycleScope.launch {
            episodeAdapter.setItemClickListener {
                lifecycleScope.launch {

                }
            }
        }
        headerAdapter.setItemClickListener {
            if (it == 0) {
                episodeAdapter.list = emptyList()
                model.loadEpisodesById(media.idMal ?: 0)
            } else {

                lifecycleScope.launch {

                    binding.animeSourceRecycler.hide()
                    binding.animeNotSupported.hide()
                    episodeAdapter.list = emptyList()
                    binding.mediaInfoProgressBar.show()
                    val list = animeSource.searchAnime("${media.title!!.userPreferred}")
                    val episodeListForAdapter = ArrayList<Data>()
                    if (list.isNotEmpty()) {
                        val animeEpisodesMap = animeSource.animeDetails(list.get(0).second)
                        epType = animeEpisodesMap.keys.first()
                        epList = animeEpisodesMap[epType]!!.keys.toMutableList()
                        epIndex = epList.first()
                        epList.onEach {
                            episodeListForAdapter.add(
                                Data(
                                    " Episode ${it}",
                                    Images(Jpg(media.coverImage!!.large!!)),
                                    it.toInt(),
                                    "${list.get(0).first} $it",
                                    "?NIULLLLAAABLEEE"
                                )
                            )
                        }
                        binding.animeSourceRecycler.show()
                        binding.animeNotSupported.hide()
                        binding.mediaInfoProgressBar.hide()
                        episodeAdapter.list = episodeListForAdapter
                        episodeAdapter.notifyDataSetChanged()

                        episodeAdapter.setItemClickListener {
                            epIndex = epList[it.mal_id - 1]
                            val data = AnimePlayingDetails(
                                animeName = media.title!!.userPreferred!!,
                                animeUrl = list.get(0).second!!,
                                animeEpisodeIndex = epIndex,
                                animeEpisodeMap = animeEpisodesMap[epType] as HashMap<String, String>,
                                animeTotalEpisode = animeEpisodesMap[epType]!!.size.toString(),
                                epType = epType
                            )
                            PlayerActivity.pipStatus = true

                            val intent = PlayerActivity.newIntent(
                                requireContext(),
                                data
                            )
                            startActivity(intent)

                        }

                    } else {
                        binding.animeSourceRecycler.show()
                        binding.animeNotSupported.show()
                        binding.mediaInfoProgressBar.hide()
                        episodeAdapter.list = emptyList()
                        episodeAdapter.notifyDataSetChanged()
                    }

                }

            }
        }

        screenWidth = resources.displayMetrics.widthPixels.dp.toInt()

        var maxGridSize = (screenWidth / 100f).roundToInt()
        maxGridSize = max(4, maxGridSize - (maxGridSize % 2))
        val gridLayoutManager = GridLayoutManager(requireContext(), maxGridSize)

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val style = episodeAdapter.getItemViewType(position)

                return when (position) {
                    0 -> maxGridSize
                    else -> when (style) {
                        0 -> maxGridSize
                        1 -> 2
                        2 -> 1
                        else -> maxGridSize
                    }
                }
            }
        }

        binding.animeSourceRecycler.layoutManager = gridLayoutManager

        binding.animeSourceRecycler.adapter = ConcatAdapter(headerAdapter, episodeAdapter)

        model.episodeListLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Error -> {
                    episodeAdapter.notifyDataSetChanged()
                    binding.animeSourceRecycler.show()
                    binding.animeNotSupported.show()
                    binding.mediaInfoProgressBar.hide()

                }
                Result.Loading -> {
                    binding.animeSourceRecycler.hide()
                    binding.mediaInfoProgressBar.show()
                }
                is Result.Success -> {
                    binding.animeSourceRecycler.show()
                    binding.mediaInfoProgressBar.hide()
                    binding.apply {
                        list = it.data.data as ArrayList<Data>
                        episodeAdapter.list = it.data.data
                        episodeAdapter.list = list.reversed()
                        episodeAdapter.notifyDataSetChanged()


                    }
                    logMessage(it.data.data.toString())
                }
            }
        }
    }

    fun onIconPressed(style: Int, reversed: Boolean) {
        episodeAdapter.list = episodeAdapter.list.reversed()
        episodeAdapter.notifyDataSetChanged()
    }

    fun onIconPressedForStyle(style: Int) {
        episodeAdapter.updateType(style)
        episodeAdapter.notifyDataSetChanged()

    }
}