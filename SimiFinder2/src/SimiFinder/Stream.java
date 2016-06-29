package SimiFinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

class Stream {
	String name;
	String type;
	ArrayList<Author> authors;
	ArrayList<AuthorWithCounter> coAuthors;
	ArrayList<StreamWithCounter> commonStreams;
	int coAuthorsCount;
	int entryCount;

	Stream(String str, boolean journal) {
		this.name = str;
		this.authors = new ArrayList<Author>();
		this.coAuthors = new ArrayList<AuthorWithCounter>();
		this.commonStreams = new ArrayList<StreamWithCounter>();
		this.entryCount = 0;
		this.coAuthorsCount = 0;
		if (journal) {
			this.type = "journals/";
		} else {
			this.type = "conf/";
		}
	}

	void addAuthorToStream(Author author) {
		// addAuthor fuegt author nur hinzu, falls er noch nicht existiert. Das
		// alte Element muss nicht ueberschrieben werden, da es sich nur um
		// einen Pointer handelt.
		this.entryCount++;
		boolean found = false;

		if (!authors.isEmpty()) {
			for (Author a : authors) {
				if (a.name.equals(author.name)) {
					found = true;

					break;
				}
			}
		}
		if (!found) {
			this.authors.add(author);

		}
	}

	void addCoAuthorToStream(Author author) {
		this.coAuthorsCount++;
		boolean found = false;
		if (!coAuthors.isEmpty()) {
			for (AuthorWithCounter a : coAuthors) {
				if (a.author.name.equals(author.name)) {
					found = true;
					break;
				}
			}
		}
		if (!found) {
			this.coAuthors.add(new AuthorWithCounter(author));
			
		}

	}

	void findSimiStreams(String method) {
		// baut commonStreams
		ArrayList<StreamWithCounter> tmpStreams = new ArrayList<StreamWithCounter>();
		boolean authors = false, asCA = false, coauthors = false, coAsCA = false;
		if (method.contains("_authors"))	{authors = true;}
		if (method.contains("_asCA"))		{asCA = true;}
		if (method.contains("_coauthors")) 	{coauthors = true;}
		if (method.contains("_coAsCa")) 	{coAsCA = true;}
		boolean found = false;
		if (authors || asCA) {

			found = false;
			for (Author a : this.authors) {
				if (method.contains("_authors")) {
					for (StreamWithCounter glblStream : a.streamsAsAuthor) {

						found = false;
						if (glblStream.stream.entryCount > 100) {
							StreamWithCounter globalStream = new StreamWithCounter(
									glblStream.stream);
							globalStream.counter.copy(glblStream.counter);

							// iteriert ueber alle Streams, in denen der Author
							// als
							// Hauptautor geschrieben hat und tut das fuer jeden
							// Hauptautor
							// der im aktuellen Stream geschrieben hat.
							for (StreamWithCounter localStream : tmpStreams) {
								// localStream ist der Stream, der bereits
								// vorgekommen
								// ist.

								if (globalStream.stream.name
										.equals(localStream.stream.name)
										&& !globalStream.stream.name
												.equals(this.name)) {
									// wenn der Stream schon vorgekommen ist,
									// wird
									// sein
									// counter, um den bereits vorhandenen
									// Counter
									// erhoeht
									localStream.counter
											.addDVal(globalStream.counter
													.getDVal());
									found = true;
									break;
								}
							}
							if (!found
									&& !globalStream.stream.name
											.equals(this.name)) {
								tmpStreams.add(globalStream);
							}
						}
					}
				}
				if (asCA) {
					for (StreamWithCounter glblStream : a.streamsAsCoAuthor) {
						found = false;
						if (glblStream.stream.entryCount > 100) {
							StreamWithCounter globalStream = new StreamWithCounter(
									glblStream.stream);
							for (StreamWithCounter localStream : tmpStreams) {

								if (globalStream.stream.name
										.equals(localStream.stream.name)
										&& !globalStream.stream.name
												.equals(this.name)) {

									localStream.counter
											.addDVal2((globalStream.counter
													.getDVal2() / 2));
									found = true;
									break;
								}
							}
							if (!found
									&& !globalStream.stream.name
											.equals(this.name)) {
								tmpStreams.add(globalStream);
							}
						}
					}
				}

			}
			if (authors)
				for (StreamWithCounter s : tmpStreams) {
					s.counter.setDVal(s.counter.getDVal()
							/ (double) s.stream.entryCount);
				}
			if (asCA) {
				// consider appearances of each author as a coauthor
				for (StreamWithCounter s : tmpStreams) {
					s.counter.addDVal(s.counter.getDVal2()
							/ (double) s.stream.coAuthorsCount);
				}
			}

		}
		if (coauthors || coAsCA) {
			found = false;
			for (AuthorWithCounter a : this.coAuthors) {
				if (coauthors) {
					for (StreamWithCounter glblStream : a.author.streamsAsAuthor) {
						found = false;
						if (glblStream.stream.entryCount > 100) {
							StreamWithCounter globalStream = new StreamWithCounter(
									glblStream.stream);
							globalStream.counter.copy(glblStream.counter);
							for (StreamWithCounter localStream : tmpStreams) {

								if (globalStream.stream.name
										.equals(localStream.stream.name)
										&& !globalStream.stream.name
												.equals(this.name)) {

									localStream.counter
											.addDVal(globalStream.counter
													.getDVal());
									found = true;
									break;
								}
							}
							if (!found
									&& !globalStream.stream.name
											.equals(this.name)) {
								tmpStreams.add(globalStream);
							}
						}
					}
				}
				if (coAsCA) {
					for (StreamWithCounter glblStream : a.author.streamsAsCoAuthor) {
						found = false;
						if (glblStream.stream.entryCount > 100) {
							StreamWithCounter globalStream = new StreamWithCounter(
									glblStream.stream);
							for (StreamWithCounter localStream : tmpStreams) {

								if (globalStream.stream.name
										.equals(localStream.stream.name)
										&& !globalStream.stream.name
												.equals(this.name)) {

									localStream.counter
											.addDVal2((globalStream.counter
													.getDVal2() / 2));
									found = true;
									break;
								}
							}
							if (!found
									&& !globalStream.stream.name
											.equals(this.name)) {
								tmpStreams.add(globalStream);
							}
						}
					}
				}

			}
			if (coauthors) {
				for (StreamWithCounter s : tmpStreams) {
					s.counter.setDVal(s.counter.getDVal()
							/ (double) s.stream.entryCount);
				}
			}
			if (coAsCA) {
				// consider appearances of each coauthor as a coauthor
				for (StreamWithCounter s : tmpStreams) {
					s.counter.addDVal(s.counter.getDVal2()
							/ (double) s.stream.coAuthorsCount);
				}
			}
		}
		try {
			Collections.sort(tmpStreams, new Comparator<StreamWithCounter>() {

				@Override
				public int compare(StreamWithCounter str1,
						StreamWithCounter str2) {

					if (str1.counter.getDVal() > str2.counter.getDVal())
						return -1;
					else if (str1.counter.getDVal() == str2.counter.getDVal())
						return 0;
					else
						return 1;
				}
			});
			int i = 0;
			for (StreamWithCounter counted : tmpStreams) {
				if (i < 10) {
					commonStreams.add(counted);
					i++;
				}
			}
			tmpStreams.clear();
		} catch (Exception e) {
			System.out.println("Fuer " + name + " ist tmpStreams leer");
		}

	}

}

class StreamWithCounter {
	Counter counter;
	Stream stream;

	StreamWithCounter(Stream s) {
		this.counter = new Counter();
		this.stream = s;
	}

}