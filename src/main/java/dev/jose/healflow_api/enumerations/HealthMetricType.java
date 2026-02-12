package dev.jose.healflow_api.enumerations;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HealthMetricType {
  // Cardiovascular metrics
  BLOOD_PRESSURE_SYSTOLIC("Blood Pressure (Systolic)", "mmHg", "cardiovascular"),
  BLOOD_PRESSURE_DIASTOLIC("Blood Pressure (Diastolic)", "mmHg", "cardiovascular"),
  HEART_RATE("Heart Rate", "bpm", "cardiovascular"),
  OXYGEN_SATURATION("Oxygen Saturation", "%", "cardiovascular"),

  // Metabolic metrics
  WEIGHT("Weight", "kg", "metabolic"),
  HEIGHT("Height", "cm", "metabolic"),
  BMI("Body Mass Index", "kg/m²", "metabolic"),
  BLOOD_GLUCOSE("Blood Glucose", "mg/dL", "metabolic"),
  HBA1C("HbA1c", "%", "metabolic"),
  CHOLESTEROL_TOTAL("Total Cholesterol", "mg/dL", "metabolic"),
  CHOLESTEROL_LDL("LDL Cholesterol", "mg/dL", "metabolic"),
  CHOLESTEROL_HDL("HDL Cholesterol", "mg/dL", "metabolic"),
  TRIGLYCERIDES("Triglycerides", "mg/dL", "metabolic"),

  // General vital signs
  BODY_TEMPERATURE("Body Temperature", "°C", "vital"),
  RESPIRATORY_RATE("Respiratory Rate", "breaths/min", "vital"),

  // Lifestyle metrics
  SLEEP_HOURS("Sleep Duration", "hours", "lifestyle"),
  EXERCISE_MINUTES("Exercise Duration", "minutes", "lifestyle"),
  WATER_INTAKE("Water Intake", "liters", "lifestyle"),
  STEPS("Steps", "steps", "lifestyle");

  private final String displayName;
  private final String defaultUnit;
  private final String category;
}
