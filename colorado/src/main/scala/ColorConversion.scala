package colorado

import java.lang.Math._

object ColorConversion {

  @inline final private def Xn = 95.047
  @inline final private def Yn = 100.000
  @inline final private def Zn = 108.883

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

  @inline final private def gamma_inverse(csrgb: Double) = {
    if (csrgb <= 0.04045)
      csrgb * 0.07739938080495357 // csrgb/12.92
    else
      pow((csrgb + 0.055) * 0.94786729857819905213, 2.4) // (csrgb+0.055/1.055)^2.4
  }

  final def labToRGB(l: Double, a: Double, b: Double): Array[Double] = {
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

    val Ri = R.max(0.0).min(1.0)
    val Gi = G.max(0.0).min(1.0)
    val Bi = B.max(0.0).min(1.0)

    Array(Ri, Gi, Bi)
  }

  final def rgbToLAB(sr: Double, sg: Double, sb: Double): Array[Double] = {
    // accepts sRGB values r,g,b in range [0,1]
    // https://en.wikipedia.org/wiki/SRGB#The_reverse_transformation

    val R = gamma_inverse(sr)
    val G = gamma_inverse(sg)
    val B = gamma_inverse(sb)

    val X = 41.23865632529916  *R + 35.75914909206253 *G +  18.045049120356364*B
    val Y = 21.26368216773238  *R + 71.51829818412506 *G +   7.218019648142546*B
    val Z =  1.9330620152483982*R + 11.919716364020843*G +  95.03725870054352 *B

    // https://en.wikipedia.org/wiki/Lab_color_space#Forward_transformation
    val l = 116.0 * f(Y / Yn) - 16.0
    val a = 500.0 * (f(X / Xn) - f(Y / Yn))
    val b = 200.0 * (f(Y / Yn) - f(Z / Zn))

    Array(l, a, b)
  }



  // https://www.osapublishing.org/oe/abstract.cfm?uri=oe-25-13-15131
  // https://observablehq.com/@jrus/jzazbz
  // https://github.com/nschloe/colorio/blob/master/colorio/jzazbz.py#L52
  final def rgbToJzAzBz(sr: Double, sg: Double, sb: Double): Array[Double] = {
    // accepts sRGB values r,g,b in range [0,1]
    // https://en.wikipedia.org/wiki/SRGB#The_reverse_transformation

    val R = gamma_inverse(sr)
    val G = gamma_inverse(sg)
    val B = gamma_inverse(sb)

    val X = (R * 41.24 + G * 35.76 + B * 18.05)
    val Y = (R * 21.26 + G * 71.52 + B * 07.22)
    val Z = (R * 01.93 + G * 11.92 + B * 95.05)

    // perceptual quantizer
    @inline def PQ(X:Double) = {
      val XX = Math.pow(X*1e-4, 0.1593017578125)
      Math.pow(
        (0.8359375 + 18.8515625*XX) / (1 + 18.6875*XX),
        134.034375
      )
    }

    val Lp = PQ(0.674207838*X + 0.382799340*Y - 0.047570458*Z)
    val Mp = PQ(0.149284160*X + 0.739628340*Y + 0.083327300*Z)
    val Sp = PQ(0.070941080*X + 0.174768000*Y + 0.670970020*Z)
    val Iz = 0.5 * (Lp + Mp)
    val az = 3.524000*Lp - 4.066708*Mp + 0.542708*Sp
    val bz = 0.199076*Lp + 1.096799*Mp - 1.295875*Sp
    val Jz = (0.44 * Iz) / (1 - 0.56*Iz) - 1.6295499532821566e-11

    Array(Jz, az, bz)
  }
}
