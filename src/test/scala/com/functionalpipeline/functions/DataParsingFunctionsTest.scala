package com.functionalpipeline.functions

import com.functionalpipeline.models.MovieRecord
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/**
 * Unit tests for DataParsingFunctions.
 *
 * These tests demonstrate testing of pure functions in functional programming.
 */
class DataParsingFunctionsTest extends AnyFlatSpec with Matchers {

  "parseMovieRecord" should "parse a valid CSV line correctly" in {
    val validLine = "1,The Shawshank Redemption,1994,Drama,9.3,2500000"
    val result = DataParsingFunctions.parseMovieRecord(validLine)

    result shouldBe defined
    result.get shouldBe MovieRecord("1", "The Shawshank Redemption", 1994, "Drama", 9.3, 2500000)
  }

  it should "return None for invalid CSV line with insufficient fields" in {
    val invalidLine = "1,The Shawshank Redemption,1994,Drama"
    val result = DataParsingFunctions.parseMovieRecord(invalidLine)

    result shouldBe None
  }

  it should "return None for CSV line with invalid number format" in {
    val invalidLine = "1,The Shawshank Redemption,not-a-year,Drama,9.3,2500000"
    val result = DataParsingFunctions.parseMovieRecord(invalidLine)

    result shouldBe None
  }

  "isValidMovieRecord" should "return true for a valid movie record" in {
    val validMovie = MovieRecord("1", "Test Movie", 2020, "Action", 8.5, 1000)
    val result = DataParsingFunctions.isValidMovieRecord(validMovie)

    result shouldBe true
  }

  it should "return false for movie with invalid year" in {
    val invalidMovie = MovieRecord("1", "Test Movie", 1800, "Action", 8.5, 1000)
    val result = DataParsingFunctions.isValidMovieRecord(invalidMovie)

    result shouldBe false
  }

  it should "return false for movie with invalid rating" in {
    val invalidMovie = MovieRecord("1", "Test Movie", 2020, "Action", 15.0, 1000)
    val result = DataParsingFunctions.isValidMovieRecord(invalidMovie)

    result shouldBe false
  }

  "parseAndValidateMovieRecord" should "return Some for valid and well-formed record" in {
    val validLine = "1,The Shawshank Redemption,1994,Drama,9.3,2500000"
    val result = DataParsingFunctions.parseAndValidateMovieRecord(validLine)

    result shouldBe defined
  }

  it should "return None for valid format but invalid data" in {
    val invalidLine = "1,The Shawshank Redemption,1800,Drama,9.3,2500000"
    val result = DataParsingFunctions.parseAndValidateMovieRecord(invalidLine)

    result shouldBe None
  }
}
