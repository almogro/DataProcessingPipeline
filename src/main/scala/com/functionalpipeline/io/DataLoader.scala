package com.functionalpipeline.io

import org.apache.spark.sql.{Dataset, SparkSession}

/**
 * Pure functions for loading data from external sources.
 * 
 * This object demonstrates separation of I/O operations from pure logic
 * in functional programming.
 */
object DataLoader {
  
  /**
   * Loads data from a file path.
   * 
   * This function handles the I/O operation of loading data,
   * keeping it separate from the pure data processing logic.
   * 
   * @param spark SparkSession instance
   * @param path Path to the data file
   * @return Dataset of raw data strings
   */
  def loadData(spark: SparkSession, path: String): Dataset[String] = {
    spark.read
      .textFile(path)
      .filter(_.nonEmpty) // Remove empty lines
  }
  
  /**
   * Loads data from multiple file paths.
   * 
   * @param spark SparkSession instance
   * @param paths List of file paths
   * @return Dataset of raw data strings from all files
   */
  def loadDataFromMultiplePaths(spark: SparkSession, paths: List[String]): Dataset[String] = {
    val datasets = paths.map(path => loadData(spark, path))
    datasets.reduce(_.union(_))
  }
  
  /**
   * Loads data with error handling.
   * 
   * This demonstrates functional error handling without exceptions.
   * 
   * @param spark SparkSession instance
   * @param path Path to the data file
   * @return Either a Dataset[String] or an error message
   */
  def loadDataSafely(spark: SparkSession, path: String): Either[String, Dataset[String]] = {
    try {
      Right(loadData(spark, path))
    } catch {
      case e: Exception => Left(s"Error loading data from $path: ${e.getMessage}")
    }
  }
}
