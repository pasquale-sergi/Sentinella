import { defineStore } from 'pinia';
import axios from 'axios';

const API_URL = process.env.VUE_APP_API_BASE_URL; 
export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user')), 
    token: localStorage.getItem('token'),
  }),
  getters: {
    isLoggedIn: (state) => !!state.token, 
    currentUser: (state) => state.user,
  },
  actions: {
    async login(username, password) {
      try {
        console.log("sending login data: ", username,password);
        const response = await axios.post(`${API_URL}/auth/signin`, { username, password });
        const { token, id, username: userUsername, email } = response.data;

        this.user = { id, username: userUsername, email };
        this.token = token;

        localStorage.setItem('user', JSON.stringify(this.user));
        localStorage.setItem('token', this.token);

        
        axios.defaults.headers.common['Authorization'] = `Bearer ${this.token}`;

        return true; 
      } catch (error) {
        console.error('Login failed:', error);
        this.logout(); 
        throw error; 
      }
    },
    logout() {
      this.user = null;
      this.token = null;
      localStorage.removeItem('user');
      localStorage.removeItem('token');
      delete axios.defaults.headers.common['Authorization']; 
    },
    
    initializeAuth() {
      if (this.token) {
        axios.defaults.headers.common['Authorization'] = `Bearer ${this.token}`;
      } else {
        delete axios.defaults.headers.common['Authorization'];
      }
    },
  },
});