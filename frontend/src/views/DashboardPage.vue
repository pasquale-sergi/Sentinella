<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router'; 

const authStore = useAuthStore();
const userSensors = ref([]);
const latestReadings = ref({}); 
const errorMessage = ref('');
const isLoading = ref(true);
const router = useRouter(); 

onMounted(async () => {
  if (!authStore.isLoggedIn) {
    errorMessage.value = 'Not authenticated. Redirecting...';
    isLoading.value = false;
    router.push('/login');
    return;
  }

  try {
    const sensorsResponse = await axios.get('/sensors/all');
    userSensors.value = sensorsResponse.data;

    for (const sensor of userSensors.value) {
      try {
        const readingsResponse = await axios.get(`/sensor-data/latest?sensorId=${sensor.id}`);
        if (readingsResponse.data && readingsResponse.data.length > 0) {
          latestReadings.value[sensor.id] = readingsResponse.data[0];
        } else {
          latestReadings.value[sensor.id] = { error: 'No data available' };
          console.warn(`No data for sensor ${sensor.id}.`);
        }
      } catch (readingError) {
        console.warn(`Could not fetch latest reading for sensor ${sensor.id}:`, readingError);
        latestReadings.value[sensor.id] = { error: `Error fetching data: ${readingError.message || readingError}` };
      }
    }
  } catch (error) {
    if (error.response && error.response.status === 401) {
        errorMessage.value = 'Session expired. Please log in again.';
        authStore.logout();
        router.push('/login');
    } else {
        errorMessage.value = `Failed to load dashboard data: ${error.message || error}`;
    }
    console.error('Dashboard load error:', error);
  } finally {
    isLoading.value = false;
  }
});
</script>

<template>
  <div class="dashboard-page">
    <h1 class="page-title">Your Sensors Dashboard</h1>
    <p v-if="isLoading" class="loading-message">Loading sensors and data...</p>
    <p v-else-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    <div v-else-if="userSensors.length === 0" class="no-sensors-message">
      <p>You have no sensors registered yet. </p>
      <router-link to="/unclaimed-sensors" class="btn btn-primary">Go to Claim Sensors</router-link>
    </div>
    <div v-else class="sensor-cards-grid">
      <div v-for="sensor in userSensors" :key="sensor.id" class="sensor-card">
        <h2 class="sensor-name">{{ sensor.name }}</h2>
        <p class="sensor-device-id">ID: {{ sensor.deviceId }}</p>
        <p class="sensor-location">Location: {{ sensor.location || 'N/A' }}</p>
        
        <hr class="card-divider" />

        <div v-if="latestReadings[sensor.id]">
          <h3 class="reading-title">Latest Reading:</h3>
          <p v-if="latestReadings[sensor.id].error" class="reading-error-message">{{ latestReadings[sensor.id].error }}</p>
          <div v-else class="reading-details">
            <p><strong>Temp:</strong> {{ latestReadings[sensor.id].temperature }}°C</p>
            <p><strong>Humidity:</strong> {{ latestReadings[sensor.id].humidity }}%</p>
            <p><strong>Pressure:</strong> {{ (latestReadings[sensor.id].pressure / 100).toFixed(2) }} hPa</p>
            <p><strong>Gas Res:</strong> {{ latestReadings[sensor.id].gasResistance }} Ohms</p>
            <p :class="['anomaly-status', {'is-anomaly': latestReadings[sensor.id].isAnomaly}]">
              <strong>Anomaly:</strong> {{ latestReadings[sensor.id].isAnomaly ? 'Yes' : 'No' }}
              <span v-if="latestReadings[sensor.id].isAnomaly">(Error: {{ latestReadings[sensor.id].reconstructionError?.toFixed(4) || 'N/A' }})</span>
            </p>
            <p class="timestamp">Last Updated: {{ new Date(latestReadings[sensor.id].timestamp).toLocaleString() }}</p>
          </div>
        </div>
        <div v-else class="no-reading-message">
          <p>No latest reading available for this sensor yet.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard-page {
  padding: 20px;
}

.page-title {
  color: var(--color-heading);
  font-size: 2.5rem;
  margin-bottom: 30px;
  text-align: center;
}

.loading-message, .error-message, .no-sensors-message {
  text-align: center;
  padding: 20px;
  background-color: var(--color-background-soft);
  border-radius: 8px;
  color: var(--color-text);
  font-size: 1.1rem;
}

.error-message {
  color: var(--color-accent-red);
  border: 1px solid var(--color-accent-red);
  background-color: rgba(220, 53, 69, 0.1);
}

.no-sensors-message {
  border: 1px dashed var(--color-border-hover);
}

.sensor-cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); /* Responsive grid */
  gap: 30px;
  margin-top: 30px;
}

.sensor-card {
  background-color: #ffffff;
  border: 1px solid var(--color-border);
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.sensor-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 20px rgba(0,0,0,0.12);
}

.sensor-name {
  font-size: 1.6rem;
  color: var(--color-heading);
  margin-bottom: 5px;
}

.sensor-device-id, .sensor-location, .sensor-api-key {
  font-size: 0.95rem;
  color: var(--color-text);
  margin-bottom: 3px;
}

.api-key-value {
  font-family: 'Courier New', Courier, monospace; /* Monospace for API key */
  background-color: var(--color-background-soft);
  padding: 2px 6px;
  border-radius: 4px;
}

.card-divider {
  border: 0;
  height: 1px;
  background-color: var(--color-border);
  margin: 20px 0;
}

.reading-title {
  font-size: 1.25rem;
  color: var(--color-accent-blue);
  margin-bottom: 15px;
  font-weight: 600;
}

.reading-error-message, .no-reading-message {
  color: var(--color-accent-orange);
  background-color: rgba(255, 193, 7, 0.1);
  padding: 10px;
  border-radius: 6px;
  font-size: 0.95rem;
  text-align: center;
}

.reading-details p {
  margin-bottom: 8px;
  color: var(--color-text);
}

.reading-details strong {
  color: var(--color-heading);
  min-width: 80px; /* Align labels */
  display: inline-block;
}

.anomaly-status {
  font-weight: bold;
}

.anomaly-status.is-anomaly {
  color: var(--color-accent-red);
}

.anomaly-status:not(.is-anomaly) {
  color: var(--color-accent-green);
}

.timestamp {
  font-size: 0.85rem;
  color: #777;
  margin-top: 15px;
  text-align: right;
}
</style>