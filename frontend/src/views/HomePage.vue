<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import authService from '../services/authService'

const router = useRouter()
const loggedIn = computed(() => authService.isLoggedIn())
</script>

<template>
  <div class="container py-5">
    <div class="text-center mb-5">
      <h1 class="display-3 mb-3">
        <i class="bi bi-music-note-list text-primary"></i> Spotish
      </h1>
      <p class="lead text-muted">Votre plateforme de musique en streaming</p>
    </div>

    <div v-if="!loggedIn" class="row justify-content-center">
      <div class="col-md-6 text-center">
        <div class="card shadow">
          <div class="card-body py-5">
            <h3 class="mb-4">Bienvenue !</h3>
            <p class="mb-4">Connectez-vous pour découvrir et écouter de la musique</p>
            <router-link to="/login" class="btn btn-primary btn-lg">
              <i class="bi bi-box-arrow-in-right"></i> Se connecter
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="row g-4">
      <div class="col-md-6">
        <div class="card h-100 shadow-sm">
          <div class="card-body text-center py-5">
            <i class="bi bi-music-note-list display-1 text-primary mb-3"></i>
            <h4>Mes Musiques</h4>
            <p class="text-muted">Découvrez vos dernières et meilleures écoutes</p>
            <router-link to="/musics" class="btn btn-primary">
              Voir les musiques
            </router-link>
          </div>
        </div>
      </div>

      <div class="col-md-6">
        <div class="card h-100 shadow-sm">
          <div class="card-body text-center py-5">
            <i class="bi bi-heart-fill display-1 text-danger mb-3"></i>
            <h4>Mes Favoris</h4>
            <p class="text-muted">Retrouvez toutes vos musiques préférées</p>
            <router-link to="/liked" class="btn btn-danger">
              Voir les favoris
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

