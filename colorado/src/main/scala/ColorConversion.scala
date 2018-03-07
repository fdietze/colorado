package colorado

import java.lang.Math._

object ColorConversion {

  val Xn = 95.047
  val Yn = 100.000
  val Zn = 108.883

  @inline final private def f(t: Double) = {
    val d3 = pow(6.0 / 29.0, 3)
    val d2times3 = 3 * pow(6.0 / 29.0, 2)
    if (t > d3)
      pow(t, 1.0 / 3.0)
    else
      t / d2times3 + 4.0 / 29.0
  }

  @inline final private def finv(t: Double) = {
    // delta = 6.0/29.0
    val t3 = pow(t, 3)
    if (t3 > 0.008856451679035631) // t^3 > delta^3
      t3
    else
      0.12841854934601665 * (t - 0.13793103448275862) //3*d^2(t-4/29)
  }

  @inline final private def csrgb(clin: Double) = {
    if (clin <= 0.0031308)
      12.92 * clin
    else
      1.055 * pow(clin, 0.4166666666666667) - 0.055
  }

  @inline final private def clinear(csrgb: Double) = {
    val a = 0.055
    if (csrgb <= 0.04045)
      csrgb * 0.07739938080495357
    else
      pow((csrgb + a) / (1 + a), 2.4)
  }

  @inline final def labToRGB(l: Double, a: Double, b: Double): Array[Double] = {
    // https://en.wikipedia.org/wiki/Lab_color_space#Reverse_transformation
    var Y = (l + 16) / 116
    var X = a / 500 + Y
    var Z = Y - b / 200

    X = Xn * finv(X)
    Y = Yn * finv(Y)
    Z = Zn * finv(Z)

    // https://en.wikipedia.org/wiki/SRGB#The_forward_transformation_.28CIE_xyY_or_CIE_XYZ_to_sRGB.29
    X = X / 100 //X from 0 to  95.047      (Observer = 2°, Illuminant = D65)
    Y = Y / 100 //Y from 0 to 100.000
    Z = Z / 100 //Z from 0 to 108.883

    // linear rgb:
    var R = X * 3.2406 + Y * -1.5372 + Z * -0.4986
    var G = X * -0.9689 + Y * 1.8758 + Z * 0.0415
    var B = X * 0.0557 + Y * -0.2040 + Z * 1.0570

    R = csrgb(R)
    G = csrgb(G)
    B = csrgb(B)

    val Ri = (R * 255).max(0).min(255)
    val Gi = (G * 255).max(0).min(255)
    val Bi = (B * 255).max(0).min(255)

    Array(Ri, Gi, Bi)
  }

  @inline final def rgbToLAB(ri: Double, gi: Double, bi: Double): Array[Double] = {
    // https://en.wikipedia.org/wiki/SRGB#The_reverse_transformation

    // sRGB:
    var R = ri / 255.0
    var G = gi / 255.0
    var B = bi / 255.0

    R = clinear(R)
    G = clinear(G)
    B = clinear(B)

    var X = R * 0.4124 + G * 0.3576 + B * 0.1805
    var Y = R * 0.2126 + G * 0.7152 + B * 0.0722
    var Z = R * 0.0193 + G * 0.1192 + B * 0.9505

    X = X * 100.0
    Y = Y * 100.0
    Z = Z * 100.0

    // https://en.wikipedia.org/wiki/Lab_color_space#Forward_transformation
    val l = 116.0 * f(Y / Yn) - 16.0
    val a = 500.0 * (f(X / Xn) - f(Y / Yn))
    val b = 200.0 * (f(Y / Yn) - f(Z / Zn))

    Array(l, a, b)
  }
}
