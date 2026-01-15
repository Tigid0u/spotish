/**
 * User Service
 * Handles user-related API calls
 */

import { apiRequest } from './apiClient.js';

const userService = {
  /**
   * Get all users
   * @returns {Promise<Array>}
   */
  async getAllUsers() {
    try {
      const users = await apiRequest('/utilisateurs', {
        method: 'GET',
      });
      return users;
    } catch (error) {
      console.error('UserService: Error getting users:', error);
      throw error;
    }
  },

  /**
   * Get user by username
   * @param {string} username
   * @returns {Promise<Object>}
   */
  async getUser(username) {
    try {
      return await apiRequest(`/utilisateurs/${encodeURIComponent(username)}`, {
        method: 'GET',
      });
    } catch (error) {
      console.error('UserService: Error getting user:', error);
      if (error.status === 404) {
        throw new Error('Utilisateur non trouvé');
      }
      throw error;
    }
  },

  /**
   * Create a new user
   * @param {Object} userData - User data
   * @param {string} userData.nomUtilisateur - Username
   * @param {string} userData.nom - Last name
   * @param {string} userData.prenom - First name
   * @param {string} userData.dateDeNaissance - Birth date (YYYY-MM-DD)
   * @param {string} userData.email - Email address
   * @returns {Promise<Object>}
   */
  async createUser(userData) {
    try {
      return await apiRequest('/utilisateurs', {
        method: 'POST',
        body: JSON.stringify(userData),
      });
    } catch (error) {
      if (error.status === 409) {
        throw new Error('Cet utilisateur existe déjà');
      }
      if (error.status === 400) {
        throw new Error('Données invalides');
      }
      throw error;
    }
  },
};

export default userService;
