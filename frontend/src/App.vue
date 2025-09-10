<script setup>
import { RouterLink, RouterView } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';

const authStore = useAuthStore();
const router = useRouter();

const handleLogout = () => {
  authStore.logout();
  router.push('/login'); 
};
</script>

<template>
  <div id="app-container">
    <header class="app-header">
      <div class="header-content">
        <h1 class="app-title">Sentinella</h1>
        <nav class="main-nav">
          <RouterLink to="/" class="nav-link">Home</RouterLink>
          <RouterLink v-if="authStore.isLoggedIn" to="/dashboard" class="nav-link">Dashboard</RouterLink>
          <RouterLink v-if="authStore.isLoggedIn" to="/history" class="nav-link">History</RouterLink>
          <RouterLink v-if="authStore.isLoggedIn" to="/unclaimed-sensors" class="nav-link">Claim Sensor</RouterLink>
        </nav>
        <div class="user-actions">
          <span v-if="authStore.isLoggedIn" class="welcome-message">Welcome, {{ authStore.currentUser?.username }}!</span>
          <template v-if="authStore.isLoggedIn">
            <button @click="handleLogout" class="btn btn-secondary">Logout</button>
          </template>
          <template v-else>
            <RouterLink to="/login" class="btn btn-primary">Login</RouterLink>
          </template>
        </div>
      </div>
    </header>

    <main class="app-main-content">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
#app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.app-header {
  background-color: var(--color-background-soft);
  border-bottom: 1px solid var(--color-border);
  padding: 1rem 2rem;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap; 
}

.app-title {
  font-size: 1.8rem;
  color: var(--color-heading);
  font-weight: bold;
}

.main-nav {
  display: flex;
  gap: 1.5rem;
  margin: 0 2rem;
  flex-grow: 1;
  justify-content: center;
}

.nav-link {
  text-decoration: none;
  color: var(--color-text);
  font-weight: 500;
  padding: 0.5rem 0;
  transition: color 0.2s ease, transform 0.2s ease;
}

.nav-link:hover {
  color: var(--color-accent-blue);
  transform: translateY(-2px);
}

.nav-link.router-link-exact-active {
  color: var(--color-accent-blue);
  border-bottom: 2px solid var(--color-accent-blue);
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.welcome-message {
  color: var(--color-text);
  font-size: 0.95rem;
}

.app-main-content {
  flex-grow: 1;
  max-width: 1200px;
  margin: 2rem auto;
  padding: 0 2rem;
  width: 100%;
}


.btn {
  padding: 0.6rem 1.2rem;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: background-color 0.2s ease, border-color 0.2s ease, color 0.2s ease;
  text-decoration: none; 
  display: inline-block;
  text-align: center;
}

.btn-primary {
  background-color: var(--color-accent-blue);
  color: white;
  border: 1px solid var(--color-accent-blue);
}

.btn-primary:hover {
  background-color: #0069d9; 
  border-color: #0062cc;
}

.btn-secondary {
  background-color: #6c757d; 
  color: white;
  border: 1px solid #6c757d;
}

.btn-secondary:hover {
  background-color: #5a6268;
  border-color: #545b62;
}


@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 1rem;
  }
  .main-nav {
    flex-wrap: wrap;
    justify-content: center;
    margin: 1rem 0;
  }
  .app-main-content {
    margin: 1rem auto;
    padding: 0 1rem;
  }
}
</style>