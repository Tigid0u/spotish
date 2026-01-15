import { apiRequest } from './apiClient.js'

const playlistService = {
  async createPlaylist(payload) {
    return apiRequest('/playlists', {
      method: 'POST',
      body: JSON.stringify(payload)
    })
  },

  async getUserPlaylists(username) {
    try {
      return await apiRequest(`/playlists/user/${encodeURIComponent(username)}`, { method: 'GET' })
    } catch (error) {
      if (error.status === 404) return []
      throw error
    }
  },

  async getFollowedPlaylists() {
    try {
      return await apiRequest('/playlists/followed', { method: 'GET' })
    } catch (error) {
      if (error.status === 404) return []
      throw error
    }
  },

  async followPlaylist(playlistId) {
    return apiRequest(`/playlists/followed/${playlistId}`, { method: 'POST' })
  },

  async addMusicToPlaylist(playlistId, musicId) {
    return apiRequest(`/playlists/${playlistId}/musics/${musicId}`, { method: 'POST' })
  },

  async removeMusicFromPlaylist(playlistId, musicId) {
    return apiRequest(`/playlists/${playlistId}/musics/${musicId}`, { method: 'DELETE' })
  },

  async getPlaylistById(playlistId) {
    return apiRequest(`/playlists/${playlistId}`, { method: 'GET' })
  }
}

export default playlistService

