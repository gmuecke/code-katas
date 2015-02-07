import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

public class AnagramFinder {

	public static void main(String[] args) throws Exception {
		AnagramFinder af = new AnagramFinder("documenting", new File(
				"wordlist.txt"));
		af.findAnagrams();
	}

	private final String wordToAnagram;
	private final File dictionary;

	public AnagramFinder(String wordToAnagram, File dictionary) {
		this.wordToAnagram = wordToAnagram;
		this.dictionary = dictionary;
	}

	private void findAnagrams() throws Exception {
		final char[] rootAnagram = getMasterCharacters();

		long start = System.nanoTime();

		final char[][] wordList = readWordsFromFile(dictionary,
				uniqueCharacters(rootAnagram)).toArray(new char[0][]);

		System.out.printf("Read %s candidates in %s ms\n", wordList.length,
				(System.nanoTime() - start) / 1_000_000);

		long startAlgorithm = System.nanoTime();

		int anagramsFound = 0;
		int iterations = 0;

		final int len = wordList.length;
		for (int i = 0; i < len; i++) {
			final char[] word1 = wordList[i];
			final int startPosition = i + 1;
			for (int j = startPosition; j < len; j++) {
				iterations++;
				final char[] word2 = wordList[j];
				if (isAnagramOf(rootAnagram, word1, word2)) {
					anagramsFound++;
				}
			}
		}

		long end = System.nanoTime();
		System.out.printf("Found %s Anagrams in %s ms (%s iterations) \n",
				anagramsFound*2, (end - startAlgorithm) / 1_000_000, iterations);
		System.out.printf("Total time %s ms \n", (end - start) / 1_000_000);
	}

	private Collection<char[]> readWordsFromFile(File dictionary, char[] filter)
			throws IOException {
		try (Scanner s = new Scanner(dictionary).useDelimiter("\\n")) {
			Deque<char[]> wordList = new ArrayDeque<>(10000);
			while (s.hasNext()) {
				char[] charactersOfWord = s.next().trim().toCharArray();
				if (isAnagramCandidate(charactersOfWord, filter)) {
					wordList.add(charactersOfWord);
				}
			}
			return wordList;
		}

	}

	private boolean isAnagramOf(final char[] rootAnagram, final char[] word1,
			final char[] word2) {
		if (isAnagram(rootAnagram, word1, word2)) {
			System.out.printf("%s = %s + %s\n", wordToAnagram,
					String.valueOf(word1), String.valueOf(word2));
			System.out.printf("%s = %s + %s\n", wordToAnagram,
					String.valueOf(word2), String.valueOf(word1));
			return true;
		}
		return false;
	}

	private boolean isAnagram(final char[] rootAnagram, final char[] word1,
			final char[] word2) {
		return combinedWordLength(word1, word2) == rootAnagram.length
				&& Arrays.equals(rootAnagram, joinCharsSorted(word1, word2));
	}

	private boolean isAnagramCandidate(char[] characters, char[] superset) {
		if (characters.length >= superset.length) {
			return false;
		}
		for (int i = 0, len = characters.length; i < len; i++) {
			if (!contains(superset, characters[i])) {
				return false;
			}
		}
		return true;
	}

	private boolean contains(char[] set, char c) {
		for (int i = 0, len = set.length; i < len; i++) {
			if (c == set[i]) {
				return true;
			}
		}
		return false;
	}

	private char[] getMasterCharacters() {
		final char[] rootAnagram = wordToAnagram.toCharArray();
		Arrays.sort(rootAnagram);
		return rootAnagram;
	}

	private char[] uniqueCharacters(char[] characters) {
		Set<Character> uniqueChars = new LinkedHashSet<>();
		for (char c : characters) {
			uniqueChars.add(c);
		}
		char[] result = new char[uniqueChars.size()];
		int pos = 0;
		for (Character c : uniqueChars) {
			result[pos++] = c.charValue();
		}
		return result;
	}

	private int combinedWordLength(char[] word1, char[] word2) {
		return word1.length + word2.length;
	}

	private char[] joinCharsSorted(char[] word1, char[] word2) {
		char[] result = new char[word1.length + word2.length];
		copyChars(word2, result, copyChars(word1, result, 0));
		Arrays.sort(result);
		return result;
	}

	private int copyChars(char[] source, char[] target, int pos) {
		for (int i = 0, len = source.length; i < len; i++) {
			target[pos++] = source[i];
		}
		return pos;
	}

}
