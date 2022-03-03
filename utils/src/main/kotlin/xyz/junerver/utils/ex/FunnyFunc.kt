package xyz.junerver.utils.ex

/**
 * Description:利用 Kotlin 语法糖写出的各种有趣的函数
 * @author Junerver
 * date: 2022/3/2-7:58
 * Email: junerver@gmail.com
 * Version: v1.0
 */
//region x 替换乘法
infix fun Int.x(a: Int): Int {
    return this * a
}
infix fun Int.x(a: Long): Long {
    return this * a
}
infix fun Int.x(a: Float): Float {
    return this * a
}
infix fun Int.x(a: Double): Double {
    return this * a
}

infix fun Long.x(a: Int): Long {
    return this * a
}
infix fun Long.x(a: Long): Long {
    return this * a
}
infix fun Long.x(a: Float): Float {
    return this * a
}
infix fun Long.x(a: Double): Double {
    return this * a
}

infix fun Float.x(a: Int): Float {
    return this * a
}
infix fun Float.x(a: Long): Float {
    return this * a
}
infix fun Float.x(a: Float): Float {
    return this * a
}
infix fun Float.x(a: Double): Double {
    return this * a
}

infix fun Double.x(a: Int): Double {
    return this * a
}
infix fun Double.x(a: Long): Double {
    return this * a
}
infix fun Double.x(a: Float): Double {
    return this * a
}
infix fun Double.x(a: Double): Double {
    return this * a
}
//endregion


