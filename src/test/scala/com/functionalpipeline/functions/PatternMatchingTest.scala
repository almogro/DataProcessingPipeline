package com.functionalpipeline.functions

import com.functionalpipeline.models._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/** Test suite for advanced PatternMatching as described.
 *
 * This test suite validates the correctness of advanced pattern matching
 * implementations including custom extractors, higher-order functions,
 * monadic operations, and functional combinators.
 *
 * @author Almog Roter and Yonathan Cohen
 * @version 1.0.0
 * @since September 2025
 */
class PatternMatchingTest extends AnyFlatSpec with Matchers {

  "processMovieWithExtractors" should "handle high quality movies correctly" in {
    val highQualityMovie = MovieRecord("1", "Inception", 2010, "Sci-Fi", 8.8, 2000000L)
    val result = PatternMatching.processMovieWithExtractors(highQualityMovie)
    result should include("Premium quality")
    result should include("Inception")
  }

  it should "handle popular movies correctly" in {
    val popularMovie = MovieRecord("2", "The Matrix", 1999, "Action", 7.5, 1500000L)
    val result = PatternMatching.processMovieWithExtractors(popularMovie)
    result should include("Popular")
    result should include("The Matrix")
  }

  it should "handle decade classification correctly" in {
    val movie2010s = MovieRecord("3", "Interstellar", 2014, "Sci-Fi", 8.6, 800000L)
    val result = PatternMatching.processMovieWithExtractors(movie2010s)
    result should include("2010s")
    result should include("Interstellar")
  }

  it should "handle classic movies correctly" in {
    val classicMovie = MovieRecord("4", "The Godfather", 1972, "Drama", 9.2, 1800000L)
    val result = PatternMatching.processMovieWithExtractors(classicMovie)
    result should include("Classic film")
    result should include("The Godfather")
  }

  "createMovieProcessor" should "create premium processor correctly" in {
    val processor = PatternMatching.createMovieProcessor("premium")
    val highQualityMovie = MovieRecord("1", "Inception", 2010, "Sci-Fi", 8.8, 2000000L)
    val result = processor(highQualityMovie)
    result should include("PREMIUM")
    result should include("Inception")
  }

  it should "create popular processor correctly" in {
    val processor = PatternMatching.createMovieProcessor("popular")
    val popularMovie = MovieRecord("2", "The Matrix", 1999, "Action", 7.5, 1500000L)
    val result = processor(popularMovie)
    result should include("POPULAR")
    result should include("The Matrix")
  }

  it should "create decade processor correctly" in {
    val processor = PatternMatching.createMovieProcessor("decade")
    val movie2010s = MovieRecord("3", "Interstellar", 2014, "Sci-Fi", 8.6, 800000L)
    val result = processor(movie2010s)
    result should include("2010s")
    result should include("Interstellar")
  }

  "processRatingCategory" should "handle all rating categories correctly" in {
    PatternMatching.processRatingCategory(Masterpiece) should include("MASTERPIECE")
    PatternMatching.processRatingCategory(Excellent) should include("EXCELLENT")
    PatternMatching.processRatingCategory(Great) should include("GREAT")
    PatternMatching.processRatingCategory(Good) should include("GOOD")
    PatternMatching.processRatingCategory(Average) should include("AVERAGE")
    PatternMatching.processRatingCategory(Poor) should include("POOR")
    PatternMatching.processRatingCategory(Bad) should include("BAD")
  }

  "processMovieSafely" should "handle Some movie correctly" in {
    val movie = MovieRecord("1", "Test Movie", 2020, "Action", 8.0, 100000L)
    val result = PatternMatching.processMovieSafely(Some(movie))
    result shouldBe Right("Test Movie: Standard movie")
  }

  it should "handle None correctly" in {
    val result = PatternMatching.processMovieSafely(None)
    result shouldBe Left("No movie data available")
  }

  "processMovieListMonadically" should "process list of optional movies correctly" in {
    val movies = List(
      Some(MovieRecord("1", "Movie1", 2020, "Action", 8.0, 100000L)),
      None,
      Some(MovieRecord("2", "Movie2", 2021, "Drama", 7.5, 150000L))
    )
    val result = PatternMatching.processMovieListMonadically(movies)
    result should have length 3
    result(0) shouldBe Right("Movie1: Standard movie")
    result(1) shouldBe Left("No movie data available")
    result(2) shouldBe Right("Movie2: Standard movie")
  }

  "createMovieAnalyzer" should "create quality analyzer correctly" in {
    val analyzer = PatternMatching.createMovieAnalyzer("quality")
    val highQualityMovie = MovieRecord("1", "Inception", 2010, "Sci-Fi", 8.8, 2000000L)
    val result = analyzer(highQualityMovie)
    result should include("Quality Analysis")
    result should include("high-quality")
  }

  it should "create popularity analyzer correctly" in {
    val analyzer = PatternMatching.createMovieAnalyzer("popularity")
    val popularMovie = MovieRecord("2", "The Matrix", 1999, "Action", 7.5, 1500000L)
    val result = analyzer(popularMovie)
    result should include("Popularity Analysis")
    result should include("popular")
  }

  it should "create era analyzer correctly" in {
    val analyzer = PatternMatching.createMovieAnalyzer("era")
    val movie2010s = MovieRecord("3", "Interstellar", 2014, "Sci-Fi", 8.6, 800000L)
    val result = analyzer(movie2010s)
    result should include("Era Analysis")
    result should include("2010s")
  }

  "analyzeMovieWithMultipleAnalyzers" should "analyze movie with multiple analyzers" in {
    val movie = MovieRecord("1", "Inception", 2010, "Sci-Fi", 8.8, 2000000L)
    val result = PatternMatching.analyzeMovieWithMultipleAnalyzers(movie)
    result should have length 3
    result should contain("Quality Analysis: Inception is high-quality (8.8/10, 2000000 votes)")
    result should contain("Popularity Analysis: Inception is popular in Sci-Fi (2000000 votes)")
    result should contain("Era Analysis: Inception represents 2010s Sci-Fi (8.8/10)")
  }

  "processMovieWithMonadicErrorHandling" should "handle Some movie correctly" in {
    val movie = MovieRecord("1", "Test Movie", 2020, "Action", 8.0, 100000L)
    val result = PatternMatching.processMovieWithMonadicErrorHandling(Some(movie))
    result shouldBe Some("Test Movie: Standard movie")
  }

  it should "handle None correctly" in {
    val result = PatternMatching.processMovieWithMonadicErrorHandling(None)
    result shouldBe None
  }

  "createMovieProcessorPipeline" should "create processor pipeline correctly" in {
    val processors = List("premium", "popular", "decade")
    val pipeline = PatternMatching.createMovieProcessorPipeline(processors)
    val movie = MovieRecord("1", "Inception", 2010, "Sci-Fi", 8.8, 2000000L)
    val result = pipeline(movie)
    result should have length 3
    result should contain("PREMIUM: Inception (8.8/10, 2000000 votes)")
    result should contain("POPULAR: Inception - Sci-Fi (2000000 votes)")
    result should contain("2010s: Inception (Sci-Fi, 8.8/10)")
  }

  "processMovieWithErrorRecovery" should "handle Some movie correctly" in {
    val movie = MovieRecord("1", "Test Movie", 2020, "Action", 8.0, 100000L)
    val result = PatternMatching.processMovieWithErrorRecovery(Some(movie))
    result shouldBe "Test Movie: Standard movie"
  }

  it should "handle None with recovery" in {
    val result = PatternMatching.processMovieWithErrorRecovery(None)
    result shouldBe "No movie data available"
  }

  "processMovieWithAdvancedCombinators" should "process movie with advanced combinators" in {
    val movie = MovieRecord("1", "Inception", 2010, "Sci-Fi", 8.8, 2000000L)
    val result = PatternMatching.processMovieWithAdvancedCombinators(movie)
    result should include("PREMIUM")
    result should include("Inception")
  }

  "processMovieListWithCombinators" should "process list of movies with combinators" in {
    val movies = List(
      MovieRecord("1", "Movie1", 2020, "Action", 8.0, 100000L),
      MovieRecord("2", "Movie2", 2021, "Drama", 7.5, 150000L)
    )
    val result = PatternMatching.processMovieListWithCombinators(movies)
    result should have length 2
    result.head should include("Movie1")
    result(1) should include("Movie2")
  }
}