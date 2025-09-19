package com.functionalpipeline.io

import com.functionalpipeline.models.{ProcessedMovieRecord, GenreStatistics}
import com.functionalpipeline.models.{ProcessedMovieRecord, GenreStatistics}
import org.apache.spark.sql.Dataset

/** I/O operations for saving data to external destinations. */
object DataSaver {
  
  /** Saves processed movie data to CSV file. */
  def saveData(data: Dataset[ProcessedMovieRecord], outputPath: String): Unit = {
    import data.sparkSession.implicits._
    
    // Flatten the structure for CSV output with proper formatting
    val flattenedData = data.map { record =>
      (
        record.movie.id,
        record.movie.title,
        record.movie.year,
        record.movie.genre,
        formatTo1Decimal(record.movie.rating),
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
  
  
  /** Saves genre statistics to CSV file. */
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
  
  /** Formats numbers with comma separators. */
  private def formatNumberWithCommas(number: Long): String = {
    java.text.NumberFormat.getNumberInstance(java.util.Locale.US).format(number)
  }
  
  /** Truncates numbers to exactly 3 decimal places. */
  private def formatTo3Decimals(number: Double): String = {
    val truncated = (number * 1000).toInt / 1000.0
    f"$truncated%.3f"
  }
  
  /** Formats numbers to exactly 1 decimal place. */
  private def formatTo1Decimal(number: Double): String = {
    val truncated = (number * 10).toInt / 10.0
    f"$truncated%.1f"
  }
  
  /** Saves top movies by decade to CSV file. */
  def saveTopMoviesByDecade(data: Dataset[(String, List[ProcessedMovieRecord])], outputPath: String): Unit = {
    import data.sparkSession.implicits._
    
    // Flatten the data for CSV output with proper formatting
    val flattenedData = data.flatMap { case (decade, movies) =>
      movies.map { movie =>
        (
          decade, 
          movie.movie.title, 
          formatTo1Decimal(movie.movie.rating), 
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
