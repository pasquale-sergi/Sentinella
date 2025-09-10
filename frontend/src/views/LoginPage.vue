<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();
const router = useRouter();

const username = ref('');
const password = ref('');
const errorMessage = ref('');

const login = async () => {
  try {
    errorMessage.value = '';
    await authStore.login(username.value, password.value);
    router.push('/dashboard'); // Navigate to dashboard on success
  } catch (error) {
    errorMessage.value = 'Login failed. Please check your credentials.';
    console.error('Login error:', error);
  }
};
</script>

<template>
  <div class="login-page">
    <h1 class="page-title">Login</h1>
    <form @submit.prevent="login" class="login-form">
      <div class="form-group">
        <label for="username">Username:</label>
        <input type="text" id="username" v-model="username" required class="form-input" />
      </div>
      <div class="form-group">
        <label for="password">Password:</label>
        <input type="password" id="password" v-model="password" required class="form-input" />
      </div>
      <button type="submit" class="btn btn-primary login-button">Login</button>
      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    </form>
  </div>
</template>

<style scoped>
.login-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  max-width: 400px;
  margin: 50px auto;
  background-color: var(--color-background-soft);
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.page-title {
  color: var(--color-heading);
  font-size: 2rem;
  margin-bottom: 30px;
}

.login-form {
  width: 100%;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: var(--color-text);
  font-weight: 500;
}

.form-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  font-size: 1rem;
  transition: border-color 0.2s ease;
}

.form-input:focus {
  border-color: var(--color-accent-blue);
  outline: none;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.25);
}

.login-button {
  width: 100%;
  padding: 12px;
  font-size: 1.1rem;
}

.error-message {
  color: var(--color-accent-red);
  margin-top: 20px;
  text-align: center;
  font-size: 0.9rem;
}
</style>