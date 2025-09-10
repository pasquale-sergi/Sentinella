#ifndef TFLM_INFERENCE_H
#define TFLM_INFERENCE_H

#include <tensorflow/lite/micro/all_ops_resolver.h>
#include <tensorflow/lite/micro/micro_interpreter.h>
#include <tensorflow/lite/micro/system_setup.h>
#include <tensorflow/lite/schema/schema_generated.h>

#include "config.h"
#include <tensorflow/lite/micro/micro_error_reporter.h>

extern const unsigned char sentinella_autoencoder_float_tflite[];
extern const unsigned int sentinella_autoencoder_float_tflite_len;

extern tflite::MicroErrorReporter micro_error_reporter;
extern tflite::AllOpsResolver resolver;
extern const tflite::Model* model;
extern tflite::MicroInterpreter* interpreter;
extern uint8_t tensor_arena[];
extern TfLiteTensor* input;
extern TfLiteTensor* output;

void initTFLM();
void runTFLMInference(const float raw_sensor_values[4], float& reconstruction_error);
float normalizeFeature(float raw_value, int feature_index);
void updateErrorStatistics(float error);

#endif 