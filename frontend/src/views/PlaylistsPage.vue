<script setup>
import { ref, computed, onMounted } from 'vue'
import authService from '../services/authService'
import playlistService from '../services/playlistService'
import musicService from '../services/musicService'

const loading = ref(true)
const error = ref('')
const myPlaylists = ref([])
const followedPlaylists = ref([])
const allMusics = ref([])
const createForm = ref({ name: '', description: '', selectedMusicIds: new Set() })
const savingCreate = ref(false)
const editingPlaylistId = ref(null)
const editSelection = ref(new Set())
const savingEdit = ref(false)
const message = ref('')
const messageType = ref('success')

const currentUser = computed(() => authService.username.value)

const resetCreateForm = () => {
  createForm.value = { name: '', description: '', selectedMusicIds: new Set() }
}

const fetchAll = async () => {
  loading.value = true
  error.value = ''
  try {
    const username = currentUser.value
    const [mine, followed, musics] = await Promise.all([
      playlistService.getUserPlaylists(username),
      playlistService.getFollowedPlaylists(),
      musicService.getAll()
    ])
    myPlaylists.value = mine || []
    followedPlaylists.value = followed || []
    allMusics.value = musics || []
  } catch (err) {
    error.value = err.message || 'Erreur lors du chargement'
  } finally {
    loading.value = false
  }
}

onMounted(fetchAll)

const toggleCreateMusic = (musicId) => {
  const set = createForm.value.selectedMusicIds
  set.has(musicId) ? set.delete(musicId) : set.add(musicId)
}

const submitCreate = async () => {
  if (!createForm.value.name.trim()) {
    message.value = 'Le nom est requis'
    messageType.value = 'danger'
    return
  }
  if (createForm.value.selectedMusicIds.size === 0) {
    message.value = 'Sélectionnez au moins une musique'
    messageType.value = 'danger'
    return
  }
  savingCreate.value = true
  try {
    await playlistService.createPlaylist({
      id: null,
      name: createForm.value.name,
      decription: createForm.value.description || '',
      musics: Array.from(createForm.value.selectedMusicIds).map(id => ({ musicId: id }))
    })
    message.value = 'Playlist créée'
    messageType.value = 'success'
    resetCreateForm()
    await fetchAll()
  } catch (err) {
    message.value = err.message || 'Erreur lors de la création'
    messageType.value = 'danger'
  } finally {
    savingCreate.value = false
  }
}

const startEdit = async (playlistId) => {
  editingPlaylistId.value = playlistId
  savingEdit.value = false
  message.value = ''
  try {
    const details = await playlistService.getPlaylistById(playlistId)
    const currentIds = new Set((details.musics || []).map(m => m.musicId))
    editSelection.value = currentIds
  } catch (err) {
    message.value = err.message || 'Erreur lors du chargement de la playlist'
    editingPlaylistId.value = null
  }
}

const toggleEditMusic = (musicId) => {
  const set = editSelection.value
  set.has(musicId) ? set.delete(musicId) : set.add(musicId)
}

const saveEdit = async () => {
  if (!editingPlaylistId.value) return
  if (editSelection.value.size === 0) {
    message.value = 'Une playlist doit contenir au moins une musique'
    messageType.value = 'danger'
    return
  }
  savingEdit.value = true
  try {
    // sync by adding missing and removing unchecked
    const details = await playlistService.getPlaylistById(editingPlaylistId.value)
    const current = new Set((details.musics || []).map(m => m.musicId))
    const target = editSelection.value

    const toAdd = Array.from(target).filter(id => !current.has(id))
    const toRemove = Array.from(current).filter(id => !target.has(id))

    await Promise.all([
      ...toAdd.map(id => playlistService.addMusicToPlaylist(editingPlaylistId.value, id)),
      ...toRemove.map(id => playlistService.removeMusicFromPlaylist(editingPlaylistId.value, id))
    ])

    message.value = 'Playlist mise à jour'
    messageType.value = 'success'
    editingPlaylistId.value = null
    await fetchAll()
  } catch (err) {
    message.value = err.message || 'Erreur lors de la mise à jour'
    messageType.value = 'danger'
  } finally {
    savingEdit.value = false
  }
}

const isMusicChecked = (set, musicId) => set.has(musicId)

const formatCreators = (creatorNames) => {
  if (!creatorNames) return ''
  return creatorNames.split(',').map(c => c.trim()).join(', ')
}
</script>

<template>
  <div class="container py-4">
    <h1 class="mb-4"><i class="bi bi-collection-music"></i> Playlists</h1>

    <div v-if="message" :class="['alert', 'alert-dismissible', 'fade', 'show', messageType === 'success' ? 'alert-success' : 'alert-danger']">
      {{ message }}
      <button type="button" class="btn-close" @click="message = ''" aria-label="Close"></button>
    </div>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-3 text-muted">Chargement des playlists...</p>
    </div>

    <div v-else-if="error" class="alert alert-danger">
      <i class="bi bi-exclamation-triangle"></i> {{ error }}
    </div>

    <div v-else class="row g-4">
      <div class="col-12 col-lg-4">
        <div class="card h-100">
          <div class="card-body">
            <h5 class="card-title"><i class="bi bi-plus-circle"></i> Nouvelle playlist</h5>
            <div class="mb-3">
              <label class="form-label">Nom *</label>
              <input v-model="createForm.name" type="text" class="form-control" placeholder="Ma playlist" />
            </div>
            <div class="mb-3">
              <label class="form-label">Description (optionnel)</label>
              <textarea v-model="createForm.description" class="form-control" rows="2" placeholder="Description..."></textarea>
            </div>
            <div class="mb-3">
              <label class="form-label">Sélectionnez des musiques *</label>
              <div class="border rounded p-2 music-list">
                <div v-if="allMusics.length === 0" class="text-muted small">Aucune musique disponible</div>
                <div v-else class="list-group small">
                  <label v-for="music in allMusics" :key="music.musicId" class="list-group-item d-flex align-items-center">
                    <input class="form-check-input me-2" type="checkbox" :checked="isMusicChecked(createForm.selectedMusicIds, music.musicId)" @change="toggleCreateMusic(music.musicId)" />
                    <span class="fw-semibold">{{ music.title }}</span>
                    <span class="ms-auto text-muted">{{ formatCreators(music.creatorNames) }}</span>
                  </label>
                </div>
              </div>
            </div>
            <button class="btn btn-primary w-100" :disabled="savingCreate" @click="submitCreate">
              <span v-if="savingCreate" class="spinner-border spinner-border-sm me-2"></span>
              Créer
            </button>
          </div>
        </div>
      </div>

      <div class="col-12 col-lg-8">
        <div class="card mb-4">
          <div class="card-body">
            <h5 class="card-title"><i class="bi bi-person-lines-fill"></i> Mes playlists</h5>
            <div v-if="myPlaylists.length === 0" class="alert alert-info">Aucune playlist</div>
            <div v-else class="accordion" id="myPlaylists">
              <div class="accordion-item" v-for="pl in myPlaylists" :key="pl.id">
                <h2 class="accordion-header">
                  <button class="accordion-button" type="button" data-bs-toggle="collapse" :data-bs-target="`#pl-${pl.id}`">
                    {{ pl.name }}
                  </button>
                </h2>
                <div :id="`pl-${pl.id}`" class="accordion-collapse collapse" data-bs-parent="#myPlaylists">
                  <div class="accordion-body">
                    <p class="text-muted mb-2">{{ pl.description || 'Aucune description' }}</p>
                    <div v-if="pl.musics && pl.musics.length" class="list-group mb-3">
                      <div v-for="m in pl.musics" :key="m.musicId" class="list-group-item d-flex justify-content-between">
                        <span>{{ m.title }}</span>
                        <small class="text-muted">{{ formatCreators(m.creatorNames) }}</small>
                      </div>
                    </div>
                    <div v-else class="text-muted">Aucune musique</div>

                    <button class="btn btn-outline-primary btn-sm" @click="startEdit(pl.id)" data-bs-toggle="modal" data-bs-target="#editModal">
                      <i class="bi bi-pencil"></i> Modifier les musiques
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="card">
          <div class="card-body">
            <h5 class="card-title"><i class="bi bi-people"></i> Playlists suivies</h5>
            <div v-if="followedPlaylists.length === 0" class="alert alert-info">Aucune playlist suivie</div>
            <div v-else class="list-group">
              <div v-for="pl in followedPlaylists" :key="pl.id" class="list-group-item">
                <div class="d-flex justify-content-between align-items-center">
                  <div>
                    <div class="fw-semibold">{{ pl.name }}</div>
                    <div class="text-muted small">{{ pl.description || 'Aucune description' }}</div>
                  </div>
                  <span class="badge bg-secondary">{{ pl.musics?.length || 0 }} musiques</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal édition -->
    <div class="modal fade" id="editModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-lg modal-dialog-scrollable">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Modifier les musiques</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close" @click="editingPlaylistId = null"></button>
          </div>
          <div class="modal-body">
            <div v-if="!editingPlaylistId" class="text-muted">Sélectionnez une playlist</div>
            <div v-else>
              <div class="list-group">
                <label v-for="music in allMusics" :key="music.musicId" class="list-group-item d-flex align-items-center">
                  <input class="form-check-input me-2" type="checkbox" :checked="isMusicChecked(editSelection, music.musicId)" @change="toggleEditMusic(music.musicId)" />
                  <span class="fw-semibold">{{ music.title }}</span>
                  <span class="ms-auto text-muted">{{ formatCreators(music.creatorNames) }}</span>
                </label>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" @click="editingPlaylistId = null">Fermer</button>
            <button type="button" class="btn btn-primary" :disabled="savingEdit || !editingPlaylistId" @click="saveEdit">
              <span v-if="savingEdit" class="spinner-border spinner-border-sm me-2"></span>
              Sauvegarder
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.music-list {
  max-height: 240px;
  overflow: auto;
}
</style>

