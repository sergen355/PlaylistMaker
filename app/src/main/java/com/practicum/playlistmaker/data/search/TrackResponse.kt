package com.practicum.playlistmaker.data.search

import com.practicum.playlistmaker.data.search.dto.Response
import com.practicum.playlistmaker.domain.model.Track

class TrackResponse(val results: List<Track>) : Response()