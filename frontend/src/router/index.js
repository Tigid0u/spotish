import { createRouter, createWebHistory } from 'vue-router'
import authService from '../services/authService.js'

// Import views
import HomePage from '../views/HomePage.vue'
import LoginPage from '../views/LoginPage.vue'
import MusicsPage from '../views/MusicsPage.vue'
import LikedPage from '../views/LikedPage.vue'
import ProfilePage from '../views/ProfilePage.vue'

const routes = [
  {
    path: '/',
    component: HomePage,
    meta: { requiresAuth: false }
  },
  {
    path: '/login',
    component: LoginPage,
    meta: { requiresAuth: false }
  },
  {
    path: '/musics',
    component: MusicsPage,
    meta: { requiresAuth: true }
  },
  {
    path: '/liked',
    component: LikedPage,
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    component: ProfilePage,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})


// Navigation guard to check authentication
router.beforeEach((to, from, next) => {
  const isLoggedIn = authService.loggedIn.value

  if (to.meta.requiresAuth && !isLoggedIn) {
    next('/login')
  } else {
    next()
  }
})

export default router
