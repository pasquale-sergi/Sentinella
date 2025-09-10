#include "tflm_inference.h"
#include "Arduino.h" 
#include "sentinella_autoencoder_float.h"
#include "global_state.h"

tflite::MicroErrorReporter micro_error_reporter; 
tflite::AllOpsResolver resolver; 
const tflite::Model* model = nullptr;
tflite::MicroInterpreter* interpreter = nullptr; //engine, takes the model, operations and it execute

#define K_TENSOR_ARENA_SIZE 32 * 1024
uint8_t tensor_arena[K_TENSOR_ARENA_SIZE]; //block of RAM that the microcontroller will use

//pointer for input/outputs of the model
TfLiteTensor* input = nullptr;
TfLiteTensor* output = nullptr;


void initTFLM() {
  tflite::InitializeTarget();
 

  model = tflite::GetModel(sentinella_autoencoder_float_tflite); 
  if (model->version() != TFLITE_SCHEMA_VERSION) {
    Serial.printf("DEBUG TFLM: Model provided is schema version %d, but only version %d is supported by this library.",
                   model->version(), TFLITE_SCHEMA_VERSION);
    while (1);
  }


  interpreter = new tflite::MicroInterpreter(model, resolver, tensor_arena, K_TENSOR_ARENA_SIZE, &micro_error_reporter);

  if (interpreter->AllocateTensors() != kTfLiteOk) {
    Serial.flush();
    // In a final product, you might reboot or try a different model. For debugging, just halt for now.
    while(1); 
  }
  input = interpreter->input(0);
  output = interpreter->output(0);

}
//model works with data in small range, usually 0-1, so we normalize the sensor data
float normalizeFeature(float raw_value, int feature_index) {

  
  float current_range = savedMaxVals[feature_index] - savedMinVals[feature_index];
  Serial.printf("DEBUG NORMALIZE: Feature %d: Raw %.2f, Min %.2f, Max %.2f, Current Range %.4f\n",
               feature_index, raw_value, savedMinVals[feature_index], savedMaxVals[feature_index], current_range);
  if (fabs(current_range) < 1e-6 ) { // If min and max are the same (or very close), the range is effectively zero.
    return 0.5; // Neutral point if min == max
  }
  float normalized_value = (raw_value - savedMinVals[feature_index]) / current_range;
  return constrain(normalized_value, 0.0f, 1.0f);
}
//final step to confront output and input and see if any anomaly
float calculateReconstructionError(const float input_normalized[], const float reconstructed_normalized[]) {
  float mse = 0.0;
  Serial.print("DEBUG MSE: Input: ");
  for (int i = 0; i < 4; ++i) {
    float diff = input_normalized[i] - reconstructed_normalized[i];
    mse += diff * diff;
    Serial.printf("%.6f ", input_normalized[i]); // Print input
  }
  Serial.print(" | Output: ");
  for (int i = 0; i < 4; ++i) {
    Serial.printf("%.6f ", reconstructed_normalized[i]); // Print output
  }
  Serial.printf(" | Raw MSE: %.10f\n", mse); // Print raw MSE with high precision

  float final_mse = mse / 4.0;
  Serial.printf("DEBUG MSE: Final MSE: %.10f\n", final_mse); Serial.flush();
  return final_mse;
}


void runTFLMInference(const float raw_sensor_values[4], float& reconstruction_error) {
  float input_to_model_normalized[4]; 

  for (int i = 0; i < 4; ++i) {
    input_to_model_normalized[i] = normalizeFeature(raw_sensor_values[i], i);
    input->data.f[i] = input_to_model_normalized[i]; // Copy to TFLite input tensor
    Serial.printf("DEBUG TFLM INFERENCE: Input[%d] raw:%.2f -> normalized:%.6f\n", i, raw_sensor_values[i], input->data.f[i]); Serial.flush();
  }

  // Run TFLite Inference
  if (interpreter->Invoke() != kTfLiteOk) {
    Serial.println("Invoke failed.");
    reconstruction_error = -1.0; // Indicate error
    return;
  }

  // Calculate Reconstruction Error
  float reconstructed_normalized_output[4];
  delay(3000);
  Serial.print("DEBUG TFLM INFERENCE: Output tensor values: ");
  for (int i = 0; i < 4; ++i) {
    reconstructed_normalized_output[i] = output->data.f[i]; // Normalized output from model
    Serial.printf("%.6f ", reconstructed_normalized_output[i]);
  }
  
  Serial.println(); Serial.flush();

  reconstruction_error = calculateReconstructionError(input_to_model_normalized, reconstructed_normalized_output); // Use local input array  
  Serial.printf("DEBUG TFLM INFERENCE: Calculated Reconstruction Error: %.6f\n", reconstruction_error); Serial.flush();}

void updateErrorStatistics(float error) {
  Serial.printf("DEBUG STATS: Before update: Mean=%.6f, M2=%.6f, Count=%lu (Input Error: %.6f)\n", calibrationErrorMean, calibrationErrorM2, calibrationErrorCount, error);
  calibrationErrorCount++;
  float delta = error - calibrationErrorMean;
  calibrationErrorMean += delta / calibrationErrorCount;
  float delta2 = error - calibrationErrorMean;
  calibrationErrorM2 += delta * delta2;
  Serial.printf("DEBUG STATS: After update: Mean=%.6f, M2=%.6f, Count=%lu\n", calibrationErrorMean, calibrationErrorM2, calibrationErrorCount);
}
