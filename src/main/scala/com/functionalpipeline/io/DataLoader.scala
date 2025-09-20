package com.functionalpipeline.io

import com.functionalpipeline.functions.Combinators._
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
   * @param path  Path to the data file
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
   * @param path  Path to the data file
   * @return Either a Dataset[String] or an error message
   */
  def loadDataSafely(spark: SparkSession, path: String): Either[String, Dataset[String]] = {
    try {
      Right(loadData(spark, path))
    } catch {
      case e: Exception => Left(s"Error loading data from $path: ${e.getMessage}")
    }
  }

  /**
   * Loads data with retry logic using retry combinator.
   *
   * @param spark      SparkSession instance
   * @param path       Path to the data file
   * @param maxRetries Maximum number of retry attempts
   * @return Either a Dataset[String] or an error message
   */
  def loadDataWithRetry(spark: SparkSession, path: String, maxRetries: Int = 3): Either[String, Dataset[String]] = {
    retry((x: Unit) => loadData(spark, path), maxRetries)(()) match {
      case Right(dataset) => Right(dataset.asInstanceOf[Dataset[String]])
      case Left(error) => Left(s"Failed to load data from $path after $maxRetries retries: ${error.getMessage}")
    }
  }
}
