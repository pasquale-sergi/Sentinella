<!-- src/views/HistoryPage.vue -->
<script setup>
import { ref, onMounted, watch } from 'vue';
import axios from 'axios';
import { useAuthStore } from '@/stores/auth';
import { useRoute, useRouter } from 'vue-router';

const authStore = useAuthStore();
const route = useRoute();
const router = useRouter();

const userSensors = ref([]);
const selectedSensorId = ref(null);
const historicalReadings = ref([]);
const errorMessage = ref('');
const isLoadingSensors = ref(true);
const isLoadingReadings = ref(false);

// Fetch all sensors for the current user
const fetchUserSensors = async () => {
  isLoadingSensors.value = true;
  errorMessage.value = '';
  try {
    const response = await axios.get('/sensors/all');
    userSensors.value = response.data;

    // If sensorId is provided in the route, try to pre-select it
    if (route.params.sensorId) {
      const foundSensor = userSensors.value.find(s => s.id == route.params.sensorId);
      if (foundSensor) {
        selectedSensorId.value = foundSensor.id;
      } else {
        errorMessage.value = `Sensor with ID ${route.params.sensorId} not found or not owned by you.`;
        // If not found, maybe redirect to /history without ID or clear selection
        router.push({ name: 'history' });
      }
    } else if (userSensors.value.length > 0) {
      // If no ID in route, and user has sensors, select the first one by default
      selectedSensorId.value = userSensors.value[0].id;
    }
  } catch (error) {
    if (error.response && error.response.status === 401) {
      errorMessage.value = 'Session expired. Please log in again.';
      authStore.logout();
      router.push('/login');
    } else {
      errorMessage.value = `Failed to load user sensors: ${error.message || error}`;
    }
    console.error('Fetch user sensors error:', error);
  } finally {
    isLoadingSensors.value = false;
  }
};

// Fetch historical readings for the selected sensor
const fetchHistoricalReadings = async (sensorId) => {
  if (!sensorId) {
    historicalReadings.value = []; // Clear readings if no sensor is selected
    return;
  }

  isLoadingReadings.value = true;
  errorMessage.value = '';
  try {
    // This endpoint returns a list of readings, perfect for history!
    const response = await axios.get(`/sensor-data/latest?sensorId=${sensorId}`);
    historicalReadings.value = response.data;
  } catch (error) {
    if (error.response && error.response.status === 401) {
      errorMessage.value = 'Session expired. Please log in again.';
      authStore.logout();
      router.push('/login');
    } else if (error.response && error.response.status === 403) {
      errorMessage.value = 'You do not have permission to view this sensor\'s data.';
    } else {
      errorMessage.value = `Failed to load historical data: ${error.message || error}`;
    }
    console.error(`Fetch historical readings error for sensor ${sensorId}:`, error);
  } finally {
    isLoadingReadings.value = false;
  }
};

// On component mount, first fetch user's sensors
onMounted(() => {
  if (!authStore.isLoggedIn) {
    router.push('/login');
    return;
  }
  fetchUserSensors();
});

// Watch for changes in selectedSensorId or route.params.sensorId
// This will trigger fetching readings whenever the sensor selection changes
watch(selectedSensorId, (newId) => {
  if (newId) {
    fetchHistoricalReadings(newId);
    // Optionally update the URL to reflect the selected sensor
    if (route.params.sensorId != newId) { // Check for type coercion
      router.push({ name: 'history', params: { sensorId: newId } });
    }
  } else {
    historicalReadings.value = []; // Clear readings if no sensor is selected
  }
});


watch(() => route.params.sensorId, (newRouteSensorId) => {
  if (newRouteSensorId && newRouteSensorId != selectedSensorId.value) {
    selectedSensorId.value = newRouteSensorId;
  } else if (!newRouteSensorId && selectedSensorId.value) {

     selectedSensorId.value = null;
  }
});
</script>

<template>
  <div class="history-page">
    <h1 class="page-title">Sensor Data History</h1>

    <div v-if="isLoadingSensors" class="loading-message">Loading your sensors...</div>
    <div v-else-if="errorMessage" class="error-message">{{ errorMessage }}</div>
    <div v-else-if="userSensors.length === 0" class="no-sensors-message">
      <p>You have no sensors registered yet.</p>
      <router-link to="/unclaimed-sensors" class="btn btn-primary">Go to Claim Sensors</router-link>
    </div>
    <div v-else class="sensor-selection-section">
      <label for="sensorSelect" class="form-label">Select a Sensor:</label>
      <select id="sensorSelect" v-model="selectedSensorId" class="form-select">
        <option :value="null" disabled>-- Choose a Sensor --</option>
        <option v-for="sensor in userSensors" :key="sensor.id" :value="sensor.id">
          {{ sensor.name }} (ID: {{ sensor.deviceId }})
        </option>
      </select>
    </div>

    <div v-if="selectedSensorId" class="readings-section">
      <h2 class="section-title">Historical Readings for {{ userSensors.find(s => s.id == selectedSensorId)?.name }}</h2>
      <p v-if="isLoadingReadings" class="loading-message">Loading historical data...</p>
      <p v-else-if="historicalReadings.length === 0" class="no-readings-message">No historical data available for this sensor.</p>
      <div v-else class="readings-table-container">
        <table class="readings-table">
          <thead>
            <tr>
              <th>Timestamp</th>
              <th>Temp (°C)</th>
              <th>Humidity (%)</th>
              <th>Pressure (hPa)</th>
              <th>Gas Res (Ohms)</th>
              <th>Anomaly</th>
              <th>Error</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="reading in historicalReadings" :key="reading.id" :class="{'anomaly-row': reading.isAnomaly}">
              <td>{{ new Date(reading.timestamp).toLocaleString() }}</td>
              <td>{{ reading.temperature.toFixed(2) }}</td>
              <td>{{ reading.humidity.toFixed(2) }}</td>
              <td>{{ (reading.pressure / 100).toFixed(2) }}</td>
              <td>{{ reading.gasResistance.toFixed(0) }}</td>
              <td>
                <span :class="{'is-anomaly-indicator': reading.isAnomaly}">
                  {{ reading.isAnomaly ? 'Yes' : 'No' }}
                </span>
              </td>
              <td>{{ reading.reconstructionError.toFixed(4) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <div v-else class="select-sensor-prompt">
      <p>Please select a sensor from the dropdown above to view its history.</p>
    </div>
  </div>
</template>

<style scoped>
.history-page {
  padding: 20px;
}

.page-title {
  color: var(--color-heading);
  font-size: 2.5rem;
  margin-bottom: 30px;
  text-align: center;
}

.loading-message, .error-message, .no-sensors-message, .no-readings-message, .select-sensor-prompt {
  text-align: center;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 25px;
  background-color: var(--color-background-soft);
  color: var(--color-text);
  font-size: 1.1rem;
}

.error-message {
  color: var(--color-accent-red);
  border: 1px solid var(--color-accent-red);
  background-color: rgba(220, 53, 69, 0.1);
}

.no-sensors-message .btn-primary {
  margin-top: 15px;
}

.sensor-selection-section {
  background-color: #ffffff;
  border: 1px solid var(--color-border);
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  margin-bottom: 30px;
  text-align: center;
}

.form-label {
  display: block;
  margin-bottom: 15px;
  font-size: 1.2rem;
  color: var(--color-heading);
  font-weight: 600;
}

.form-select {
  width: 100%;
  max-width: 400px;
  padding: 10px 15px;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  background-color: white;
  font-size: 1rem;
  cursor: pointer;
  appearance: none; /* Remove default dropdown arrow */
  background-image: url('data:image/svg+xml;charset=US-ASCII,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22292.4%22%20height%3D%22292.4%22%3E%3Cpath%20fill%3D%22%23333%22%20d%3D%22M287%2069.4a17.6%2017.6%200%200%200-13-5.4H18.4c-6.5%200-12.3%203.2-16.1%208.1-3.8%204.9-4.7%2011.6-2.9%2017.7l139%20169c4.4%205.3%2011%208.2%2017.8%208.2s13.4-2.9%2017.8-8.2l139-169c1.9-6.1%201-12.8-2.9-17.7z%22%2F%3E%3C%2Fsvg%3E');
  background-repeat: no-repeat;
  background-position: right 0.7em top 50%, 0 0;
  background-size: 0.65em auto, 100%;
  margin-top: 10px;
}

.form-select:focus {
  border-color: var(--color-accent-blue);
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.25);
  outline: none;
}

.section-title {
  color: var(--color-heading);
  font-size: 2rem;
  margin-top: 40px;
  margin-bottom: 25px;
  text-align: center;
}

.readings-table-container {
  overflow-x: auto; /* Enable horizontal scrolling for smaller screens */
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  margin-top: 20px;
}

.readings-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 700px; /* Ensure table is wide enough for columns */
}

.readings-table th, .readings-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid var(--color-border);
}

.readings-table th {
  background-color: var(--color-background-soft);
  color: var(--color-heading);
  font-weight: 600;
  text-transform: uppercase;
  font-size: 0.9rem;
}

.readings-table tbody tr:last-child td {
  border-bottom: none;
}

.readings-table tbody tr:hover {
  background-color: var(--color-background-soft);
}

.anomaly-row {
  background-color: rgba(220, 53, 69, 0.08); /* Light red background for anomaly rows */
  font-weight: 500;
}

.is-anomaly-indicator {
  color: var(--color-accent-red);
  font-weight: bold;
}

.select-sensor-prompt {
  text-align: center;
  margin-top: 40px;
  font-size: 1.1rem;
  color: var(--color-text);
}
</style>