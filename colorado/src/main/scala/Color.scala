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


final case class RGB(r: Double, g: Double, b: Double) extends Color {
  @inline def ri = (r*255).toInt
  @inline def gi = (g*255).toInt
  @inline def bi = (b*255).toInt
  override def toCSS = s"rgb($ri, $gi, $bi)"
  override def toHex = "#%02X%02X%02X" format (ri, gi, bi)

  override def rgb = this
  override lazy val lab = {
    val lab = ColorConversion.rgbToLAB(r, g, b)
    LAB(lab(0), lab(1), lab(2))
  }
  override lazy val hcl = lab.hcl
}

object RGB {
  def apply(r:Int, g:Int, b:Int):RGB = RGB(r / 255.0, g / 255.0, b / 255.0)
  def apply(s:String):RGB = {
    import Integer.parseInt

    if(s.length == 6) {
      val rs = s.substring(0,2)
      val gs = s.substring(2,4)
      val bs = s.substring(4,6)
      RGB(parseInt(rs, 16), parseInt(gs, 16), parseInt(bs, 16))
    } else if(s.length == 7) {
      val rs = s.substring(1,3)
      val gs = s.substring(3,5)
      val bs = s.substring(5,7)
      RGB(parseInt(rs, 16), parseInt(gs, 16), parseInt(bs, 16))
    } else {
      throw new IllegalArgumentException("Use RRGGBB or #RRGGBB")
    }
  }
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
