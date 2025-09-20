package com.functionalpipeline.functions

import com.functionalpipeline.models.{MovieRecord, ProcessedMovieRecord, RatingCategory, Masterpiece, Excellent, Great, Good, Average, Poor, Bad}
import com.functionalpipeline.functions.Combinators._

/** Provides pure functions for data transformation as described.
 *
 *  This object contains pure functions that transform raw movie data into
 *  enriched, processed records. All functions are side-effect free and
 *  demonstrate core functional programming principles including immutability,
 *  pure functions, and function composition.
 *
 *  @author Almog Roter and Yonathan Cohen
 *  @version 1.0.0
 *  @since September 2025
 */
object DataTransformationFunctions {
  
  /**
   * Enriches movie record with computed fields using when combinator.
   * 
   * This function demonstrates the core transformation logic of the pipeline,
   * combining multiple pure functions to create an enriched movie record.
   * It uses the custom 'when' combinator for conditional processing and
   * applies various business rules for data enrichment.
   * 
   * @param movie The original movie record to enrich
   * @return ProcessedMovieRecord with additional computed fields
   * @throws IllegalArgumentException if movie is null
   */
  def enrichMovieRecord(movie: MovieRecord): ProcessedMovieRecord = {
    /*
     * Step 1: Create enriched record with computed fields
     * Demonstrates pure function composition and immutable data creation
     */
    ProcessedMovieRecord(
      movie = movie,  // Preserve original movie data
      decade = computeDecade(movie.year),  // Compute decade from year
      ratingCategory = categorizeRating(movie.rating).toString,  // Categorize rating
      /*
       * Step 2: Apply conditional transformation using when combinator
       * If rating has .9 or higher decimal part, round up the popularity score
       */
      popularityScore = when(hasDecimalNine(movie.rating))(roundUp)(computePopularityScore(movie.rating, movie.votes))
    )
  }
  
  /**
   * Checks if a number has .9 or higher in its decimal part.
   * 
   * This helper function determines whether a rating should trigger
   * the rounding-up behavior in the popularity score calculation.
   * 
   * @param rating The rating value to check
   * @return true if the decimal part is 0.9 or higher, false otherwise
   */
  private def hasDecimalNine(rating: Double): Boolean = {
    val decimalPart = rating - rating.toInt  // Extract decimal part
    decimalPart >= 0.9  // Check if decimal part is 0.9 or higher
  }
  
  /**
   * Rounds up numbers that end in .9 or higher.
   * 
   * This function is used in conjunction with the when combinator
   * to apply conditional rounding to popularity scores.
   * 
   * @param score The score to round up
   * @return The ceiling of the score
   */
  private def roundUp(score: Double): Double = Math.ceil(score)
  
  /**
   * Computes decade string from year.
   * 
   * This function converts a year to its corresponding decade string
   * for grouping and analysis purposes.
   * 
   * @param year The year to convert
   * @return String representation of the decade (e.g., "1990s", "2000s")
   */
  private def computeDecade(year: Int): String = {
    val decadeStart = (year / 10) * 10  // Calculate decade start year
    s"${decadeStart}s"  // Format as decade string
  }
  
  // Custom extractors for advanced pattern matching
  object MasterpieceRating {
    def unapply(rating: Double): Option[Double] = 
      if (rating >= 9.0) Some(rating) else None
  }
  
  object ExcellentRating {
    def unapply(rating: Double): Option[Double] = 
      if (rating >= 8.0 && rating < 9.0) Some(rating) else None
  }
  
  object GreatRating {
    def unapply(rating: Double): Option[Double] = 
      if (rating >= 7.0 && rating < 8.0) Some(rating) else None
  }
  
  object GoodRating {
    def unapply(rating: Double): Option[Double] = 
      if (rating >= 6.0 && rating < 7.0) Some(rating) else None
  }
  
  object AverageRating {
    def unapply(rating: Double): Option[Double] = 
      if (rating >= 5.0 && rating < 6.0) Some(rating) else None
  }
  
  object PoorRating {
    def unapply(rating: Double): Option[Double] = 
      if (rating >= 4.0 && rating < 5.0) Some(rating) else None
  }

  /**
   * Categorizes rating using advanced pattern matching with custom extractors.
   * 
   * This function demonstrates sophisticated pattern matching by using custom
   * extractors instead of simple guard conditions. This approach provides
   * better reusability, composability, and follows functional programming
   * principles more closely.
   * 
   * @param rating The numeric rating to categorize
   * @return RatingCategory object representing the rating tier
   */
  private def categorizeRating(rating: Double): RatingCategory = {
    rating match {
      case MasterpieceRating(_) => Masterpiece
      case ExcellentRating(_) => Excellent
      case GreatRating(_) => Great
      case GoodRating(_) => Good
      case AverageRating(_) => Average
      case PoorRating(_) => Poor
      case _ => Bad
    }
  }
  
  /**
   * Computes popularity score from rating and votes.
   * 
   * This function creates a composite popularity score by combining
   * normalized rating and vote count with weighted averages. It
   * demonstrates mathematical computation in functional programming.
   * 
   * @param rating The movie's average rating
   * @param votes The number of votes received
   * @return Computed popularity score between 0.0 and 1.0
   */
  private def computePopularityScore(rating: Double, votes: Long): Double = {
    /*
     * Step 1: Normalize votes to a 0-1 scale
     * Assumes maximum votes around 1 million for normalization
     */
    val normalizedVotes = Math.min(votes / 1000000.0, 1.0)
    
    /*
     * Step 2: Combine rating and votes with weighted averages
     * Rating gets 70% weight, normalized votes get 30% weight
     */
    (rating / 10.0) * 0.7 + normalizedVotes * 0.3
  }
  
  /**
   * Transforms movie to simplified tuple format.
   * 
   * This utility function extracts key fields from a movie record
   * into a simple tuple format for lightweight processing.
   * 
   * @param movie The movie record to transform
   * @return Tuple containing (title, year, rating)
   */
  def toSimpleFormat(movie: MovieRecord): (String, Int, Double) = {
    (movie.title, movie.year, movie.rating)
  }
  
  /**
   * Creates formatted summary string for movie using advanced pattern matching.
   * 
   * This function demonstrates sophisticated pattern matching with custom
   * extractors, higher-order functions, and functional composition.
   * 
   * @param movie The movie record to summarize
   * @return Formatted string with title, year, genre, rating, and votes
   */
  def createSummary(movie: MovieRecord): String = {
    movie match {
      case MovieRecord(_, title, year, genre, MasterpieceRating(rating), votes) =>
        s"MASTERPIECE: $title ($year) - $genre - Rating: $rating/10 ($votes votes)"
      case MovieRecord(_, title, year, genre, ExcellentRating(rating), votes) =>
        s"EXCELLENT: $title ($year) - $genre - Rating: $rating/10 ($votes votes)"
      case MovieRecord(_, title, year, genre, GreatRating(rating), votes) =>
        s"GREAT: $title ($year) - $genre - Rating: $rating/10 ($votes votes)"
      case MovieRecord(_, title, year, genre, rating, votes) =>
        s"$title ($year) - $genre - Rating: $rating/10 ($votes votes)"
    }
  }

  // Higher-order functions with pattern matching
  def createMovieProcessor(processorType: String): MovieRecord => String = {
    processorType match {
      case "premium" => movie => movie match {
        case MovieRecord(_, title, year, genre, MasterpieceRating(rating), votes) =>
          s"PREMIUM: $title ($year) - $genre - $rating/10 ($votes votes)"
        case _ => s"Standard: ${movie.title}"
      }
      case "popular" => movie => movie match {
        case MovieRecord(_, title, year, genre, rating, votes) if votes >= 1000000 =>
          s"POPULAR: $title ($year) - $genre - $rating/10 ($votes votes)"
        case _ => s"Niche: ${movie.title}"
      }
      case "era" => movie => movie match {
        case MovieRecord(_, title, year, genre, rating, votes) if year >= 2020 =>
          s"2020s: $title ($year) - $genre - $rating/10"
        case MovieRecord(_, title, year, genre, rating, votes) if year >= 2010 =>
          s"2010s: $title ($year) - $genre - $rating/10"
        case MovieRecord(_, title, year, genre, rating, votes) if year >= 2000 =>
          s"2000s: $title ($year) - $genre - $rating/10"
        case _ => s"Classic: ${movie.title}"
      }
      case _ => movie => s"${movie.title}"
    }
  }

  // Functional composition with pattern matching
  def processMovieWithCombinators(movie: MovieRecord): String = {
    val processors = List(
      createMovieProcessor("premium"),
      createMovieProcessor("popular"),
      createMovieProcessor("era")
    )
    
    processors.foldLeft(s"${movie.title}") { (acc, processor) =>
      val result = processor(movie)
      if (result != s"${movie.title}") result else acc
    }
  }

  // Monadic operations with pattern matching
  def processMovieSafely(movie: Option[MovieRecord]): Either[String, String] = {
    movie match {
      case Some(m) => Right(createSummary(m))
      case None => Left("No movie data available")
    }
  }

  // Pattern matching with functional combinators
  def processMovieListFunctionally(movies: List[MovieRecord]): List[String] = {
    movies match {
      case Nil => Nil
      case head :: tail => 
        processMovieWithCombinators(head) :: processMovieListFunctionally(tail)
    }
  }
}
