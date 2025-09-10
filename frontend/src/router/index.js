import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth'; 
import HomeView from '../views/HomeView.vue';
import LoginPage from '../views/LoginPage.vue'; 
import DashboardPage from '../views/DashboardPage.vue'; 
import HistoryPage from '../views/HistoryPage.vue'
import UnclaimedSensorPage from '../views/UnclaimedSensorPage.vue';

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
  },
  {
    path: '/login',
    name: 'login',
    component: LoginPage,
  },
  {
    path: '/dashboard',
    name: 'dashboard',
    component: DashboardPage,
    meta: { requiresAuth: true }, 
  },
  {
    path: '/history/:sensorId?', 
    name: 'history',
    component: HistoryPage,
    meta: { requiresAuth: true },
  },
    {
    path: '/unclaimed-sensors', 
    name: 'unclaimedSensors',
    component: UnclaimedSensorPage,
    meta: { requiresAuth: true },
  },
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL), 
  routes,
});

// Navigation Guard: checks if a route requires authentication
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore(); 
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    next('/login'); 
  } else {
    next(); 
  }
});

export default router;