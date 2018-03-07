package colorado

import java.lang.Math._

trait Color {
  def lab: LAB
  def lch: LCH
  def rgb: RGB

  def distanceTo(that: Color): Double = ColorDistance.ciede2000(this.lab, that.lab)

  def toCSS: String = rgb.toCSS
  def toHex: String = rgb.toHex
}


final case class RGB(r: Double, g: Double, b: Double) extends Color {
  @inline def ri = r.toInt
  @inline def gi = g.toInt
  @inline def bi = b.toInt
  override def toCSS = s"rgb($ri, $gi, $bi)"
  override def toHex = "%02X%02X%02X" format (ri, gi, bi)

  override def rgb = this
  override lazy val lab = {
    val lab = ColorConversion.rgbToLAB(r, g, b)
    LAB(lab(0), lab(1), lab(2))
  }
  override lazy val lch = lab.lch
}


final case class LAB(l: Double, a: Double, b: Double, hueHint: Double = PI) extends Color {
  def luminance = l

  private def isGray = a == 0 && b == 0

  override def lab = this
  override lazy val lch = {
    val chroma = sqrt(a * a + b * b)
    val hue = ((PI * 2) + atan2(b, a)) % (PI * 2)
    LCH(l, chroma, if (isGray) hueHint else hue)
  }
  override lazy val rgb: RGB = {
    val rgb = ColorConversion.labToRGB(l, a, b)
    RGB(rgb(0), rgb(1), rgb(2))
  }
}


final case class LCH(l: Double, c: Double, h: Double) extends Color {
  def luminance = l
  def chroma = c
  def hue = h

  override def lab = {
    val a = cos(h) * c
    val b = sin(h) * c
    LAB(l, a, b, hueHint = h)
  }
  override def lch = this
  override lazy val rgb: RGB = lab.rgb
}
