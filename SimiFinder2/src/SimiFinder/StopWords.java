package SimiFinder;

import java.io.*;
import java.util.*;

public class StopWords {
	private ArrayList<String> words;

	public StopWords(String fileLoc) {
		words = new ArrayList<String>();
		try {

			String line;
			BufferedReader br = new BufferedReader(new FileReader(fileLoc));

			while ((line = br.readLine()) != null) {
				if (!line.equals(" "))
					words.add(line);
			}
			br.close();
		} catch (Exception e) {
			System.out.println("No StopWordsFile");
		}
	}

	public boolean isStopWord(String str) {
		try {
			if (this.words.contains(str)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println("wort nicht gefunden: " + str);
			return false;
		}

	}
}