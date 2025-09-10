<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const unclaimedSensors = ref([]); // Stores sensors where user_id is null
const errorMessage = ref('');
const successMessage = ref('');
const isLoading = ref(true);

// Refs for the form fields when claiming an *unseen* device
const deviceIdToClaim = ref('');
const newSensorName = ref('');
const newSensorLocation = ref('');

onMounted(() => {
  fetchUnclaimedSensors();
});

const fetchUnclaimedSensors = async () => {
  isLoading.value = true;
  errorMessage.value = '';
  successMessage.value = ''; 
  try {
    const response = await axios.get('/sensors/unclaimed');
    unclaimedSensors.value = response.data;
  } catch (error) {
    errorMessage.value = error.response?.data?.message || 'Failed to load unclaimed sensors.';
    console.error('Fetch unclaimed sensors error:', error);
  } finally {
    isLoading.value = false;
  }
};

const claimSensor = async (existingDeviceId = null) => {
  try {
    errorMessage.value = '';
    successMessage.value = '';

    const payloadDeviceId = existingDeviceId || deviceIdToClaim.value;

    if (!payloadDeviceId) {
      errorMessage.value = 'Device ID cannot be empty.';
      return;
    }

    const response = await axios.post('/sensors/claim', {
      deviceId: payloadDeviceId,
      name: newSensorName.value || `My ${payloadDeviceId}`,
      location: newSensorLocation.value || 'Default Location'
    });
    
    successMessage.value = `Sensor "${response.data.name}" (${response.data.deviceId}) claimed successfully!`;
    
    // Clear form fields
    deviceIdToClaim.value = '';
    newSensorName.value = '';
    newSensorLocation.value = '';
    
    await fetchUnclaimedSensors(); // Refresh the list of unclaimed sensors
  } catch (error) {
    errorMessage.value = error.response?.data?.message || 'Failed to claim sensor.';
    console.error('Claim sensor error:', error);
  }
};
</script>

<template>
  <div class="claim-sensors-page">
    <h1 class="page-title">Claim New Sensors</h1>
    <p v-if="isLoading" class="loading-message">Loading available sensors...</p>
    <p v-else-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    <p v-else-if="successMessage" class="success-message">{{ successMessage }}</p>

    <div class="form-section">
      <h2>Proactively Claim a Device</h2>
      <p class="section-description">Enter a Device ID to claim it, even if it hasn't sent data yet.</p>
      <form @submit.prevent="claimSensor(null)" class="claim-form">
        <div class="form-group">
          <label for="deviceIdToClaim">Device ID:</label>
          <input type="text" id="deviceIdToClaim" v-model="deviceIdToClaim" placeholder="e.g., Sentinella_ESP32_01" required class="form-input" />
        </div>
        <div class="form-group">
          <label for="newNameManual">Name (optional):</label>
          <input type="text" id="newNameManual" v-model="newSensorName" placeholder="e.g., Living Room Sensor" class="form-input" />
        </div>
        <div class="form-group">
          <label for="newLocationManual">Location (optional):</label>
          <input type="text" id="newLocationManual" v-model="newSensorLocation" placeholder="e.g., Main Shelf" class="form-input" />
        </div>
        <button type="submit" class="btn btn-primary claim-button">Claim New Device</button>
      </form>
    </div>

    <div class="list-section">
      <h2>Unclaimed Devices Discovered by MQTT:</h2>
      <p v-if="unclaimedSensors.length === 0" class="no-unclaimed-message">No unclaimed sensors found currently. Make sure your devices are sending data!</p>
      <div v-else class="unclaimed-cards-grid">
        <div v-for="sensor in unclaimedSensors" :key="sensor.id" class="unclaimed-sensor-card">
          <h3>Device ID: {{ sensor.deviceId }}</h3>
          <p>Current Name: {{ sensor.name }}</p>
          <p>Current Location: {{ sensor.location }}</p>
          <p class="timestamp">First seen: {{ new Date(sensor.readings[0]?.timestamp || Date.now()).toLocaleString() }}</p>
          <button @click="claimSensor(sensor.deviceId)" class="btn btn-secondary small-button">Claim This Device</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.claim-sensors-page {
  padding: 20px;
}

.page-title {
  color: var(--color-heading);
  font-size: 2.5rem;
  margin-bottom: 30px;
  text-align: center;
}

.loading-message, .error-message, .success-message, .no-unclaimed-message {
  text-align: center;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 25px;
  font-size: 1.1rem;
}

.loading-message {
  background-color: var(--color-background-soft);
  color: var(--color-text);
}

.error-message {
  background-color: rgba(220, 53, 69, 0.1);
  color: var(--color-accent-red);
  border: 1px solid var(--color-accent-red);
}

.success-message {
  background-color: rgba(40, 167, 69, 0.1);
  color: var(--color-accent-green);
  border: 1px solid var(--color-accent-green);
}

.form-section, .list-section {
  background-color: #ffffff;
  border: 1px solid var(--color-border);
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  margin-bottom: 30px;
}

.section-description {
  color: var(--color-text);
  margin-bottom: 25px;
}

.claim-form .form-group {
  margin-bottom: 15px;
}

.claim-form label {
  display: block;
  margin-bottom: 8px;
  color: var(--color-heading);
  font-weight: 600;
}

.claim-form .form-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  font-size: 1rem;
}

.claim-form .claim-button {
  width: 100%;
  padding: 12px;
  font-size: 1.1rem;
  margin-top: 20px;
}

.unclaimed-cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 25px;
  margin-top: 25px;
}

.unclaimed-sensor-card {
  background-color: var(--color-background-soft);
  border: 1px dashed var(--color-accent-orange);
  border-left: 5px solid var(--color-accent-orange); /* Stronger highlight */
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(255,193,7,0.15);
}

.unclaimed-sensor-card h3 {
  font-size: 1.4rem;
  color: var(--color-heading);
  margin-bottom: 10px;
}

.unclaimed-sensor-card p {
  font-size: 0.95rem;
  color: var(--color-text);
  margin-bottom: 5px;
}

.unclaimed-sensor-card .timestamp {
  font-size: 0.8rem;
  color: #888;
  margin-top: 10px;
  text-align: right;
}

.unclaimed-sensor-card .small-button {
  margin-top: 15px;
  padding: 0.5rem 1rem;
  font-size: 0.9rem;
}
</style>