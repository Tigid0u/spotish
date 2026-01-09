<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import authService from '../services/authService'
import userService from '../services/userService'

const router = useRouter()
const users = ref([])
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  try {
    users.value = await userService.getAllUsers()
  } catch (err) {
    error.value = 'Erreur lors du chargement des utilisateurs'
  } finally {
    loading.value = false
  }
})

const loginAsUser = async (username) => {
  try {
    await authService.login(username)
    router.push('/')
  } catch (err) {
    error.value = 'Erreur lors de la connexion: ' + (err.message || err)
  }
}
</script>

<template>
  <div class="container py-5">
    <div class="row justify-content-center">
      <div class="col-md-8">
        <div class="card shadow">
          <div class="card-header bg-primary text-white">
            <h3 class="mb-0"><i class="bi bi-person-circle"></i> Connexion à Spotish</h3>
          </div>
          <div class="card-body">
            <p class="text-muted mb-4">Sélectionnez un utilisateur pour vous connecter :</p>

            <div v-if="loading" class="text-center py-5">
              <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Chargement...</span>
              </div>
            </div>

            <div v-else-if="error" class="alert alert-danger">
              {{ error }}
            </div>

            <div v-else-if="users.length === 0" class="alert alert-info">
              Aucun utilisateur trouvé dans la base de données
            </div>

            <div v-else class="list-group">
              <button
                v-for="user in users"
                :key="user.username"
                @click="loginAsUser(user.username)"
                class="list-group-item list-group-item-action d-flex justify-content-between align-items-center"
              >
                <div>
                  <h5 class="mb-1">{{ user.username }}</h5>
                  <p class="mb-1 text-muted">{{ user.fname }} {{ user.lname }}</p>
                  <small class="text-muted">{{ user.email }}</small>
                </div>
                <i class="bi bi-arrow-right-circle fs-4 text-primary"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
