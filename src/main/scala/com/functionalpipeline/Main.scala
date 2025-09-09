package com.functionalpipeline

import com.functionalpipeline.pipeline.DataPipeline
import com.functionalpipeline.io.DataLoader
import com.functionalpipeline.io.DataSaver
import org.apache.spark.sql.SparkSession

/**
 * Main entry point for the Functional Data Processing Pipeline.
 * 
 * This application demonstrates functional programming principles
 * applied to Apache Spark data processing.
 */
object Main {
  
  def main(args: Array[String]): Unit = {
    // Create Spark session
    val spark = SparkSession.builder()
      .appName("Functional Data Processing Pipeline")
      .master("local[*]")
      .getOrCreate()
    
    try {
      // Initialize the data pipeline
      val pipeline = new DataPipeline(spark)
      
      // Load data from external source
      val inputPath = args.headOption.getOrElse("data/input")
      val outputPath = args.lift(1).getOrElse("data/output")
      
      val rawData = DataLoader.loadData(spark, inputPath)
      
      // Process data using functional pipeline
      val processedData = pipeline.processData(rawData)
      
      // Save results
      DataSaver.saveData(processedData, outputPath)
      
      println("Data processing pipeline completed successfully!")
      
    } finally {
      spark.stop()
    }
  }
}
