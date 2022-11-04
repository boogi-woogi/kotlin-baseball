package baseball

import camp.nextstep.edu.missionutils.Console

object BaseballGameReferee {
    fun processBaseballGame() {
        Computer.generateNewRandomNumbers()
        WordPrinter.printGameStart()

        while (true) {
            val guessedNumbers = User.guessNumber() ?: return
            lateinit var curGameStatus: GameStatus

            try {
                curGameStatus = decideEachTurn(guessedNumbers)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                return
            }
            when (curGameStatus) {
                GameStatus.TERMINATE -> break
                GameStatus.NEW_GAME -> Computer.generateNewRandomNumbers()
                else -> continue
            }
        }
    }

    private fun decideEachTurn(userInput: ArrayList<Int>): GameStatus {
        val (strikeCount, ballCount) = arrayOf(calcStrikeCount(userInput), calcBallCount(userInput))

        if (strikeCount + ballCount == 0) {
            WordPrinter.printNothing()
            return GameStatus.CONTINUE
        }

        when {
            strikeCount == 3 -> {
                WordPrinter.printStrikeCount(strikeCount)
                WordPrinter.printGameEnd()
                return checkPlayAgain()
            }
            ballCount > 0 && strikeCount > 0 -> {
                WordPrinter.printBallCount(ballCount)
                WordPrinter.printStrikeCount(strikeCount)
            }
            ballCount > 0 -> WordPrinter.printBallCount(ballCount)
            strikeCount > 0 -> WordPrinter.printStrikeCount(strikeCount)
        }
        WordPrinter.printNewLine()
        return GameStatus.CONTINUE
    }

    private fun checkPlayAgain(): GameStatus {
        return when (Console.readLine()) {
            "1" -> GameStatus.NEW_GAME
            "2" -> GameStatus.TERMINATE
            else -> throw IllegalArgumentException("잘못된 입력입니다.")
        }
    }

    fun calcStrikeCount(userInput: ArrayList<Int>): Int {
        var strikeCount = 0

        userInput.forEachIndexed { index, eachNum ->
            if (Computer.randomNumbers[index] == eachNum) {
                strikeCount++
            }
        }
        return strikeCount
    }

    fun calcBallCount(userInput: ArrayList<Int>): Int {
        var ballCount = 0

        userInput.forEachIndexed { userIndex, userNum ->
            ballCount += Computer.randomNumbers.filterIndexed { computerIndex, _ -> computerIndex != userIndex }
                .filter { computerNum -> computerNum == userNum }.size
        }
        return ballCount
    }

    fun checkException(userInput: String) {
        var isValid = true

        when {
            userInput.length != 3 -> isValid = false
            !userInput.isNumeric() -> isValid = false
            !userInput.hasOverlappedNumbers() -> isValid = false
            userInput.contains('0') -> isValid = false
        }

        if (!isValid) {
            throw IllegalArgumentException("입력 오류입니다.")
        }
    }

    private fun String.isNumeric(): Boolean {
        return this.chars().allMatch { eachChar -> Character.isDigit(eachChar) }
    }

    private fun String.hasOverlappedNumbers(): Boolean {
        val usedNumberSet = mutableSetOf<Char>()

        this.forEach { eachNum ->
            if (usedNumberSet.contains(eachNum)) {
                return false
            }
            usedNumberSet.add(eachNum)
        }
        return true
    }
}