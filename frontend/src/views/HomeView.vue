<script setup>
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();

const scrollToSection = (id) => {
  const element = document.getElementById(id);
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' });
  }
};
</script>

<template>
  <div class="project-showcase-page">
    <header class="hero-section">
      <h1 class="hero-title">Sentinella: AI-Powered IoT Anomaly Detection</h1>
      <p class="hero-subtitle">Real-time environmental monitoring with on-device machine learning for proactive insights.</p>
      <div class="hero-actions">
        <router-link v-if="authStore.isLoggedIn" to="/dashboard" class="btn btn-primary large-button">Go to Dashboard</router-link>
        <button v-else @click="scrollToSection('demo-access-section')" class="button-demo">View Live Demo</button>      </div>
    </header>

    <section class="section-container">
      <h2 class="section-title">What is Sentinella?</h2>
      <p>
        Sentinella is a cutting-edge <strong class="highlight-keyword">AI-powered Internet of Things (IoT) system</strong> designed for proactive environmental monitoring and anomaly detection. At its core, an <strong class="highlight-keyword">ESP32 microcontroller with an integrated BME680 sensor</strong> continuously collects crucial environmental data: temperature, humidity, pressure, and gas resistance.
      </p>
      <p>
        The device is engineered for <strong class="highlight-keyword">autonomous operation and environmental adaptability</strong>. Upon initial deployment or reboot, Sentinella enters a <strong class="highlight-keyword">24-hour calibration phase</strong>, during which it meticulously collects sensor data. This data is then used to calibrate a <strong class="highlight-keyword">pre-trained TensorFlow Lite Micro (TFLM) autoencoder model</strong>, allowing the system to learn and adapt to the unique "normal" operating conditions of its specific environment.
      </p>
      <p>
        After calibration, Sentinella transitions into <strong class="highlight-keyword">monitor mode</strong>. In this phase, the TFLM model calculates a <strong class="highlight-keyword">reconstruction error</strong> for each new sensor reading. This error quantifies the deviation of current conditions from the learned normal patterns. If the reconstruction error surpasses a dynamically determined anomaly threshold, the system promptly issues an <strong class="highlight-keyword">anomaly notification</strong>. This proactive alerting mechanism enables users to identify and address unusual environmental shifts or potential equipment malfunctions before they escalate, facilitating preventative action and mitigating risks.
      </p>
    </section>

    <section class="section-container tech-stack-section">
      <h2 class="section-title">Technology Stack</h2>
      <div class="tech-grid">
        <div class="tech-item"><strong>Frontend:</strong> Vue.js, Pinia, Vue Router, Axios</div>
        <div class="tech-item"><strong>Backend:</strong> Spring Boot, Spring Integration (MQTT), Java 17+, JPA, PostgreSQL, JWT</div>
        <div class="tech-item"><strong>Embedded:</strong> ESP32 (NodeMCU/ESP-IDF), BME680 Sensor, PlatformIO</div>
        <div class="tech-item"><strong>Machine Learning:</strong> PyTorch (for training), TensorFlow Lite Micro (for deployment), Keras/Python (Autoencoder model)</div> <!-- More precise ML description -->
        <div class="tech-item"><strong>Messaging:</strong> MQTT (Mosquitto on Raspberry Pi)</div>
        <div class="tech-item"><strong>Deployment:</strong> Vercel (Frontend), Raspberry Pi (Backend/MQTT)</div>
      </div>
    </section>

    <section class="section-container">
      <h2 class="section-title">Project Architecture</h2>
      <p>A simplified overview of how Sentinella works:</p>
      <img src="@/assets/diagram.png" alt="Sentinella Architecture Diagram" class="architecture-diagram"/>
      
    </section>

    <section class="section-container hardware-design-section">
      <h2 class="section-title">Hardware Design & Enclosure</h2>
      <p>
        Beyond the software architecture, Sentinella features a custom-designed 3D-printed enclosure,
        crafted to integrate all electronic components and optimize sensor performance.
        This design facilitates easy assembly, protects the internal circuitry, and ensures efficient
        airflow for accurate environmental readings.
      </p>
      <img src="@/assets/design.png" alt="Sentinella 3D Hardware Design" class="hardware-design-diagram"/>
      <div class="hardware-details">
        <p>
          The enclosure includes designated sections for the <strong class="highlight-keyword">ESP32 microcontroller</strong>, the <strong class="highlight-keyword">LiPo battery</strong>, and crucial external interfaces. Key features like the <strong class="highlight-keyword">USB-C port</strong> provide convenient charging, while the <strong class="highlight-keyword">on/off switch with integrated LED</strong> offers intuitive control and status indication. Three distinct <strong class="highlight-keyword">airflow sections</strong> are engineered to guide air efficiently over the <strong class="highlight-keyword">BME680 sensor</strong>, ensuring precise and responsive environmental data collection.
        </p>

      </div>
    </section>

    <section class="section-container">
      <h2 class="section-title">Key Features</h2>
      <ul class="feature-list">
        <li><strong class="highlight-feature">On-Device AI:</strong> Utilizes a trained autoencoder model on ESP32 to detect anomalies at the source.</li>
        <li><strong class="highlight-feature">Real-time Monitoring:</strong> Collects temperature, humidity, pressure, and gas resistance data.</li>
        <li><strong class="highlight-feature">Secure Authentication:</strong> JWT-based user authentication for protected access.</li>
        <li><strong class="highlight-feature">Dynamic Sensor Management:</strong> Users can claim and manage their IoT devices through a web interface.</li>
        <li><strong class="highlight-feature">Historical Data Visualization:</strong> View sensor trends and anomaly history.</li>
      </ul>
    </section>

    <section class="section-container challenges-section">
      <h2 class="section-title">Challenges & Solutions</h2>
      <ul class="challenge-list">
        <li><strong class="highlight-challenge">Resource Constraints on ESP32:</strong> Optimized TFLM model size and inference efficiency for deployment on the ESP32 microcontroller.</li>
        <li><strong class="highlight-challenge">Robust MQTT Integration:</strong> Implemented persistent connections, QoS settings, and reliable message processing across the distributed system.</li>
        <li><strong class="highlight-challenge">Secure User/Device Pairing:</strong> Designed and implemented a sensor claiming API with unique device IDs, ensuring data ownership and system security.</li>
        <li><strong class="highlight-challenge">Development Environment Consistency:</strong> Navigated complex configuration challenges including environment variables, build tool annotation processors (Lombok), and cross-origin resource sharing (CORS) across diverse tech stacks.</li>
      </ul>
    </section>
    <section class="section-container future-features-section">
      <h2 class="section-title">Desired Features for v2</h2>
      <p class="section-description">
        Sentinella is a constantly evolving project. Here are some key enhancements planned for future iterations:
      </p>
      <ul class="future-features-list">
        <li>
          <strong class="highlight-feature">Custom PCB Design:</strong> Move from breadboard/off-the-shelf components to a custom-designed PCB board. This will enable miniaturization, integrated power management, and improved durability for a more production-ready device.
        </li>
        <li>
          <strong class="highlight-feature">Enhanced AI Adaptability:</strong> Implement strategies for continuous model improvement, including integrating a wider range of diverse training data from various environments and exploring incremental learning on the edge or regular cloud-based retraining for increased anomaly detection accuracy and reduced false positives.
        </li>
        <li>
          <strong class="highlight-feature">Multi-Channel Notifications:</strong> Expand proactive anomaly alerting beyond Telegram to include email, SMS, and customizable webhooks, allowing users to choose their preferred communication channels and ensuring critical alerts are never missed.
        </li>

        <li>
          <strong class="highlight-feature">Advanced Analytics & Reporting:</strong> Develop more sophisticated dashboard elements, including historical trend comparisons, anomaly correlation analysis, and automated periodic reports to provide deeper insights into environmental patterns.
        </li>
      </ul>
    </section>
    <footer  id="demo-access-section" class="demo-footer">
      <p class="demo-info">Check Sentinella in Action</p>

      <div class="network-status-note">
        <strong style="color: var(--color-accent-orange);">Note:</strong> The live demo's backend is hosted on a Raspberry Pi within a local network and is currently undergoing router configuration for public access. The dashboard functionality (login, live data) may be temporarily unavailable or show network errors. We appreciate your patience!
      </div>

      <p class="demo-info-message">

        Please check this quick demo to see how the dashboard would look like. Anomaly detecting demo coming soon!
        </p>
        <div class="video-wrapper">
          <iframe
            width="560"
            height="315"
            src="https://www.youtube.com/embed/7JPhpkSq8h0"
            title="Sentinella Anomaly Detection Demo"
            frameborder="0"
            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
            referrerpolicy="strict-origin-when-cross-origin"
            allowfullscreen>
          </iframe>
        </div>
      
      
     
      <div class="test-credentials-block">
        <p class="test-credentials-heading">Use these test credentials:</p>
        <div class="credential-item">
          <span class="credential-label">Username:</span>
          <span class="credential-value">`admin`</span>
        </div>
        <div class="credential-item">
          <span class="credential-label">Password:</span>
          <span class="credential-value">`test`</span>
        </div>
      </div>

      <div class="demo-button-wrapper">
          <router-link v-if="authStore.isLoggedIn" to="/dashboard" class="btn btn-primary large-button">Go to Dashboard</router-link>
          <router-link v-else to="/login" class="btn btn-primary large-button">Launch Live Demo</router-link>
      </div>
 
      <p class="github-link">
        Links:
        <a href="https://github.com/pasquale-sergi/Sentinella" target="_blank">GitHub Repo</a> 

      </p>
    </footer>
  </div>
</template>

<style scoped>

.network-status-note {
  background-color: rgba(255, 193, 7, 0.1); /* Light orange background */
  border: 1px solid var(--color-accent-orange);
  padding: 15px 20px;
  border-radius: 8px;
  margin: 30px auto;
  max-width: 700px;
  text-align: center;
  font-size: 0.95rem;
  color: var(--color-text);
  line-height: 1.5;
}

.section-subtitle {
  font-size: 1.8rem; /* Smaller than main title, larger than normal text */
  color: var(--color-heading);
  margin-top: 30px; /* Space above subtitle */
  margin-bottom: 20px;
  text-align: center;
}

/* Ensure existing video section styles are also applied to this new video container */
.video-demo-section {
  /* Inherits from .section-container */
  text-align: center;
  background-color: var(--color-background-soft);
  margin-top: 30px; /* Adjust spacing from previous content */
}

.button-demo {
  background-color: #e1ecf4;
  border-radius: 20px;
  border: 1px solid #7aa7c7;
  box-shadow: rgba(255, 255, 255, .7) 0 1px 0 0 inset;
  box-sizing: border-box;
  color: #39739d;
  cursor: pointer;
  display: inline-block;
  font-family: -apple-system,system-ui,"Segoe UI","Liberation Sans",sans-serif;
  font-size: 13px;
  font-weight: 400;
  line-height: 1.15385;
  margin: 0;
  outline: none;
  padding: 8px .8em;
  position: relative;
  text-align: center;
  text-decoration: none;
  user-select: none;
  -webkit-user-select: none;
  touch-action: manipulation;
  vertical-align: baseline;
  white-space: nowrap;
}

.button-demo:hover,
.button-demo:focus {
  background-color: #b3d3ea;
  color: #2c5777;
}

.button-demo:focus {
  box-shadow: 0 0 0 4px rgba(0, 149, 255, .15);
}

.button-demo:active {
  background-color: #a0c7e4;
  box-shadow: none;
  color: #2c5777;
}

.future-features-section {
  border-left: 5px solid var(--color-accent-blue); 
}

.future-features-list {
  list-style: none; /* Remove default list style */
  padding-left: 0;
}

.future-features-list li {
  background-color: var(--color-background-soft);
  padding: 18px 25px;
  border-radius: 10px;
  margin-bottom: 15px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.future-features-list li:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}
.project-showcase-page {
  padding: 40px 20px;
  max-width: 1000px;
  margin: 0 auto;
  font-family: 'Inter', sans-serif;
  color: var(--color-text);
  line-height: 1.7;
}

.hero-section {
  text-align: center;
  padding: 80px 0;
  background: linear-gradient(135deg, var(--color-background-soft) 0%, var(--color-background) 100%);
  border-radius: 15px;
  margin-bottom: 50px;
  box-shadow: 0 8px 25px rgba(0,0,0,0.1);
}

.hero-title {
  font-size: 3.5rem;
  color: var(--color-heading);
  margin-bottom: 20px;
  font-weight: 800;
  line-height: 1.2;
}

.hero-subtitle {
  font-size: 1.4rem;
  color: var(--color-accent-blue);
  max-width: 700px;
  margin: 0 auto 40px auto;
}

.hero-actions .large-button {
  padding: 18px 40px;
  font-size: 1.3rem;
  border-radius: 8px;
}

.section-container {
  background-color: #ffffff;
  border: 1px solid var(--color-border);
  border-radius: 12px;
  padding: 35px;
  margin-bottom: 40px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.08);
}

.section-title {
  font-size: 2.2rem;
  color: var(--color-heading);
  margin-bottom: 25px;
  border-bottom: 2px solid var(--color-accent-blue);
  padding-bottom: 10px;
  display: inline-block;
}

p {
  margin-bottom: 15px;
  font-size: 1.05rem;
}

ul {
  list-style: disc;
  padding-left: 25px;
  margin-bottom: 15px;
}

ul li {
  margin-bottom: 10px;
  font-size: 1.05rem;
}

strong {
  color: var(--color-heading);
}

.architecture-diagram {
  max-width: 100%;
  height: auto;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  margin-top: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
}

.tech-stack-section .tech-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.tech-item {
  background-color: var(--color-background-soft);
  padding: 15px;
  border-left: 5px solid var(--color-accent-blue);
  border-radius: 8px;
  font-size: 1rem;
}



.demo-footer {
  text-align: center;
  margin-top: 50px;
  padding: 40px 0;
  background-color: var(--color-background-soft);
  border-radius: 15px;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.05);
}


.demo-info {
  font-size: 1.5rem;
  color: var(--color-heading);
  margin-bottom: 20px;
}

.demo-footer .large-button {
  margin-bottom: 20px;
}

.test-credentials-block {
  background-color: var(--color-background-mute);
  padding: 20px 30px;
  border-radius: 8px;
  display: inline-block; 
  margin: 30px auto; 
  text-align: left; 
  line-height: 1.6;
  font-size: 0.95rem;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.test-credentials-heading {
  font-weight: bold;
  color: var(--color-heading);
  margin-bottom: 15px;
  font-size: 1.1rem;
  text-align: center;
}

.credential-item {
  display: flex; 
  justify-content: space-between; 
  margin-bottom: 8px;
  gap: 20px; 
}

.credential-label {
  font-weight: 500;
  color: var(--color-text);
  min-width: 80px; 
}

.credential-value {
  font-family: 'Courier New', Courier, monospace;
  background-color: var(--color-background-soft);
  padding: 3px 8px;
  border-radius: 4px;
  color: var(--color-accent-blue);
}


.demo-footer .large-button {
  margin-bottom: 20px; 
}

.github-link {
  margin-top: 20px; 
}

.github-link a {
  color: var(--color-accent-blue);
  text-decoration: none;
  font-weight: 500;
  margin: 0 15px;
}

.github-link a:hover {
  text-decoration: underline;
}

.highlight-keyword {
  color: var(--color-black); 
  font-weight: 600; 
}


.feature-list li strong.highlight-feature,
.challenge-list li strong.highlight-challenge {
  color: var(--color-black); 
  font-weight: 700; 
  margin-right: 5px; 
}

.tech-item strong {
  color: var(--color-heading);
  font-weight: 600;
}


.feature-list, .challenge-list {
  padding-left: 20px;
}
.feature-list li, .challenge-list li {
  margin-bottom: 12px;
  line-height: 1.5;
  font-size: 1.05rem;
}
.feature-list li::marker, .challenge-list li::marker {
  color: var(--color-accent-blue); 
  font-weight: bold;
}


.github-link {
    text-align: center;
    margin-top: 30px; 
    font-size: 0.95rem;
}
.github-link a {
    color: var(--color-accent-blue);
    text-decoration: none;
    font-weight: 500;
    margin: 0 10px;
}
.github-link a:hover {
    text-decoration: underline;
}
.hardware-design-section {
  text-align: center;
  background-color: var(--color-background-soft); /* Slightly different background */
  border-left: 5px solid var(--color-accent-blue); /* A nice accent border */
}

.hardware-design-diagram {
  max-width: 90%; /* Adjust size as needed */
  height: auto;
  margin: 30px auto;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.hardware-details {
  text-align: left; /* Align text within this block */
  max-width: 800px;
  margin: 0 auto;
  padding-top: 20px;
}

.hardware-details p {
  margin-bottom: 15px;
}

.call-to-action-note {
  font-style: italic;
  color: #555;
  margin-top: 25px;
}

/* Ensure consistent image styling */
.architecture-diagram {
  margin: 30px auto; /* Add margin for spacing */
}




@media (max-width: 768px) {

  .hero-title { font-size: 2.5rem; }
  .hero-subtitle { font-size: 1.2rem; }
  .hero-actions .large-button { font-size: 1.1rem; padding: 15px 30px; }
  .section-title { font-size: 1.8rem; }
  .tech-stack-section .tech-grid { grid-template-columns: 1fr; }
  .section-container { padding: 20px; }
  p, ul li { font-size: 0.95rem; }
  .hardware-design-diagram {
    max-width: 100%;
  }
}

.demo-button-wrapper {
  margin-top: 30px; 
  margin-bottom: 20px; 
  text-align: center; 
}
</style>