# WORDLER

This app solves wordle puzzles by attempting to pick guess which on average will reduce the number of remaining possibilitiea the most.

This was made as an academic learning exercise. Obviously I do not recommend using it and ruining your fun of playing the game.

## Supported environments

This works with two different versions of the game: the original Wordle (English) and Sanuli (Finnish).

### Wordle

https://www.powerlanguage.co.uk/wordle/

The word lists were taken from source code. The words in the main list may be correct answers, while the words in the extra list are allowed as guesses but will never be the correct answer.

### Sanuli

https://sanuli.fi/

Sanuli uses WebAssembly and does not expose the wordlist very clearly. The source is however mentioned here: https://www.kotus.fi/nyt/kotus-vinkit/nykysuomen_sanalista_sanuli-sanapelin_taustalla.37769.news

Sanuli seems to be using somehow filtered version of that dictionary. Unlike Wordle it may not have two separate lists, but anyway I manually partitioned the list into higher and lower likelihood words with ad hoc criteria such as estimated commonness and avoiding proper nouns.

Still there are many cases in Finnish language where so many words are just one letter apart from each other (e.g. jahti, lahti, mahti, rahti, sahti, tahti, vahti) that the app needs to get lucky guessing the correct one within six attempts. This could possibly be solved by allowing to guess words that do not fit all the previous clues in order to rule out multiple answers at once even when 4/5 letters are already known.  

Unlike Wordle which only changes the secret word once per day, Sanuli lets you keep playing continuously. Therefore, I wrote a Selenium based bot which plays the game. You can see it play here: https://youtu.be/sDlXN-BxQwg
