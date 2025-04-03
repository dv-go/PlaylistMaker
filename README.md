# Playlist Maker

**Playlist Maker** — Android-приложение для поиска, прослушивания и сохранения музыки, с темной/светлой темой, медиатекой и настройками. Построено по Clean Architecture с MVVM.

---

## 📅 Функционал

- 🔍 Поиск треков (iTunes API)
- ⌚ История поиска (SharedPreferences + JSON)
- ▶️ Воспроизведение превью трека (MediaPlayer)
- 📚 Медиатека (избранное, плейлисты)
- 🔧 Настройки (темная тема, шаринг, соглашение, связь с поддержкой)

---

## 📊 Архитектура

- **MVVM + Clean Architecture**
- Слои:
  - `ui` — экраны, фрагменты, адаптеры
  - `viewmodel` — обработка UI-событий
  - `domain` (interactor, api, models) — бизнес-логика
  - `data` (dto, datasource, repository) — работа с API/кэшем

---

## 🌐 Технологии

- Kotlin
- Android SDK, ViewBinding
- Retrofit
- MediaPlayer
- SharedPreferences
- Glide
- Koin (DI)
- LiveData + ViewModel
- Handler + Runnable (Debounce)
- ViewPager2 + TabLayout

---

## 📂 Структура

```bash
com.practicum.playlistmaker
├── main            # MainActivity
├── search          # Поиск треков + история
├── player          # Аудиоплеер
├── mediateka       # Избранное и плейлисты
├── settings        # Настройки, тема, support
├── di              # Koin модули
```

---

## ⚡ Сборка и запуск

1. 📂 Клонируйте проект:
```bash
git clone https://github.com/dv-go/PlaylistMaker.git
```

2. 🎓 Откройте в **Android Studio Arctic Fox** (или выше)

3. 🍺 Соберите и запустите на устройстве/эмуляторе

---

## 📍 Точки развития

- Добавление плейлистов и избранного
- Room/БД для хранения
- Тесты (моки, UI, unit)
- Jetpack Compose вместо XML

---

## ✉️ Обратная связь

Репозиторий: [github.com/dv-go/PlaylistMaker](https://github.com/dv-go/PlaylistMaker)

---

Спасибо за использование Playlist Maker!