package com.functionalpipeline.functions

import com.functionalpipeline.models.{MovieRecord, ProcessedMovieRecord, RatingCategory, Masterpiece, Excellent, Great, Good, Average, Poor, Bad}
import com.functionalpipeline.functions.Combinators._

/** Pure functions for data transformation and enrichment. */
object DataTransformationFunctions {
  
  /** Enriches movie record with computed fields using when combinator. */
  def enrichMovieRecord(movie: MovieRecord): ProcessedMovieRecord = {
    ProcessedMovieRecord(
      movie = movie,
      decade = computeDecade(movie.year),
      ratingCategory = categorizeRating(movie.rating).toString,
      popularityScore = when(hasDecimalNine(movie.rating))(roundUp)(computePopularityScore(movie.rating, movie.votes))
    )
  }
  
  /** Checks if a number has .9 or higher in its decimal part. */
  private def hasDecimalNine(rating: Double): Boolean = {
    val decimalPart = rating - rating.toInt
    decimalPart >= 0.9
  }
  
  /** Rounds up numbers that end in .9 or higher. */
  private def roundUp(score: Double): Double = Math.ceil(score)
  
  /** Computes decade string from year. */
  private def computeDecade(year: Int): String = {
    val decadeStart = (year / 10) * 10
    s"${decadeStart}s"
  }
  
  /** Categorizes rating using pattern matching. */
  private def categorizeRating(rating: Double): RatingCategory = {
    rating match {
      case r if r >= 9.0 => Masterpiece
      case r if r >= 8.0 => Excellent
      case r if r >= 7.0 => Great
      case r if r >= 6.0 => Good
      case r if r >= 5.0 => Average
      case r if r >= 4.0 => Poor
      case _ => Bad
    }
  }
  
  /** Computes popularity score from rating and votes. */
  private def computePopularityScore(rating: Double, votes: Long): Double = {
    // Normalize votes to a 0-1 scale (assuming max votes around 1M)
    val normalizedVotes = Math.min(votes / 1000000.0, 1.0)
    
    // Combine rating and votes with weights
    (rating / 10.0) * 0.7 + normalizedVotes * 0.3
  }
  
  /** Transforms movie to simplified tuple format. */
  def toSimpleFormat(movie: MovieRecord): (String, Int, Double) = {
    (movie.title, movie.year, movie.rating)
  }
  
  /** Creates formatted summary string for movie. */
  def createSummary(movie: MovieRecord): String = {
    s"${movie.title} (${movie.year}) - ${movie.genre} - Rating: ${movie.rating}/10 (${movie.votes} votes)"
  }
}
