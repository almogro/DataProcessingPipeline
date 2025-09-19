package com.functionalpipeline.pipeline

import com.functionalpipeline.models._
import com.functionalpipeline.functions._
import org.apache.spark.sql.{Dataset, SparkSession}

/**
 * Main data processing pipeline that demonstrates functional programming principles.
 * 
 * This class orchestrates the data processing workflow using pure functions
 * and immutable data structures.
 */
class DataPipeline(spark: SparkSession) {
  
  import spark.implicits._
  
  /**
   * Processes the input data through the functional pipeline with quality filtering.
   * 
   * @param rawData Raw input data as Dataset[String]
   * @return Processed high-quality data as Dataset[ProcessedMovieRecord]
   */
  def processData(rawData: Dataset[String]): Dataset[ProcessedMovieRecord] = {
    // Parse raw data into movie records
    val movieRecords = rawData
      .map(DataParsingFunctions.parseMovieRecord)
      .filter(_.isDefined)
      .map(_.get)
    
    // Apply quality filtering
    val filteredMovies = movieRecords
      .filter(DataFilteringFunctions.isHighQualityMovie(_))
    
    // Transform using pure functions
    val processedMovies = filteredMovies
      .map(DataTransformationFunctions.enrichMovieRecord)
      .orderBy(org.apache.spark.sql.functions.col("movie.rating").desc)
    
    processedMovies
  }
  
  /**
   * Processes all input data without quality filtering.
   * 
   * @param rawData Raw input data as Dataset[String]
   * @return Processed data for all movies as Dataset[ProcessedMovieRecord]
   */
  def processAllData(rawData: Dataset[String]): Dataset[ProcessedMovieRecord] = {
    // Parse raw data into movie records
    val movieRecords = rawData
      .map(DataParsingFunctions.parseMovieRecord)
      .filter(_.isDefined)
      .map(_.get)
    
    // Transform all movies using pure functions (no filtering)
    val processedMovies = movieRecords
      .map(DataTransformationFunctions.enrichMovieRecord)
      .orderBy(org.apache.spark.sql.functions.col("movie.rating").desc)
    
    processedMovies
  }
  
  /**
   * Computes genre statistics using functional operations.
   * 
   * @param movies Dataset of processed movie records
   * @return Dataset of genre statistics
   */
  def computeGenreStatistics(movies: Dataset[ProcessedMovieRecord]): Dataset[GenreStatistics] = {
    movies
      .groupByKey(_.movie.genre)
      .mapGroups { case (genre, movieIter) =>
        val movieList = movieIter.toList
        DataAggregationFunctions.computeGenreStats(genre, movieList)
      }
  }
  
  /**
   * Finds top movies by decade using functional composition.
   * 
   * @param movies Dataset of processed movie records
   * @return Dataset of top movies grouped by decade
   */
  def findTopMoviesByDecade(movies: Dataset[ProcessedMovieRecord]): Dataset[(String, List[ProcessedMovieRecord])] = {
    movies
      .groupByKey(_.decade)
      .mapGroups { case (decade, movieIter) =>
        val movieList = movieIter.toList
        (decade, DataAggregationFunctions.findTopMovies(movieList, 5))
      }
  }
}
