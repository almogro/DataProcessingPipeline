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

}
