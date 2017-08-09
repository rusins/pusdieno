import models.WeekPlan
import org.scalatest.FlatSpec

class WeekPlanSuite extends FlatSpec {
  "Both 9:30 - 9:30" should "match, leaving only 3 unique times." in {
    assert(new WeekPlan(Array((930, 930), (1030, 1030), (930, 930), (930, 1030), (930, 930), (930, 930), (930, 930))).uniqueCount == 3)
    assert(new WeekPlan(Array((1030, 1030), (930, 930), (930, 930), (930, 1030), (930, 930), (930, 930), (930, 930))).uniqueCount == 3)
    assert(new WeekPlan(Array((930, 930), (1030, 1030), (930, 930), (930, 1030), (1030, 1030), (930, 930), (930, 930))).uniqueCount == 3)
  }

  "WeekPlans" should "only accept 7 days of the week" in {
    intercept[AssertionError] {
      new WeekPlan(Array((1, 1), (2, 2), (3, 3), (4, 4), (5, 5), (6, 6)))
    }
    intercept[AssertionError] {
      new WeekPlan(Array((1, 1), (2, 2), (3, 3), (4, 4), (5, 5), (6, 6), (7, 7), (8, 8)))
    }
    new WeekPlan(Array((1, 1), (2, 2), (3, 3), (4, 4), (5, 5), (6, 6), (7, 7)))
  }
}
