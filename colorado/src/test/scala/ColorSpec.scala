package colorado

import org.scalatest._
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.must.Matchers

class ColorSpec extends AsyncFreeSpec with Matchers {
  "RGB" - {
    "from integers" in {
      RGB(0xA1, 0xB1, 0xC1) mustEqual RGB(0xA1 / 255.0, 0xB1 / 255.0, 0xC1 / 255.0)
    }

    "from hex string" in {
      RGB("A1B1C1") mustEqual RGB(0xA1, 0xB1, 0xC1)
      RGB("#A1B1C1") mustEqual RGB(0xA1, 0xB1, 0xC1)
    }

    "to hex string" in {
      RGB(0xA1, 0xB1, 0xC1).toHex mustEqual "#A1B1C1"
    }

    "to css" in {
      RGB(17, 28, 39).toCSS mustEqual "rgb(17,28,39)"
    }

    "to css with alpha" in {
      RGB(17, 28, 39).toCSS(0.2) mustEqual "rgba(17,28,39,0.2)"
    }
  }
}
