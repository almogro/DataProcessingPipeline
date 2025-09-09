package com.functionalpipeline.functions

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/**
 * Unit tests for custom Combinators.
 * 
 * These tests demonstrate testing of custom combinator implementations.
 */
class CombinatorsTest extends AnyFlatSpec with Matchers {
  
  "when" should "apply function when condition is true" in {
    val result = Combinators.when(true)((x: Int) => x * 2)(5)
    result shouldBe 10
  }
  
  it should "not apply function when condition is false" in {
    val result = Combinators.when(false)((x: Int) => x * 2)(5)
    result shouldBe 5
  }
  
  "pipe" should "apply all transformations in sequence" in {
    val transformations = List[Int => Int](
      _ + 1,
      _ * 2,
      _ - 3
    )
    
    val result = Combinators.pipe(transformations)(5)
    result shouldBe 9 // ((5 + 1) * 2) - 3 = 9
  }
  
  it should "return original value for empty transformations" in {
    val result = Combinators.pipe(List.empty[Int => Int])(5)
    result shouldBe 5
  }
  
  "andThen" should "compose two functions correctly" in {
    val f = (x: Int) => x * 2
    val g = (x: Int) => x + 1
    
    val result = Combinators.andThen(f, g)(5)
    result shouldBe 11 // g(f(5)) = g(10) = 11
  }
  
  "compose" should "compose two functions in reverse order" in {
    val f = (x: Int) => x * 2
    val g = (x: Int) => x + 1
    
    val result = Combinators.compose(f, g)(5)
    result shouldBe 12 // f(g(5)) = f(6) = 12
  }
  
  "retry" should "succeed on first try" in {
    val f = (x: Int) => x * 2
    val result = Combinators.retry(f, 3)(5)
    
    result shouldBe Right(10)
  }
  
  it should "retry on failure and eventually succeed" in {
    var attempts = 0
    val f = (x: Int) => {
      attempts += 1
      if (attempts < 3) throw new RuntimeException("Temporary failure")
      else x * 2
    }
    
    val result = Combinators.retry(f, 3)(5)
    result shouldBe Right(10)
    attempts shouldBe 3
  }
  
  it should "return error after max retries" in {
    val f = (x: Int) => throw new RuntimeException("Always fails")
    val result = Combinators.retry(f, 2)(5)
    
    result.isLeft shouldBe true
    result.left.get.getMessage shouldBe "Always fails"
  }
  
  "mapOption" should "apply function to Some value" in {
    val f = (x: Int) => x * 2
    val result = Combinators.mapOption(f)(Some(5))
    
    result shouldBe Some(10)
  }
  
  it should "return None for None value" in {
    val f = (x: Int) => x * 2
    val result = Combinators.mapOption(f)(None)
    
    result shouldBe None
  }
  
  "flatMap" should "flatten list of lists" in {
    val f = (x: Int) => List(x, x * 2)
    val result = Combinators.flatMap(f)(List(1, 2, 3))
    
    result shouldBe List(1, 2, 2, 4, 3, 6)
  }
}
