package com.functionalpipeline.functions

import com.functionalpipeline.models.{MovieRecord, ProcessedMovieRecord}

/**
 * Pure functions for transforming data.
 * 
 * These functions demonstrate functional programming concepts
 * including immutability, pure functions, and pattern matching.
 */
object DataTransformationFunctions {
  
  /**
   * Enriches a movie record with additional computed fields.
   * 
   * This function demonstrates pattern matching and pure transformations.
   * 
   * @param movie The original movie record
   * @return An enriched ProcessedMovieRecord
   */
  def enrichMovieRecord(movie: MovieRecord): ProcessedMovieRecord = {
    ProcessedMovieRecord(
      movie = movie,
      decade = computeDecade(movie.year),
      ratingCategory = categorizeRating(movie.rating),
      popularityScore = computePopularityScore(movie.rating, movie.votes)
    )
  }
  
  /**
   * Computes the decade string for a given year.
   * 
   * @param year The release year
   * @return The decade string (e.g., "1990s", "2000s")
   */
  private def computeDecade(year: Int): String = {
    val decadeStart = (year / 10) * 10
    s"${decadeStart}s"
  }
  
  /**
   * Categorizes a rating into a descriptive category.
   * 
   * @param rating The movie rating
   * @return A categorical description of the rating
   */
  private def categorizeRating(rating: Double): String = {
    rating match {
      case r if r >= 9.0 => "Masterpiece"
      case r if r >= 8.0 => "Excellent"
      case r if r >= 7.0 => "Great"
      case r if r >= 6.0 => "Good"
      case r if r >= 5.0 => "Average"
      case r if r >= 4.0 => "Poor"
      case _ => "Bad"
    }
  }
  
  /**
   * Computes a popularity score based on rating and votes.
   * 
   * This is a pure function that computes a derived value.
   * 
   * @param rating The movie rating
   * @param votes The number of votes
   * @return A computed popularity score
   */
  private def computePopularityScore(rating: Double, votes: Long): Double = {
    // Normalize votes to a 0-1 scale (assuming max votes around 1M)
    val normalizedVotes = Math.min(votes / 1000000.0, 1.0)
    
    // Combine rating and votes with weights
    (rating / 10.0) * 0.7 + normalizedVotes * 0.3
  }
  
  /**
   * Transforms a movie record to a simplified format.
   * 
   * @param movie The movie record to transform
   * @return A tuple of (title, year, rating)
   */
  def toSimpleFormat(movie: MovieRecord): (String, Int, Double) = {
    (movie.title, movie.year, movie.rating)
  }
  
  /**
   * Creates a summary string for a movie record.
   * 
   * @param movie The movie record to summarize
   * @return A formatted summary string
   */
  def createSummary(movie: MovieRecord): String = {
    s"${movie.title} (${movie.year}) - ${movie.genre} - Rating: ${movie.rating}/10 (${movie.votes} votes)"
  }
}
