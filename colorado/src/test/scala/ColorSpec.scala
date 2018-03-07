package colorado

import org.scalatest._

class ColorSpec extends AsyncFreeSpec with MustMatchers {
  "RGB" - {
    "from hex string" in {
      RGB.from("A1B1C1") mustEqual RGB(0xA1, 0xB1, 0xC1)
      RGB.from("#A1B1C1") mustEqual RGB(0xA1, 0xB1, 0xC1)
    }
  }
}
