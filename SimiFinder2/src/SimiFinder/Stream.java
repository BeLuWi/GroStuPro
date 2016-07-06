package SimiFinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

class Stream {
	String name;
	ArrayList<Author> authors;
	ArrayList<AuthorWithCounter> coAuthors;
	ArrayList<StreamWithCounter> commonStreams;
	Counter coAuthorsCount, entryCount;

	Stream(String str) {
		this.name = str;
		this.authors = new ArrayList<Author>();
		this.coAuthors = new ArrayList<AuthorWithCounter>();
		this.commonStreams = new ArrayList<StreamWithCounter>();
		this.entryCount = new Counter();
		this.coAuthorsCount = new Counter();

	}

	void addAuthorToStream(Author author) {
		// addAuthor fuegt author nur hinzu, falls er noch nicht existiert. Das
		// alte Element muss nicht ueberschrieben werden, da es sich nur um
		// einen Pointer handelt.
		this.entryCount.inc();
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
		this.coAuthorsCount.inc();
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
		if (method.contains("_authors")) {
			authors = true;
		}
		if (method.contains("_asCA")) {
			asCA = true;
		}
		if (method.contains("_coauthors")) {
			coauthors = true;
		}
		if (method.contains("_coAsCA")) {
			coAsCA = true;
		}
		boolean found = false;
		if (authors || asCA) {
			found = false;
			for (Author a : this.authors) {
				if (authors) {
					for (StreamWithCounter glblStream : a.streamsAsAuthor) {

						found = false;
						if (glblStream.stream.entryCount.getVal() > 100) {
							StreamWithCounter globalStream = new StreamWithCounter(glblStream.stream);
							globalStream.counter.copy(glblStream.counter);
							for (StreamWithCounter localStream : tmpStreams) {

								if (globalStream.stream.name.equals(localStream.stream.name)&& !globalStream.stream.name.equals(this.name)) {

									localStream.counter.addDVal(globalStream.counter.getDVal());
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
						if (glblStream.stream.entryCount.getVal() > 100) {
							StreamWithCounter globalStream = new StreamWithCounter(
									glblStream.stream);
							globalStream.counter.copy(glblStream.counter);
							for (StreamWithCounter localStream : tmpStreams) {

								if (globalStream.stream.name.equals(localStream.stream.name)&& !globalStream.stream.name.equals(this.name)) {

									localStream.counter.addDVal2(globalStream.counter.getDVal2());
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
			if (authors) {
				for (StreamWithCounter s : tmpStreams) {
					s.counter.setDVal(s.counter.getDVal()/ (double) s.stream.entryCount.getVal());
				}
			}
			if (asCA) {
				// consider appearances of each author as a coauthor
				for (StreamWithCounter s : tmpStreams) {
					s.counter.addDVal(s.counter.getDVal2()/ (double) s.stream.coAuthorsCount.getVal());
				}
			}

		}
		if (coauthors || coAsCA) {
			found = false;
			for (AuthorWithCounter a : this.coAuthors) {
				if (coauthors) {
					for (StreamWithCounter glblStream : a.author.streamsAsAuthor) {
						found = false;
						if (glblStream.stream.entryCount.getVal() > 100) {
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
						if (glblStream.stream.entryCount.getVal() > 100) {
							StreamWithCounter globalStream = new StreamWithCounter(
									glblStream.stream);
							globalStream.counter.copy(glblStream.counter);
							for (StreamWithCounter localStream : tmpStreams) {

								if (globalStream.stream.name
										.equals(localStream.stream.name)
										&& !globalStream.stream.name
												.equals(this.name)) {

									localStream.counter
											.addDVal2(globalStream.counter
													.getDVal2());
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
							/ (double) s.stream.entryCount.getVal());
				}
			}
			if (coAsCA) {
				// consider appearances of each coauthor as a coauthor
				for (StreamWithCounter s : tmpStreams) {
					s.counter.addDVal(s.counter.getDVal2()
							/ (double) s.stream.coAuthorsCount.getVal());
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
				if (i < 40) {
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