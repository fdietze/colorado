package colorado

import java.lang.Math._

object ColorDistance {

  def ciede2000(col1: LAB, col2: LAB): Double = ciede2000(col1.l, col1.a, col1.b, col2.l, col2.a, col2.b)
  def ciede2000(lab1l: Double, lab1a: Double, lab1b: Double, lab2l: Double, lab2a: Double, lab2b: Double): Double = {
    // ported from: https://github.com/THEjoezack/ColorMine/blob/master/ColorMine/ColorSpaces/Comparisons/CieDe2000Comparison.cs

    // Set weighting factors to 1
    val k_l = 1.0
    val k_c = 1.0
    val k_h = 1.0

    // Calculate Cprime1, Cprime2, Cabbar
    val c_star_1_ab = sqrt(lab1a * lab1a + lab1b * lab1b)
    val c_star_2_ab = sqrt(lab2a * lab2a + lab2b * lab2b)
    val c_star_average_ab = (c_star_1_ab + c_star_2_ab) / 2.0

    var c_star_average_ab_pot7 = c_star_average_ab * c_star_average_ab * c_star_average_ab
    c_star_average_ab_pot7 *= c_star_average_ab_pot7 * c_star_average_ab

    val g = 0.5 * (1.0 - sqrt(c_star_average_ab_pot7 / (c_star_average_ab_pot7 + 6103515625.0))) //25^7
    val a1_prime = (1.0 + g) * lab1a
    val a2_prime = (1.0 + g) * lab2a

    val c_prime_1 = sqrt(a1_prime * a1_prime + lab1b * lab1b)
    val c_prime_2 = sqrt(a2_prime * a2_prime + lab2b * lab2b)
    // Angles in Degree.
    val h_prime_1 = ((atan2(lab1b, a1_prime) * 180.0 / PI) + 360.0) % 360.0
    val h_prime_2 = ((atan2(lab2b, a2_prime) * 180.0 / PI) + 360.0) % 360.0

    val delta_l_prime = lab2l - lab1l
    val delta_c_prime = c_prime_2 - c_prime_1

    val h_bar = (h_prime_1 - h_prime_2).abs
    var delta_h_prime = if (c_prime_1 * c_prime_2 == 0.0) {
      0.0
    } else {
      if (h_bar <= 180.0) {
        h_prime_2 - h_prime_1
      } else if (h_bar > 180.0 && h_prime_2 <= h_prime_1) {
        h_prime_2 - h_prime_1 + 360.0
      } else {
        h_prime_2 - h_prime_1 - 360.0
      }
    }
    delta_h_prime = 2.0 * sqrt(c_prime_1 * c_prime_2) * sin(delta_h_prime * PI / 360.0)

    // Calculate CIEDE2000
    val l_prime_average = (lab1l + lab2l) / 2.0
    val c_prime_average = (c_prime_1 + c_prime_2) / 2.0

    // Calculate h_prime_average

    val h_prime_average =
      if (c_prime_1 * c_prime_2 == 0.0) {
        0.0
      } else {
        if (h_bar <= 180.0) {
          (h_prime_1 + h_prime_2) / 2.0
        } else if (h_bar > 180.0 && (h_prime_1 + h_prime_2) < 360.0) {
          (h_prime_1 + h_prime_2 + 360.0) / 2.0
        } else {
          (h_prime_1 + h_prime_2 - 360.0) / 2.0
        }
      }
    var l_prime_average_minus_50_square = l_prime_average - 50.0
    l_prime_average_minus_50_square *= l_prime_average_minus_50_square

    val s_l = 1.0 +
      ((0.015 * l_prime_average_minus_50_square) /
        sqrt(20.0 + l_prime_average_minus_50_square))
    val s_c = 1.0 + 0.045 * c_prime_average
    val t = 1.0 - 0.17 * cos((h_prime_average - 30.0).toRadians) +
      0.24 * cos((h_prime_average * 2.0).toRadians) +
      0.32 * cos((h_prime_average * 3.0 + 6.0).toRadians) -
      0.2 * cos((h_prime_average * 4.0 - 63.0).toRadians)
    val s_h = 1.0 + 0.015 * t * c_prime_average
    var h_prime_average_minus_275_div_25_square = (h_prime_average - 275.0) / 25.0
    h_prime_average_minus_275_div_25_square *= h_prime_average_minus_275_div_25_square
    val delta_theta = 30.0 * exp(-h_prime_average_minus_275_div_25_square)

    var c_prime_average_pot_7 = c_prime_average * c_prime_average * c_prime_average
    c_prime_average_pot_7 *= c_prime_average_pot_7 * c_prime_average
    val r_c = 2.0 * sqrt(c_prime_average_pot_7 / (c_prime_average_pot_7 + 6103515625.0))

    val r_t = -sin((2.0 * delta_theta).toRadians) * r_c

    val delta_l_prime_div_k_l_s_l = delta_l_prime / (s_l * k_l)
    val delta_c_prime_div_k_c_s_c = delta_c_prime / (s_c * k_c)
    val delta_h_prime_div_k_h_s_h = delta_h_prime / (s_h * k_h)

    val ciede2000 = sqrt(delta_l_prime_div_k_l_s_l * delta_l_prime_div_k_l_s_l +
      delta_c_prime_div_k_c_s_c * delta_c_prime_div_k_c_s_c +
      delta_h_prime_div_k_h_s_h * delta_h_prime_div_k_h_s_h +
      r_t * delta_c_prime_div_k_c_s_c * delta_h_prime_div_k_h_s_h)

    ciede2000
  }

}
