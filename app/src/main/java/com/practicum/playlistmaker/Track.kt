package com.practicum.playlistmaker

data class Track(
    var trackId: Int, var trackName: String, // Название композиции
    var artistName: String, // Имя исполнителя
    var trackTimeMillis: Int, // Продолжительность трека
    var artworkUrl100: String // Ссылка на изображение обложки
)