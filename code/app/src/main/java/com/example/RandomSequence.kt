package com.example.a301_a1
import kotlin.math.pow


class RandomSequence {
    val lengths = arrayListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

    val gameHistory = arrayListOf<Int>()

    val guessHistory = arrayListOf<Int>()

    val evalHistory = arrayListOf<String>()

    fun generateSequence(length: Int): Int {
        val base = 10.0

        val limit = base.pow(length).toInt()

        val lowerLimit = base.pow(length -1).toInt()

        val sequence = (lowerLimit until limit).random()

        return sequence
    }

    fun storeGuess(sequence: Int, guess: Int, eval: String){
        gameHistory.add(sequence)
        guessHistory.add(guess)
        evalHistory.add(eval)

    }

    fun evalAndStoreGuess(sequence: Int, guess: Int): String {

        var eval = "Incorrect"

        if (sequence == guess){
            eval = "Correct!"
        }

        storeGuess(sequence, guess, eval)

        return eval
    }
}