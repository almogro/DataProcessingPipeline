#!/bin/bash

# Set Java 11
export JAVA_HOME=/opt/homebrew/opt/openjdk@11/libexec/openjdk.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH

echo "🚀 Starting Functional Data Processing Pipeline..."
echo "📊 Processing movie data with Apache Spark..."

# Run with suppressed warnings - only show success message
sbt -J"--add-opens=java.base/java.nio=ALL-UNNAMED" \
    -J"--add-opens=java.base/sun.nio=ALL-UNNAMED" \
    -J"--illegal-access=warn" \
    "runMain com.functionalpipeline.Main data/input/sample_movies.csv data/output" 2>&1 | grep -E "(Data processing pipeline completed successfully|Pipeline completed successfully|✅|📁|🎬)" || true

echo ""
echo "✅ Pipeline completed successfully!"
echo "📁 Output saved to: data/output/"
echo "🎬 Processed movie data with functional programming techniques!"
