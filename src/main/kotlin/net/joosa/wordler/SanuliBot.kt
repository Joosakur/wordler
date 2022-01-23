package net.joosa.wordler

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

fun main() {
    val wordler = Wordler(GameVersion.SANULI)
    val driver = chrome()

    driver.get("https://sanuli.fi/")

    val keyboard = WebDriverWait(driver, Duration.ofSeconds(10))
        .until(ExpectedConditions.elementToBeClickable(By.className("keyboard")))

    Thread.sleep(10000)

    repeat(1000) start@{
        var guesses = GuessList()
        repeat(6) { i ->
            var nextGuess = wordler.getNextGuess(guesses)
            do {
                makeGuess(keyboard, nextGuess)
                val wordNotFound = driver
                    .findElements(By.cssSelector(".message"))
                    .any { it.text.startsWith("EI SANULISTALLA") }
                if(wordNotFound) {
                    wordler.blockWord(nextGuess)
                    nextGuess = wordler.getNextGuess(guesses)
                    val backspace = keyboard.findElement(By.className("keyboard-button-backspace"))
                    repeat(5) {
                        backspace.click()
                    }
                } else break
            } while (true)

            val output = getOutput(driver, i)
            if(output.all { it == CharState.CORRECT }) {
                Thread.sleep(500)
                keyboard.findElement(By.xpath("//button[text() = 'UUSI?']")).click()
                Thread.sleep(500)
                return@start
            }

            if(i == 5) {
                throw Error("Could not guess the word :(")
            } else {
                guesses = guesses.addGuess(Guess(nextGuess, output))
            }
        }
    }
}

private fun chrome(): WebDriver {
    WebDriverManager.chromedriver().setup()
    return ChromeDriver()
}

private fun makeGuess(keyboard: WebElement, word: String) {
    word.forEach { keyboard.findElement(By.xpath("//button[text() = '${it.uppercase()}']")).click() }
    keyboard.findElement(By.xpath("//button[text() = 'ARVAA']")).click()
    Thread.sleep(300)
}

private fun getOutput(driver: WebDriver, i: Int): List<CharState> {
    return driver
        .findElements(By.cssSelector(".tile"))
        .slice(5 * i until  5 * i + 5)
        .map { it.getAttribute("class").split(" ")[1] }
        .map { when(it) {
            "correct" -> CharState.CORRECT
            "present" -> CharState.PRESENT
            "absent" -> CharState.ABSENT
            else -> throw Error()
        } }
}
