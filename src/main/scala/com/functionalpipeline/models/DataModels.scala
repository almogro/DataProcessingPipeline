package com.functionalpipeline.models

/**
 * Data models for the functional programming pipeline.
 * 
 * These case classes represent immutable data structures
 * that follow functional programming principles.
 */

/**
 * Base trait for all data records in the pipeline.
 */
sealed trait DataRecord

/**
 * Represents a movie record with immutable properties.
 * 
 * @param id Unique identifier for the movie
 * @param title Title of the movie
 * @param year Release year
 * @param genre Primary genre
 * @param rating Average rating
 * @param votes Number of votes
 */
case class MovieRecord(
  id: String,
  title: String,
  year: Int,
  genre: String,
  rating: Double,
  votes: Long
) extends DataRecord

/**
 * Represents a processed movie record with additional computed fields.
 * 
 * @param movie The original movie record
 * @param decade The decade the movie was released in
 * @param ratingCategory Categorical rating (Poor, Average, Good, Excellent)
 * @param popularityScore Computed popularity score
 */
case class ProcessedMovieRecord(
  movie: MovieRecord,
  decade: String,
  ratingCategory: String,
  popularityScore: Double
) extends DataRecord

/**
 * Represents aggregated statistics for a genre.
 * 
 * @param genre The movie genre
 * @param count Number of movies in this genre
 * @param averageRating Average rating for this genre
 * @param totalVotes Total votes across all movies in this genre
 * @param topMovie The highest rated movie in this genre
 */
case class GenreStatistics(
  genre: String,
  count: Long,
  averageRating: Double,
  totalVotes: Long,
  topMovie: String
)

/**
 * Represents the result of a data processing operation.
 * 
 * @param success Whether the operation was successful
 * @param data The processed data (if successful)
 * @param error Error message (if failed)
 */
case class ProcessingResult[A](
  success: Boolean,
  data: Option[A],
  error: Option[String]
)

/**
 * Configuration for the data pipeline.
 * 
 * @param inputPath Path to input data
 * @param outputPath Path for output data
 * @param minRating Minimum rating threshold for filtering
 * @param minVotes Minimum votes threshold for filtering
 */
case class PipelineConfig(
  inputPath: String,
  outputPath: String,
  minRating: Double,
  minVotes: Long
)
