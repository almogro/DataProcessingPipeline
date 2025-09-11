package com.functionalpipeline.io

import com.functionalpipeline.models.{ProcessedMovieRecord, GenreStatistics}
import com.functionalpipeline.models.{ProcessedMovieRecord, GenreStatistics}
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
    
    // Flatten the structure for CSV output with proper formatting
    val flattenedData = data.map { record =>
      (
        record.movie.id,
        record.movie.title,
        record.movie.year,
        record.movie.genre,
        formatTo3Decimals(record.movie.rating),
        formatNumberWithCommas(record.movie.votes),
        record.decade,
        record.ratingCategory,
        formatTo3Decimals(record.popularityScore)
      )
    }.toDF("id", "title", "year", "genre", "rating", "votes", "decade", "ratingCategory", "popularityScore")
    
    flattenedData.write
      .mode("overwrite")
      .option("header", "true")
      .csv(outputPath)
  }
  
  
  /**
   * Saves genre statistics data to a file.
   * 
   * @param data Dataset of genre statistics
   * @param outputPath Path where to save the data
   */
  def saveGenreStatistics(data: Dataset[GenreStatistics], outputPath: String): Unit = {
    import data.sparkSession.implicits._
    
    // Format numbers with comma separators for better readability
    val formattedData = data.map { stats =>
      (
        stats.genre,
        stats.count,
        stats.averageRating,
        formatNumberWithCommas(stats.totalVotes),
        stats.topMovie
      )
    }.toDF("genre", "count", "averageRating", "totalVotes", "topMovie")
    
    formattedData.write
      .mode("overwrite")
      .option("header", "true")
      .csv(outputPath)
  }
  
  /**
   * Formats a number with comma separators for better readability.
   * 
   * @param number The number to format
   * @return Formatted string with commas
   */
  private def formatNumberWithCommas(number: Long): String = {
    java.text.NumberFormat.getNumberInstance(java.util.Locale.US).format(number)
  }
  
  /**
   * Formats a number to exactly 3 decimal places (truncates, doesn't round).
   * 
   * @param number The number to format
   * @return Formatted string with exactly 3 decimal places
   */
  private def formatTo3Decimals(number: Double): String = {
    val truncated = (number * 1000).toInt / 1000.0
    f"$truncated%.3f"
  }
  
  /**
   * Saves top movies by decade data to a file.
   * 
   * @param data Dataset of top movies grouped by decade
   * @param outputPath Path where to save the data
   */
  def saveTopMoviesByDecade(data: Dataset[(String, List[ProcessedMovieRecord])], outputPath: String): Unit = {
    import data.sparkSession.implicits._
    
    // Flatten the data for CSV output with proper formatting
    val flattenedData = data.flatMap { case (decade, movies) =>
      movies.map { movie =>
        (
          decade, 
          movie.movie.title, 
          formatTo3Decimals(movie.movie.rating), 
          formatTo3Decimals(movie.popularityScore), 
          movie.movie.genre
        )
      }
    }.toDF("decade", "title", "rating", "popularityScore", "genre")
    
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
