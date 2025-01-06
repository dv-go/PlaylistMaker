package com.practicum.playlistmaker

data class Track(
    val trackId: Int,         // Уникальный идентификатор трека
    val trackName: String,    // Название композиции
    val artistName: String,   // Имя исполнителя
    var trackTimeMillis: String, // Продолжительность трека
    val artworkUrl100: String // Ссылка на изображение обложки
)