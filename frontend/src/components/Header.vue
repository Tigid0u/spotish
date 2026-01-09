<script setup>
import { useRouter } from 'vue-router'
import authService from '../services/authService'

const router = useRouter()
const loggedIn = authService.loggedIn
const username = authService.username

const handleLogout = async () => {
  await authService.logout()
  router.push('/')
}
</script>

<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container-fluid">
      <router-link to="/" class="navbar-brand">
        <i class="bi bi-music-note-list"></i> Spotish
      </router-link>

      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav me-auto">
          <li class="nav-item" v-if="loggedIn">
            <router-link to="/musics" class="nav-link">
              <i class="bi bi-music-note"></i> Musiques
            </router-link>
          </li>
          <li class="nav-item" v-if="loggedIn">
            <router-link to="/liked" class="nav-link">
              <i class="bi bi-heart-fill"></i> Favoris
            </router-link>
          </li>
        </ul>

        <ul class="navbar-nav">
          <li class="nav-item" v-if="loggedIn">
            <router-link to="/profile" class="nav-link">
              <i class="bi bi-person-circle"></i> {{ username }}
            </router-link>
          </li>
          <li class="nav-item" v-if="loggedIn">
            <button @click="handleLogout" class="btn btn-outline-light btn-sm ms-2">
              <i class="bi bi-box-arrow-right"></i> Déconnexion
            </button>
          </li>
          <li class="nav-item" v-if="!loggedIn">
            <router-link to="/login" class="btn btn-light btn-sm">
              Connexion
            </router-link>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

