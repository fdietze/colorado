package colorado

import java.lang.Math._

@inline final case class RGB(r: Double, g: Double, b: Double) extends Color {
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

  @inline final def toXYZ(sr: Double, sg: Double, sb: Double)(setXYZ:(Double, Double, Double) => Unit): Unit = {
    // accepts sRGB values r,g,b in range [0,1]
    // https://en.wikipedia.org/wiki/SRGB#The_reverse_transformation
    
    @inline def gamma_inverse(csrgb: Double) = {
      if (csrgb <= 0.04045)
        csrgb * 0.07739938080495357 // csrgb/12.92
      else
        pow((csrgb + 0.055) * 0.94786729857819905213, 2.4) // (csrgb+0.055/1.055)^2.4
    }

    val R = gamma_inverse(sr)
    val G = gamma_inverse(sg)
    val B = gamma_inverse(sb)

    val X = 41.23865632529916  *R + 35.75914909206253 *G +  18.045049120356364*B
    val Y = 21.26368216773238  *R + 71.51829818412506 *G +   7.218019648142546*B
    val Z =  1.9330620152483982*R + 11.919716364020843*G +  95.03725870054352 *B

    setXYZ(X,Y,Z)
  }

  @inline final def fromXYZ(X:Double, Y:Double, Z:Double)(setRGB:(Double, Double, Double) => Unit):Unit = {
    // linear rgb:
    var R = X * 3.2406 + Y * -1.5372 + Z * -0.4986
    var G = X * -0.9689 + Y * 1.8758 + Z * 0.0415
    var B = X * 0.0557 + Y * -0.2040 + Z * 1.0570

    @inline def csrgb(clin: Double) = {
      if (clin <= 0.0031308)
        12.92 * clin
      else
        1.055 * pow(clin, 0.4166666666666667) - 0.055
    }

    R = csrgb(R)
    G = csrgb(G)
    B = csrgb(B)

    val RClamped = R.max(0.0).min(1.0)
    val GClamped = G.max(0.0).min(1.0)
    val BClamped = B.max(0.0).min(1.0)

    setRGB(RClamped, GClamped, BClamped)
  }
}
