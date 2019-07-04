package colorado

import java.lang.Math._

trait Color {
  def lab: LAB
  def hcl: HCL
  def rgb: RGB

  def distanceTo(that: Color): Double = ColorDistance.ciede2000(this.lab, that.lab)

  def toCSS: String = rgb.toCSS
  def toHex: String = rgb.toHex
}


final case class LAB(l: Double, a: Double, b: Double, hueHint: Double = PI) extends Color {
  def luminance = l

  private def isGray = a == 0 && b == 0

  override def lab = this
  override lazy val hcl = {
    val chroma = sqrt(a * a + b * b)
    val hue = ((PI * 2) + atan2(b, a)) % (PI * 2)
    HCL(if (isGray) hueHint else hue, chroma, l)
  }
  override lazy val rgb: RGB = {
    val rgb = ColorConversion.labToRGB(l, a, b)
    RGB(rgb(0), rgb(1), rgb(2))
  }
}


final case class HCL(h: Double, c: Double, l: Double) extends Color {
  def luminance = l
  def chroma = c
  def hue = h

  override def lab = {
    val a = cos(h) * c
    val b = sin(h) * c
    LAB(l, a, b, hueHint = h)
  }
  override def hcl = this
  override lazy val rgb: RGB = lab.rgb
}
