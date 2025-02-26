package com.practicum.playlistmaker

data class Track(
    val trackId: Int,               // Уникальный идентификатор трека
    val trackName: String,          // Название композиции
    val artistName: String,         // Имя исполнителя
    var trackTimeMillis: String,    // Продолжительность трека
    val artworkUrl100: String,      // Ссылка на изображение обложки
    val collectionName: String?,    // Название альбома (может быть null)
    val releaseDate: String,        // Год релиза трека
    val primaryGenreName: String,   // Жанр трека
    val country: String,            // Страна исполнителя
    val previewUrl: String          // 🔥 Ссылка на аудио-превью трека
)
