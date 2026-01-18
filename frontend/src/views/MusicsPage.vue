<script setup>
import { ref, onMounted } from 'vue'
import musicService from '../services/musicService'

const lastListened = ref([])
const mostListened = ref([])
const loading = ref(true)
const error = ref('')
const likeMessage = ref('')
const likeMessageType = ref('success') // 'success' ou 'error'
const likedMusicIds = ref(new Set())

onMounted(async () => {
  try {
    const [last, most] = await Promise.all([
      musicService.getLastListened(),
      musicService.getMostListened()
    ])
    lastListened.value = last || []
    mostListened.value = most || []
  } catch (err) {
    error.value = 'Erreur lors du chargement des musiques: ' + (err.message || err)
    console.error('❌ Error loading musics:', err)
  } finally {
    loading.value = false
  }
})

const likeMusic = async (musicId, musicTitle) => {
  try {
    await musicService.likeMusic(musicId)
    likedMusicIds.value.add(musicId)
    likeMessage.value = `"${musicTitle}" ajoutée aux favoris !`
    likeMessageType.value = 'success'
    setTimeout(() => likeMessage.value = '', 3000)
  } catch (err) {
    if (err.status === 401) {
      likeMessage.value = 'Vous devez être connecté'
    } else if (err.status === 404) {
      likeMessage.value = 'Musique non trouvée'
    } else {
      likeMessage.value = 'Erreur: ' + (err.message || 'Impossible d\'aimer cette musique')
    }
    likeMessageType.value = 'error'
    setTimeout(() => likeMessage.value = '', 4000)
  }
}

const formatDuration = (seconds) => {
  if (!seconds) return '0:00'
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

const isLiked = (musicId) => {
  return likedMusicIds.value.has(musicId)
}
</script>

<template>
  <div class="container py-4">
    <h1 class="mb-4"><i class="bi bi-music-note-list"></i> Mes Musiques</h1>

    <!-- Message de like -->
    <div v-if="likeMessage" :class="['alert', 'alert-dismissible', 'fade', 'show', likeMessageType === 'success' ? 'alert-success' : 'alert-danger']" role="alert">
      {{ likeMessage }}
      <button type="button" class="btn-close" @click="likeMessage = ''" aria-label="Close"></button>
    </div>

    <!-- Chargement -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Chargement...</span>
      </div>
      <p class="mt-3 text-muted">Chargement de vos musiques...</p>
    </div>

    <!-- Erreur -->
    <div v-else-if="error" class="alert alert-danger">
      <i class="bi bi-exclamation-triangle"></i> {{ error }}
    </div>

    <!-- Contenu -->
    <div v-else>
      <!-- Dernières musiques écoutées -->
      <section class="mb-5">
        <h2 class="h4 mb-3"><i class="bi bi-clock-history"></i> 10 Dernières Écoutées</h2>
        <div v-if="lastListened.length === 0" class="alert alert-info">
          <i class="bi bi-info-circle"></i> Aucune musique écoutée récemment
        </div>
        <div v-else class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-3">
          <div v-for="music in lastListened" :key="'last-' + music.musicId" class="col">
            <div class="card h-100">
              <div class="card-body">
                <h5 class="card-title">{{ music.title }}</h5>
                <p class="card-text text-muted mb-2">
                  <i class="bi bi-person"></i>
                  <template v-if="music.creatorNames">
                    <template v-for="(creator, idx) in music.creatorNames.split(',')" :key="idx">
                      <router-link :to="`/creators/${creator.trim()}`" class="text-decoration-none">
                        {{ creator.trim() }}
                      </router-link>
                      <span v-if="idx < music.creatorNames.split(',').length - 1">, </span>
                    </template>
                  </template>
                </p>
                <p class="card-text">
                  <small class="text-muted">
                    <i class="bi bi-music-note-beamed"></i> {{ music.genre }}<br>
                    <i class="bi bi-clock"></i> {{ formatDuration(music.duration) }}<br>
                    <i class="bi bi-calendar"></i> {{ music.releaseDate }}
                  </small>
                </p>
              </div>
              <div class="card-footer bg-transparent">
                <button
                  @click="likeMusic(music.musicId, music.title)"
                  :disabled="isLiked(music.musicId)"
                  class="btn btn-sm w-100"
                  :class="isLiked(music.musicId) ? 'btn-danger' : 'btn-outline-danger'"
                >
                  <i class="bi" :class="isLiked(music.musicId) ? 'bi-heart-fill' : 'bi-heart'"></i>
                  {{ isLiked(music.musicId) ? 'Aimée' : 'Aimer' }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Top 10 musiques -->
      <section>
        <h2 class="h4 mb-3"><i class="bi bi-trophy"></i> Top 10 Plus Écoutées</h2>
        <div v-if="mostListened.length === 0" class="alert alert-info">
          <i class="bi bi-info-circle"></i> Aucune statistique disponible
        </div>
        <div v-else class="row row-cols-1 row-cols-md-3 g-3">
          <div v-for="(music, index) in mostListened" :key="'top-' + music.musicId" class="col">
            <div class="card h-100 border-warning">
              <div class="card-header bg-warning text-dark fw-bold">
                <i class="bi bi-trophy-fill"></i> #{{ index + 1 }}
              </div>
              <div class="card-body">
                <h5 class="card-title">{{ music.title }}</h5>
                <p class="card-text text-muted mb-2">
                  <i class="bi bi-person"></i>
                  <template v-if="music.creatorNames">
                    <template v-for="(creator, idx) in music.creatorNames.split(',')" :key="idx">
                      <router-link :to="`/creators/${creator.trim()}`" class="text-decoration-none">
                        {{ creator.trim() }}
                      </router-link>
                      <span v-if="idx < music.creatorNames.split(',').length - 1">, </span>
                    </template>
                  </template>
                </p>
                <p class="card-text">
                  <small class="text-muted">
                    <i class="bi bi-music-note-beamed"></i> {{ music.genre }}<br>
                    <i class="bi bi-clock"></i> {{ formatDuration(music.duration) }}<br>
                    <i class="bi bi-calendar"></i> {{ music.releaseDate }}
                  </small>
                </p>
              </div>
              <div class="card-footer bg-transparent">
                <button
                  @click="likeMusic(music.musicId, music.title)"
                  :disabled="isLiked(music.musicId)"
                  class="btn btn-sm w-100"
                  :class="isLiked(music.musicId) ? 'btn-danger' : 'btn-outline-danger'"
                >
                  <i class="bi" :class="isLiked(music.musicId) ? 'bi-heart-fill' : 'bi-heart'"></i>
                  {{ isLiked(music.musicId) ? 'Aimée' : 'Aimer' }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>
