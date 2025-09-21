package com.functionalpipeline.functions

import com.functionalpipeline.models._

/** Provides advanced functional pattern matching examples as described.
 *
 * This object demonstrates sophisticated pattern matching techniques in Scala,
 * including custom extractors, higher-order functions, monadic operations,
 * and functional combinators. These examples showcase how pattern matching
 * can be used for advanced functional programming constructs beyond simple
 * case matching.
 *
 * @author Almog Roter and Yonathan Cohen
 * @version 1.0.0
 * @since September 2025
 */
object PatternMatching {

  // Custom extractors for advanced pattern matching
  private object HighQualityMovie {
    def unapply(movie: MovieRecord): Option[(String, Double, Long)] = {
      if (movie.rating >= 8.0 && movie.votes >= 100000)
        Some((movie.title, movie.rating, movie.votes))
      else None
    }
  }

  private object PopularMovie {
    def unapply(movie: MovieRecord): Option[(String, String, Long)] = {
      if (movie.votes >= 1000000)
        Some((movie.title, movie.genre, movie.votes))
      else None
    }
  }

  private object DecadeRange {
    def unapply(year: Int): Option[String] = year match {
      case y if y >= 2020 => Some("2020s")
      case y if y >= 2010 => Some("2010s")
      case y if y >= 2000 => Some("2000s")
      case y if y >= 1990 => Some("1990s")
      case y if y >= 1980 => Some("1980s")
      case _ => Some("Pre-1980s")
    }
  }

  // Pattern matching with custom extractors and higher-order functions
  def processMovieWithExtractors(movie: MovieRecord): String = {
    movie match {
      case HighQualityMovie(title, rating, votes) =>
        s"$title: Premium quality (${rating}/10, $votes votes)"
      case PopularMovie(title, genre, votes) =>
        s"$title: Popular $genre movie ($votes votes)"
      case MovieRecord(_, title, DecadeRange(decade), genre, rating, votes) =>
        s"$title: $decade $genre film (${rating}/10)"
      case MovieRecord(_, title, year, _, rating, _) if year < 1980 =>
        s"$title: Classic film from $year (${rating}/10)"
      case _ => s"${movie.title}: Standard movie"
    }
  }

  // Pattern matching with monadic operations and functional combinators
  def processMovieListFunctionally(movies: List[MovieRecord]): List[String] = {
    movies match {
      case Nil => Nil
      case head :: tail =>
        processMovieWithExtractors(head) :: processMovieListFunctionally(tail)
    }
  }

  // Pattern matching with higher-order functions and currying
  def createMovieProcessor(processorType: String): MovieRecord => String = {
    processorType match {
      case "premium" => {
        case HighQualityMovie(title, rating, votes) =>
          s"PREMIUM: $title (${rating}/10, $votes votes)"
        case movie => s"Standard: ${movie.title}"
      }
      case "popular" => {
        case PopularMovie(title, genre, votes) =>
          s"POPULAR: $title - $genre ($votes votes)"
        case movie => s"Niche: ${movie.title}"
      }
      case "decade" => {
        case MovieRecord(_, title, DecadeRange(decade), genre, rating, _) =>
          s"$decade: $title ($genre, ${rating}/10)"
        case movie => s"${movie.title}"
      }
      case _ => movie => s"${movie.title}"
    }
  }

  // Pattern matching with functional combinators and composition
  def processMovieWithCombinators(movie: MovieRecord): String = {
    val processors = List(
      createMovieProcessor("premium"),
      createMovieProcessor("popular"),
      createMovieProcessor("decade")
    )

    processors.foldLeft(s"${movie.title}") { (acc, processor) =>
      val result = processor(movie)
      if (result != s"${movie.title}") result else acc
    }
  }

  // Pattern matching with algebraic data types and sealed traits
  def processRatingCategory(category: RatingCategory): String = {
    category match {
      case Masterpiece => "MASTERPIECE: Exceptional cinematic achievement"
      case Excellent => "EXCELLENT: Outstanding quality"
      case Great => "GREAT: Very good quality"
      case Good => "GOOD: Solid quality"
      case Average => "AVERAGE: Decent quality"
      case Poor => "POOR: Below average quality"
      case Bad => "BAD: Low quality"
    }
  }

  // Pattern matching with functional error handling
  def processMovieSafely(movie: Option[MovieRecord]): Either[String, String] = {
    movie match {
      case Some(m) => Right(processMovieWithExtractors(m))
      case None => Left("No movie data available")
    }
  }

  // Pattern matching with functional composition and monadic operations
  def processMovieListMonadically(movies: List[Option[MovieRecord]]): List[Either[String, String]] = {
    movies.map(processMovieSafely)
  }

  // Pattern matching with functional composition and currying
  def createMovieAnalyzer(analysisType: String): MovieRecord => String = {
    analysisType match {
      case "quality" => {
        case HighQualityMovie(title, rating, votes) =>
          s"Quality Analysis: $title is high-quality (${rating}/10, $votes votes)"
        case movie => s"Quality Analysis: ${movie.title} is standard quality"
      }
      case "popularity" => {
        case PopularMovie(title, genre, votes) =>
          s"Popularity Analysis: $title is popular in $genre ($votes votes)"
        case movie => s"Popularity Analysis: ${movie.title} is niche"
      }
      case "era" => {
        case MovieRecord(_, title, DecadeRange(decade), genre, rating, _) =>
          s"Era Analysis: $title represents $decade $genre (${rating}/10)"
        case movie => s"Era Analysis: ${movie.title} is timeless"
      }
      case _ => movie => s"General Analysis: ${movie.title}"
    }
  }

  // Pattern matching with functional combinators and composition
  def analyzeMovieWithMultipleAnalyzers(movie: MovieRecord): List[String] = {
    val analyzers = List("quality", "popularity", "era")
    analyzers.map(createMovieAnalyzer).map(_(movie))
  }

  // Pattern matching with functional error handling and monadic operations
  def processMovieWithMonadicErrorHandling(movie: Option[MovieRecord]): Option[String] = {
    movie.map(processMovieWithExtractors)
  }

  // Pattern matching with functional combinators and higher-order functions
  def processMovieListWithCombinators(movies: List[MovieRecord]): List[String] = {
    movies.map(processMovieWithCombinators)
  }

  // Pattern matching with functional composition and currying
  def createMovieProcessorPipeline(processors: List[String]): MovieRecord => List[String] = {
    movie => processors.map(createMovieProcessor).map(_(movie))
  }

  // Pattern matching with functional error handling and recovery
  def processMovieWithErrorRecovery(movie: Option[MovieRecord]): String = {
    movie match {
      case Some(m) => processMovieWithExtractors(m)
      case None => "No movie data available"
    }
  }

  // Pattern matching with functional combinators and composition
  def processMovieWithAdvancedCombinators(movie: MovieRecord): String = {
    val processors = List(
      createMovieProcessor("premium"),
      createMovieProcessor("popular"),
      createMovieProcessor("decade")
    )

    processors.foldLeft(s"${movie.title}") { (acc, processor) =>
      val result = processor(movie)
      if (result != s"${movie.title}") result else acc
    }
  }
}