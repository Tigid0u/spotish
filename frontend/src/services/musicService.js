/**
 * Music Service
 * Handles music-related API calls
 */

import { apiRequest } from './apiClient.js';

const musicService = {
  /**
   * Get 10 last listened musics
   * @returns {Promise<Array>}
   */
  async getLastListened() {
    try {
      return await apiRequest('/musics/last-listened', {
        method: 'GET',
      });
    } catch (error) {
      if (error.status === 404) {
        return [];
      }
      throw error;
    }
  },

  /**
   * Get 3 most listened musics
   * @returns {Promise<Array>}
   */
  async getMostListened() {
    try {
      return await apiRequest('/musics/most-listened', {
        method: 'GET',
      });
    } catch (error) {
      if (error.status === 404) {
        return [];
      }
      throw error;
    }
  },

  /**
   * Get all liked musics
   * @returns {Promise<Array>}
   */
  async getLiked() {
    try {
      return await apiRequest('/musics/liked', {
        method: 'GET',
      });
    } catch (error) {
      if (error.status === 404) {
        return [];
      }
      throw error;
    }
  },

  /**
   * Get music by ID
   * @param {number} musicId
   * @returns {Promise<Object>}
   */
  async getMusic(musicId) {
    try {
      return await apiRequest(`/musics/${musicId}`, {
        method: 'GET',
      });
    } catch (error) {
      if (error.status === 404) {
        throw new Error('Musique non trouvée');
      }
      throw error;
    }
  },

  /**
   * Like a music
   * @param {number} musicId
   * @returns {Promise<void>}
   */
  async likeMusic(musicId) {
    try {
      await apiRequest(`/musics/liked/${musicId}`, {
        method: 'POST',
      });
      return { success: true };
    } catch (error) {
      throw error;
    }
  },

  /**
   * Get all musics
   * @returns {Promise<Array>}
   */
  async getAll() {
    try {
      return await apiRequest('/musics', { method: 'GET' }) || []
    } catch (error) {
      if (error.status === 404) {
        return []
      }
      throw error
    }
  },
};

export default musicService;
