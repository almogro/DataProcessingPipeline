package com.functionalpipeline.pipeline

import com.functionalpipeline.functions._
import com.functionalpipeline.models._
import org.apache.spark.sql.{Dataset, SparkSession}

/** Provides a data processing pipeline as described.
 *
 * This class orchestrates the data processing workflow using pure functions
 * and immutable data structures. It serves as the central coordinator for
 * all data transformation operations, maintaining clear separation between
 * pure business logic and I/O operations.
 *
 * @param spark The SparkSession instance for distributed data processing
 * @author Almog Roter and Yonathan Cohen
 * @version 1.0.0
 * @since September 2025
 */
class DataPipeline(spark: SparkSession) {

  import spark.implicits._

  /** Processes the input data through the functional pipeline as described.
   *
   * This method demonstrates the core functional programming principles:
   *  - Pure functions for data transformation
   *  - Immutable data structures throughout the pipeline
   *  - Higher-order functions for data processing
   *  - Clear separation of concerns
   *
   * @param rawData Raw input data as Dataset[String] containing CSV movie records
   * @return Processed high-quality data as Dataset[ProcessedMovieRecord] sorted by rating
   * @throws IllegalArgumentException if rawData is null or empty
   */
  def processData(rawData: Dataset[String]): Dataset[ProcessedMovieRecord] = {
    /*
     * Step 1: Parse raw CSV data into structured MovieRecord objects
     * Uses functional composition with map operations and Option handling
     */
    val movieRecords = rawData
      .map(DataParsingFunctions.parseMovieRecord) // String -> Option[MovieRecord]
      .filter(_.isDefined) // Keep only successful parses
      .map(_.get) // Extract MovieRecord from Option

    /*
     * Step 2: Apply quality filtering using pure predicate functions
     * Demonstrates higher-order functions and function composition
     */
    val filteredMovies = movieRecords
      .filter(DataFilteringFunctions.isHighQualityMovie(_)) // Closure capturing movie record

    /*
     * Step 3: Transform data using pure functions and custom combinators
     * Applies enrichment, categorization, and sorting operations
     */
    val processedMovies = filteredMovies
      .map(DataTransformationFunctions.enrichMovieRecord) // Pure transformation function
      .orderBy(org.apache.spark.sql.functions.col("movie.rating").desc) // Sort by rating descending

    processedMovies
  }

  /** Processes all input data without quality filtering as described.
   *
   * This method processes the complete dataset without applying any quality filters,
   * providing a comprehensive view of all movie data. It follows the same functional
   * programming principles as processData but without filtering constraints.
   *
   * @param rawData Raw input data as Dataset[String] containing CSV movie records
   * @return Processed data for all movies as Dataset[ProcessedMovieRecord] sorted by rating
   * @throws IllegalArgumentException if rawData is null or empty
   */
  def processAllData(rawData: Dataset[String]): Dataset[ProcessedMovieRecord] = {
    /*
     * Step 1: Parse raw CSV data into structured MovieRecord objects
     * Same parsing logic as processData but without quality filtering
     */
    val movieRecords = rawData
      .map(DataParsingFunctions.parseMovieRecord) // String -> Option[MovieRecord]
      .filter(_.isDefined) // Keep only successful parses
      .map(_.get) // Extract MovieRecord from Option

    /*
     * Step 2: Transform all movies using pure functions (no filtering)
     * Applies enrichment and sorting to the complete dataset
     */
    val processedMovies = movieRecords
      .map(DataTransformationFunctions.enrichMovieRecord) // Pure transformation function
      .orderBy(org.apache.spark.sql.functions.col("movie.rating").desc) // Sort by rating descending

    processedMovies
  }

  /** Computes genre statistics using functional operations as described.
   *
   * This method demonstrates advanced functional programming techniques including:
   *  - Grouping operations with higher-order functions
   *  - Immutable data aggregation
   *  - Pure function composition for statistical calculations
   *
   * @param movies Dataset of processed movie records to analyze
   * @return Dataset of genre statistics containing count, average rating, and top movies
   * @throws IllegalArgumentException if movies is null or empty
   */
  def computeGenreStatistics(movies: Dataset[ProcessedMovieRecord]): Dataset[GenreStatistics] = {
    /*
     * Step 1: Group movies by genre using functional grouping
     * Demonstrates higher-order functions and closure usage
     */
    movies
      .groupByKey(_.movie.genre) // Group by genre field
      .mapGroups { case (genre, movieIter) =>
        /*
         * Step 2: Convert iterator to list for processing
         * This closure captures the genre parameter and processes the movie iterator
         */
        val movieList = movieIter.toList
        DataAggregationFunctions.computeGenreStats(genre, movieList) // Pure aggregation function
      }
  }

  /** Finds top movies by decade using functional composition as described.
   *
   * This method demonstrates complex functional programming patterns:
   *  - Grouping by computed decade field
   *  - Functional composition for top-N selection
   *  - Immutable data structures for result aggregation
   *
   * @param movies Dataset of processed movie records to analyze
   * @return Dataset of top movies grouped by decade as (String, List[ProcessedMovieRecord])
   * @throws IllegalArgumentException if movies is null or empty
   */
  def findTopMoviesByDecade(movies: Dataset[ProcessedMovieRecord]): Dataset[(String, List[ProcessedMovieRecord])] = {
    /*
     * Step 1: Group movies by decade using functional grouping
     * The decade field is computed during enrichment phase
     */
    movies
      .groupByKey(_.decade) // Group by computed decade field
      .mapGroups { case (decade, movieIter) =>
        /*
         * Step 2: Find top 5 movies for each decade
         * This closure captures the decade parameter and processes the movie iterator
         */
        val movieList = movieIter.toList
        (decade, DataAggregationFunctions.findTopMovies(movieList, 5)) // Pure top-N selection
      }
  }
}
