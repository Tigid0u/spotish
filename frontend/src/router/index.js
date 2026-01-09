import { createRouter, createWebHistory } from 'vue-router'

// Import views
import HomePage from '../views/HomePage.vue'

const routes = [
  {
    path: '/',
    component: HomePage,
    meta: { requiresAuth: false }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router

