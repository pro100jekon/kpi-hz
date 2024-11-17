#!/bin/bash

# Define parameter sets
params=(
  "w1"
  "majority"
  "w1"
  "majority"
  "w3"
)

# Iterate through the parameter sets
for i in "${!params[@]}"; do
  echo "Running iteration $((i + 1)) with parameters: ${params[$i]}"
  java -jar app.jar "${params[$i]}"

  # Check exit code to ensure the application ran successfully
  if [ $? -ne 0 ]; then
    echo "Error: Application failed with parameters: ${params[$i]}"
    exit 1
  fi
  echo "Time to check database for the value!"
  sleep 30
done

echo "All iterations completed. Exiting."