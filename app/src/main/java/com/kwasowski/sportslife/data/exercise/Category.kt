package com.kwasowski.sportslife.data.exercise

import android.content.Context
import com.kwasowski.sportslife.R

enum class Category {
    AEROBICS,
    AMERICAN_SOCCER,
    BASKETBALL,
    BOXING,
    CALISTHENICS,
    CYCLING,
    FOOTBALL,
    GYMNASTICS,
    HANDBALL,
    ICE_HOKEY,
    MMA,
    RUNNING,
    RUGBY,
    SQUASH,
    TABLE_TENNIS,
    TENNIS,
    VOLLEYBALL,
    WEIGHT_TRAINING,
    YOGA;

    companion object {
        fun fromString(input: String?, categoriesResourcesList: Array<String>): Category? {
            val index = categoriesResourcesList.indexOfFirst { it.equals(input, ignoreCase = true) }
            return if (index != -1) {
                values()[index]
            } else {
                null
            }
        }

        fun toResourceString(context: Context, category: String): String {
            val enumCategory = Category.valueOf(category.uppercase())
            return context.resources.getString(enumCategory.getResourceId())
        }
    }

    private fun getResourceId(): Int {
        return when (this) {
            AEROBICS -> R.string.aerobics
            AMERICAN_SOCCER -> R.string.american_soccer
            BASKETBALL -> R.string.basketball
            BOXING -> R.string.boxing
            CALISTHENICS -> R.string.calisthenics
            CYCLING -> R.string.cycling
            FOOTBALL -> R.string.football
            GYMNASTICS -> R.string.gymnastics
            HANDBALL -> R.string.handball
            ICE_HOKEY -> R.string.ice_hokey
            MMA -> R.string.mma
            RUNNING -> R.string.running
            RUGBY -> R.string.rugby
            SQUASH -> R.string.squash
            TABLE_TENNIS -> R.string.table_tennis
            TENNIS -> R.string.tennis
            VOLLEYBALL -> R.string.volleyball
            WEIGHT_TRAINING -> R.string.weight_training
            YOGA -> R.string.yoga
        }
    }
}
