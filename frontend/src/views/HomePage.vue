<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<template>
  <div class="container py-5">
    <div class="text-center mb-5">
      <h1 class="display-3 mb-3">
        <i class="bi bi-music-note-list text-primary"></i> Spotish
      </h1>
      <p class="lead text-muted">Votre plateforme de musique en streaming</p>
    </div>

    <div class="row justify-content-center">
      <div class="col-md-6 text-center">
        <div class="card shadow">
          <div class="card-body py-5">
            <h3 class="mb-4">Bienvenue !</h3>
            <p class="mb-4">Connectez-vous pour découvrir et écouter de la musique</p>
              <i class="bi bi-box-arrow-in-right"></i> Se connecter
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

