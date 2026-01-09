import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
    plugins: [vue()],
    server: {
        proxy: {
            '/login': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                secure: false,
            },
            '/logout': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                secure: false,
            },
            '/utilisateurs': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                secure: false,
            },
            '/musics': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                secure: false,
            },
        },
    },
})
