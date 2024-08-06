package dev.jpires.carview.model.data

enum class ThemeMode {
    SYSTEM, LIGHT, DARK;

    override fun toString(): String {
        return when (this) {
            SYSTEM -> "System"
            LIGHT -> "Light"
            DARK -> "Dark"
        }
    }

    companion object {
        fun fromInt(value: Int) = entries.first { it.ordinal == value }
    }
}