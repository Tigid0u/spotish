<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import userService from '../services/userService'
import authService from '../services/authService'

const router = useRouter()
const user = ref(null)
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  const username = authService.getCurrentUsername()
  if (!username) {
    router.push('/login')
    return
  }

  try {
    user.value = await userService.getUser(username)
  } catch (err) {
    error.value = 'Erreur lors du chargement du profil'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="container py-4">
    <div class="row justify-content-center">
      <div class="col-md-8">
        <h1 class="mb-4"><i class="bi bi-person-circle"></i> Mon Profil</h1>

        <div v-if="loading" class="text-center py-5">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Chargement...</span>
          </div>
        </div>

        <div v-else-if="error" class="alert alert-danger">
          {{ error }}
        </div>

        <div v-else-if="user" class="card shadow">
          <div class="card-header bg-primary text-white">
            <h3 class="mb-0">
              <i class="bi bi-person-badge"></i> {{ user.username }}
            </h3>
          </div>
          <div class="card-body">
            <div class="row mb-3">
              <div class="col-sm-4 fw-bold">Prénom :</div>
              <div class="col-sm-8">{{ user.fname }}</div>
            </div>
            <div class="row mb-3">
              <div class="col-sm-4 fw-bold">Nom :</div>
              <div class="col-sm-8">{{ user.lname }}</div>
            </div>
            <div class="row mb-3">
              <div class="col-sm-4 fw-bold">Email :</div>
              <div class="col-sm-8">{{ user.email }}</div>
            </div>
            <div class="row mb-3">
              <div class="col-sm-4 fw-bold">Date de naissance :</div>
              <div class="col-sm-8">{{ user.birthdate }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

