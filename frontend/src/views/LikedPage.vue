<script setup>
import { ref, onMounted } from 'vue'
import musicService from '../services/musicService'

const likedMusics = ref([])
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  try {
    const musics = await musicService.getLiked()

    if (Array.isArray(musics)) {
      likedMusics.value = musics
    } else if (musics && typeof musics === 'object') {
      likedMusics.value = [musics]
    } else {
      likedMusics.value = []
    }
  } catch (err) {
    if (err.status === 404) {
      likedMusics.value = []
      error.value = ''
    } else if (err.status === 401) {
      error.value = 'Vous n\'êtes pas connecté. Veuillez vous reconnecter.'
    } else {
      error.value = 'Erreur lors du chargement des favoris: ' + (err.message || JSON.stringify(err))
    }
  } finally {
    loading.value = false
  }
})

const formatDuration = (seconds) => {
  if (!seconds) return '0:00'
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins}:${secs.toString().padStart(2, '0')}`
}
</script>

<template>
  <div class="container py-4">
    <h1 class="mb-4"><i class="bi bi-heart-fill text-danger"></i> Mes Favoris</h1>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Chargement...</span>
      </div>
      <p class="mt-3 text-muted">Chargement de vos favoris...</p>
    </div>

    <div v-else-if="error" class="alert alert-danger">
      <i class="bi bi-exclamation-triangle"></i> {{ error }}
    </div>

    <div v-else-if="!likedMusics || likedMusics.length === 0" class="alert alert-info">
      <i class="bi bi-info-circle"></i>
      Vous n'avez pas encore de musiques favorites.
      <router-link to="/musics" class="alert-link">Commencez à aimer des musiques !</router-link>
    </div>

    <div v-else>
      <div class="alert alert-info mb-4">
        <i class="bi bi-info-circle"></i>
        Vous avez {{ likedMusics.length }} musique(s) favorite(s)
      </div>

      <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
        <div v-for="music in likedMusics" :key="'music-' + music.musicId" class="col">
          <div class="card h-100 border-danger">
            <div class="card-header bg-light">
              <i class="bi bi-heart-fill text-danger"></i>
              <strong>{{music.title}}</strong>
            </div>
            <div class="card-body">
              <p class="card-text text-muted mb-2">
                <i class="bi bi-person"></i>
                <strong>
                  <template v-if="music.creatorNames">
                    <template v-for="(creator, idx) in music.creatorNames.split(',')" :key="idx">
                      <router-link :to="`/creators/${creator.trim()}`" class="text-decoration-none">
                        {{ creator.trim() }}
                      </router-link>
                      <span v-if="idx < music.creatorNames.split(',').length - 1">, </span>
                    </template>
                  </template>
                </strong>
              </p>
              <p class="card-text">
                <small class="text-muted">
                  <i class="bi bi-music-note-beamed"></i>
                  {{ music.genre }}<br>
                  <i class="bi bi-clock"></i>
                  {{ formatDuration(music.duration) }}<br>
                  <i class="bi bi-calendar"></i>
                  {{music.releaseDate}}
                </small>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
