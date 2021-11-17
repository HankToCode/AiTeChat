package com.ycf.qianzhihe.app.utils

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 *   @author Gubr <a href="mailto:Gubr@foxmail.com">Gubr</a>
 **/
infix fun Number.sub(second: Number): BigDecimal {
    return BigDecimal(this.toString()).subtract(BigDecimal(second.toString()))
}

fun String.toBigDecimalStr(): String {
    return DecimalFormat().format(BigDecimal(this))
}

/**
 * 统一小数点后零位
 */
fun Double.format0(): String {
    return DecimalFormat("0").apply {
        this.maximumFractionDigits = 0
        this.groupingSize = 0
        this.roundingMode = RoundingMode.DOWN
    }.format(this.toBigDecimal())
}

/**
 * 统一小数点后1位
 */
fun Double.format1(): String {
    return DecimalFormat("0.0").apply {
        this.maximumFractionDigits = 1
        this.groupingSize = 0
        this.roundingMode = RoundingMode.DOWN
    }.format(this.toBigDecimal())
}

/**
 * 统一小数点后两位
 */
fun Double.format2(): String {
    return DecimalFormat("0.00").apply {
        this.maximumFractionDigits = 2
        this.groupingSize = 0
        this.roundingMode = RoundingMode.DOWN
    }.format(this.toBigDecimal())
}

/**
 * 统一小数点后两位
 */
fun Double.format2v2(): String {
    return DecimalFormat("0.##").apply {
        this.maximumFractionDigits = 2
        this.groupingSize = 0
        this.roundingMode = RoundingMode.DOWN
    }.format(this.toBigDecimal())
}


/**
 * 统一小数点后三位
 */
fun Double.format3(): String {
    return DecimalFormat("0.000").apply {
        this.maximumFractionDigits = 3
        this.groupingSize = 0
        this.roundingMode = RoundingMode.DOWN
    }.format(this.toBigDecimal())
}
/**
 * 统一CCQ小数点后位数
 */
fun Double.formatCCQ(): String {
    return DecimalFormat("0.0000").apply {
        this.maximumFractionDigits = 4
        this.groupingSize = 0
        this.roundingMode = RoundingMode.DOWN
    }.format(this.toBigDecimal())
}


/**
 * 统一小数点后八位
 */
fun Double.format8(): String {
    return DecimalFormat("0.00000000").apply {
        this.maximumFractionDigits = 8
        this.groupingSize = 0
        this.roundingMode = RoundingMode.DOWN
    }.format(this.toBigDecimal())
}

/**
 * 统一小数点后六位
 */
fun Double.format6(): String {
    return DecimalFormat("0.000000").apply {
        this.maximumFractionDigits = 6
        this.groupingSize = 0
        this.roundingMode = RoundingMode.DOWN
    }.format(this.toBigDecimal())
}

/**
 * 统一小数点后八位
 */
fun Double.format8v2(): String {
    return DecimalFormat("0.########").apply {
        this.maximumFractionDigits = 8
        this.groupingSize = 0
        this.roundingMode = RoundingMode.DOWN
    }.format(this.toBigDecimal())
}

/**
 * 统一小数点后四位
 */
fun Double.format4(): String {
    return DecimalFormat("0.0000").apply {
        this.maximumFractionDigits = 4
        this.groupingSize = 0
//        this.setRoundingMode(RoundingMode.DOWN)
    }.format(this.toBigDecimal())
}

//重载Number运算符 解决Java遗留Double计算精度问题
operator fun Number.times(value: Number): Number {
    val b1 = BigDecimal(this.toString())
    val b2 = BigDecimal(value.toString())
    return b1.multiply(b2).toDouble()
}

operator fun Number.plus(value: Number): Number {
    val b1 = BigDecimal(this.toString())
    val b2 = BigDecimal(value.toString())
    return b1.add(b2).toDouble()
}

operator fun Number.div(value: Number): Number {
    val b1 = BigDecimal(this.toString())
    val b2 = BigDecimal(value.toString())
    return b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP).toDouble()
}

operator fun Number.minus(value: Number): Number {
    val b1 = BigDecimal(this.toString())
    val b2 = BigDecimal(value.toString())
    return b1.subtract(b2).toDouble()
}
