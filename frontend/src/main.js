import { createApp } from 'vue';
import { createPinia } from 'pinia'; 
import App from './App.vue';
import router from './router';
import axios from 'axios'; 
import './assets/base.css';

const API_URL = process.env.VUE_APP_API_BASE_URL; 
axios.defaults.baseURL = API_URL;

const app = createApp(App);
const pinia = createPinia(); 

app.use(pinia);
app.use(router); 


import { useAuthStore } from '@/stores/auth';
const authStore = useAuthStore();
authStore.initializeAuth();

app.mount('#app');