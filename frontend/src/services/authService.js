/**
 * Authentication Service
 * Handles user login and logout operations
 */

import { apiRequest } from './apiClient.js';
import { ref } from 'vue';

const loggedIn = ref(false);
const username = ref(null);

const authService = {
  /**
   * Login user with username
   * @param {string} usernameInput - The username to login with
   * @returns {Promise<void>}
   */
  async login(usernameInput) {
    try {
      const encodedUsername = encodeURIComponent(usernameInput);

      const endpoint = `/login/${encodedUsername}`;

      await apiRequest(endpoint, {
        method: 'POST',
      });

      username.value = usernameInput;
      loggedIn.value = true;
      return { success: true };
    } catch (error) {
      console.error('AuthService: Login error:', error);
      console.error('AuthService: Error status:', error.status);
      console.error('AuthService: Error data:', error.data);

      if (error.status === 404) {
        throw new Error('Utilisateur non trouvé');
      }
      throw error;
    }
  },

  /**
   * Logout current user
   * @returns {Promise<void>}
   */
  async logout() {
    await apiRequest('/logout', {
      method: 'POST',
    });
    username.value = null;
    loggedIn.value = false;
    return { success: true };
  },

  /**
   * Check if user is logged in
   * @returns {boolean}
   */
  isLoggedIn() {
    return loggedIn.value;
  },

  /**
   * Get current username
   * @returns {string|null}
   */
  getCurrentUsername() {
    return username.value;
  },

  loggedIn,
  username,
};

export default authService;
