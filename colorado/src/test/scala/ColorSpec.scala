package colorado

import org.scalatest._

class ColorSpec extends AsyncFreeSpec with MustMatchers {
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
  }
}
