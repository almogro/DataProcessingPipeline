package com.functionalpipeline.io

import com.functionalpipeline.models.ProcessedMovieRecord
import org.apache.spark.sql.Dataset

/**
 * Pure functions for saving data to external destinations.
 * 
 * This object demonstrates separation of I/O operations from pure logic
 * in functional programming.
 */
object DataSaver {
  
  /**
   * Saves processed data to a file.
   * 
   * This function handles the I/O operation of saving data,
   * keeping it separate from the pure data processing logic.
   * 
   * @param data Dataset of processed movie records
   * @param outputPath Path where to save the data
   */
  def saveData(data: Dataset[ProcessedMovieRecord], outputPath: String): Unit = {
    import data.sparkSession.implicits._
    
    // Flatten the structure for CSV output
    val flattenedData = data.map { record =>
      (
        record.movie.id,
        record.movie.title,
        record.movie.year,
        record.movie.genre,
        record.movie.rating,
        record.movie.votes,
        record.decade,
        record.ratingCategory,
        record.popularityScore
      )
    }.toDF("id", "title", "year", "genre", "rating", "votes", "decade", "ratingCategory", "popularityScore")
    
    flattenedData.write
      .mode("overwrite")
      .option("header", "true")
      .csv(outputPath)
  }
  
  /**
   * Saves data in JSON format.
   * 
   * @param data Dataset of processed movie records
   * @param outputPath Path where to save the JSON data
   */
  def saveAsJson(data: Dataset[ProcessedMovieRecord], outputPath: String): Unit = {
    data.write
      .mode("overwrite")
      .json(outputPath)
  }
  
  /**
   * Saves data with error handling.
   * 
   * This demonstrates functional error handling without exceptions.
   * 
   * @param data Dataset of processed movie records
   * @param outputPath Path where to save the data
   * @return Either Unit (success) or an error message
   */
  def saveDataSafely(data: Dataset[ProcessedMovieRecord], outputPath: String): Either[String, Unit] = {
    try {
      saveData(data, outputPath)
      Right(())
    } catch {
      case e: Exception => Left(s"Error saving data to $outputPath: ${e.getMessage}")
    }
  }
  
  /**
   * Saves data to multiple formats.
   * 
   * @param data Dataset of processed movie records
   * @param csvPath Path for CSV output
   * @param jsonPath Path for JSON output
   */
  def saveInMultipleFormats(data: Dataset[ProcessedMovieRecord], csvPath: String, jsonPath: String): Unit = {
    saveData(data, csvPath)
    saveAsJson(data, jsonPath)
  }
}
