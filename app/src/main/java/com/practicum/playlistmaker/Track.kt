package com.practicum.playlistmaker

import java.io.Serializable

data class Track(
    var trackId: Int, var trackName: String, // Название композиции
    var artistName: String, // Имя исполнителя
    var trackTimeMillis: Int, // Продолжительность трека
    var artworkUrl100: String,
    val collectionName: String,// Ссылка на изображение обложки
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String
    ) : Serializable