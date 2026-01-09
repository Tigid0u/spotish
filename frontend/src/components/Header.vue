<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container-fluid">
      <router-link to="/" class="navbar-brand">
        <i class="bi bi-music-note-list"></i> Spotish
      </router-link>
    </div>
  </nav>
</template>

