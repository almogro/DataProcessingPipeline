package com.functionalpipeline.functions

import com.functionalpipeline.models.MovieRecord
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/**
 * Unit tests for DataFilteringFunctions.
 * 
 * These tests demonstrate testing of higher-order functions and currying.
 */
class DataFilteringFunctionsTest extends AnyFlatSpec with Matchers {
  
  val sampleMovie = MovieRecord("1", "Test Movie", 2020, "Action", 8.5, 1500)
  val lowQualityMovie = MovieRecord("2", "Bad Movie", 2020, "Comedy", 3.0, 100)
  
  "isHighQualityMovie" should "return true for high quality movie" in {
    val result = DataFilteringFunctions.isHighQualityMovie(sampleMovie)
    result shouldBe true
  }
  
  it should "return false for low quality movie" in {
    val result = DataFilteringFunctions.isHighQualityMovie(lowQualityMovie)
    result shouldBe false
  }
  
  "isFromDecade" should "return correct function for 2020s decade" in {
    val isFrom2020s = DataFilteringFunctions.isFromDecade("2020s")
    
    isFrom2020s(sampleMovie) shouldBe true
    isFrom2020s(MovieRecord("3", "Old Movie", 1990, "Drama", 7.0, 1000)) shouldBe false
  }
  
  "hasMinimumRating" should "return correct function for minimum rating" in {
    val hasMinRating8 = DataFilteringFunctions.hasMinimumRating(8.0)
    
    hasMinRating8(sampleMovie) shouldBe true
    hasMinRating8(lowQualityMovie) shouldBe false
  }
  
  "hasMinimumVotes" should "return correct function for minimum votes" in {
    val hasMinVotes1000 = DataFilteringFunctions.hasMinimumVotes(1000)
    
    hasMinVotes1000(sampleMovie) shouldBe true
    hasMinVotes1000(lowQualityMovie) shouldBe false
  }
  
  "combineFilters" should "apply all filters with AND logic" in {
    val filters = List(
      DataFilteringFunctions.hasMinimumRating(8.0),
      DataFilteringFunctions.hasMinimumVotes(1000),
      DataFilteringFunctions.isGenre("Action")
    )
    
    val combinedFilter = DataFilteringFunctions.combineFilters(filters)
    
    combinedFilter(sampleMovie) shouldBe true
    combinedFilter(MovieRecord("4", "Drama Movie", 2020, "Drama", 8.5, 1500)) shouldBe false
  }
  
  "isGenre" should "return correct function for specific genre" in {
    val isAction = DataFilteringFunctions.isGenre("Action")
    
    isAction(sampleMovie) shouldBe true
    isAction(lowQualityMovie) shouldBe false
  }
}
