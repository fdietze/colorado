package colorado

import org.scalatest._
import ColorConversion._
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.must.Matchers

class ColorConversionSpec extends AsyncFreeSpec with Matchers {
  "LAB to RGB and back" in {
    def test(l:Double,a:Double,b:Double, rgb:Array[Double]) = {
      val accuracy = 0.02
      val rgbArray = labToRGB(l,a,b)
      rgbArray(0) mustEqual rgb(0) +- accuracy
      rgbArray(1) mustEqual rgb(1) +- accuracy
      rgbArray(2) mustEqual rgb(2) +- accuracy

      val labArray = rgbToLAB(rgb(0), rgb(1), rgb(2))
      labArray(0) mustEqual l +- accuracy
      labArray(1) mustEqual a +- accuracy
      labArray(2) mustEqual b +- accuracy
    }
    test(0,0,0, Array(0,0,0))
    test(100,0,0, Array(1,1,1))
    test(70,5,10, Array(0.7359,0.6566,0.6010))
  }
}
