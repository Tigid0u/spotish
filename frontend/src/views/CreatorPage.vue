<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import creatorService from '../services/creatorService'

const route = useRoute()
const creator = ref(null)
const loading = ref(true)
const error = ref('')

const loadCreator = async (name) => {
  loading.value = true
  error.value = ''
  creator.value = null

  try {
    creator.value = await creatorService.getCreator(name)
  } catch (err) {
    if (err.status === 404) {
      error.value = 'Créateur non trouvé'
    } else {
      error.value = 'Erreur lors du chargement: ' + (err.message || err)
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadCreator(route.params.name)
})

// Important: le composant est réutilisé quand on navigue vers la même route
// avec un paramètre différent. Sans watcher, la page ne se rafraîchit pas.
watch(
  () => route.params.name,
  (newName, oldName) => {
    if (!newName || newName === oldName) return
    loadCreator(newName)
  }
)

const isArtist = () => {
  // Backend: un artiste a `artists = null`, un groupe a `artists = []` (ou une liste).
  return !creator.value?.artists
}

const displayName = () => {
  return creator.value?.creatorName || ''
}

const normalizeGroupArtists = () => {
  const artists = creator.value?.artists
  if (!Array.isArray(artists)) return []
  // Backend actuel: `artists` est une liste de strings (noms d'artistes).
  // On normalise en objets pour simplifier le template.
  return artists
    .filter((a) => a)
    .map((artistName) => ({ artistName }))
}

const formatDuration = (seconds) => {
  if (!seconds) return '0:00'
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins}:${secs.toString().padStart(2, '0')}`
}
</script>

<template>
  <div class="container py-4">
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Chargement...</span>
      </div>
      <p class="mt-3 text-muted">Chargement...</p>
    </div>

    <div v-else-if="error" class="alert alert-danger">
      <i class="bi bi-exclamation-triangle"></i> {{ error }}
    </div>

    <div v-else-if="creator">
      <!-- En-tête -->
      <div class="mb-4">
        <h1>
          <i :class="isArtist() ? 'bi bi-person-fill' : 'bi bi-people-fill'"></i>
          {{ displayName() }}
        </h1>
        <p class="text-muted">
          <span v-if="isArtist()">
            <i class="bi bi-tag"></i> Artiste
          </span>
          <span v-else>
            <i class="bi bi-tag"></i> Groupe
          </span>
        </p>
      </div>

      <!-- Détails artiste -->
      <div v-if="isArtist()" class="card mb-4">
        <div class="card-body">
          <h5 class="card-title">Informations</h5>
          <p class="card-text">
            <strong>Nom de scène:</strong> {{ creator.creatorName }}
          </p>
          <p class="text-muted mb-0">
            Les informations détaillées (prénom/nom/date de naissance/email) ne sont pas disponibles dans l'API actuelle.
          </p>
        </div>
      </div>

      <!-- Détails groupe -->
      <div v-if="!isArtist()" class="mb-4">
        <div class="card mb-3">
          <div class="card-body">
            <h5 class="card-title">Informations</h5>
            <p class="card-text">
              <strong>Nom du groupe:</strong> {{ creator.creatorName }}
            </p>
          </div>
        </div>

        <!-- Artistes du groupe -->
        <div v-if="normalizeGroupArtists().length > 0" class="card mb-3">
          <div class="card-body">
            <h5 class="card-title">Artistes du groupe</h5>
            <ul class="list-group list-group-flush">
              <li v-for="artist in normalizeGroupArtists()" :key="artist.artistName" class="list-group-item">
                <router-link :to="`/creators/${artist.artistName}`" class="text-decoration-none">
                  <i class="bi bi-person"></i> {{ artist.artistName }}
                </router-link>
              </li>
            </ul>
          </div>
        </div>
      </div>

      <!-- Albums -->
      <section v-if="creator.albums && creator.albums.length > 0" class="mb-4">
        <h2 class="h4 mb-3"><i class="bi bi-disc"></i> Albums</h2>
        <div class="row row-cols-1 row-cols-md-2 g-3">
          <div v-for="album in creator.albums" :key="album.id" class="col">
            <div class="card">
              <div class="card-header bg-primary text-white">
                <strong>{{ album.title }}</strong>
              </div>
              <div class="card-body">
                <p class="card-text">
                  <small class="text-muted">
                    <i class="bi bi-calendar"></i> {{ album.releaseDate }}<br>
                    <i class="bi bi-music-note-list"></i> {{ album.musics ? album.musics.length : 0 }} musique(s)
                  </small>
                </p>
                <ul v-if="album.musics && album.musics.length > 0" class="list-group list-group-flush mt-2">
                  <li v-for="music in album.musics" :key="music.musicId" class="list-group-item">
                    <div class="d-flex justify-content-between align-items-center">
                      <span>{{ music.title }}</span>
                      <span class="badge bg-secondary">{{ formatDuration(music.duration) }}</span>
                    </div>
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Musiques -->
      <section v-if="creator.musics && creator.musics.length > 0">
        <h2 class="h4 mb-3"><i class="bi bi-music-note"></i> Musiques</h2>
        <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-3">
          <div v-for="music in creator.musics" :key="music.musicId" class="col">
            <div class="card h-100">
              <div class="card-body">
                <h5 class="card-title">{{ music.title }}</h5>
                <p class="card-text">
                  <small class="text-muted">
                    <i class="bi bi-music-note-beamed"></i> {{ music.genre }}<br>
                    <i class="bi bi-clock"></i> {{ formatDuration(music.duration) }}<br>
                    <i class="bi bi-calendar"></i> {{ music.releaseDate }}
                  </small>
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Aucun contenu -->
      <div v-if="(!creator.albums || creator.albums.length === 0) && (!creator.musics || creator.musics.length === 0)" class="alert alert-info">
        <i class="bi bi-info-circle"></i> Aucun album ou musique disponible pour ce créateur.
      </div>
    </div>
  </div>
</template>

