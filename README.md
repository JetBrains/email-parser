# email-parser
Clone project via git and change directory to `email-parser`.

### To parse .eml file:
Enter `gradlew run -PemlFile="path"` in the console, where `path` is path to eml file.

### To get quote's header suggestions for two EMLs, distance between them and alignment:
Enter `gradlew runQHS -Ppaths="path1;path2"` in the console, where `path1` and `path2` are paths to the eml files separated by semicolon.

### To get distances and alignments between quotes' headers:
Enter `gradlew runEditDistance -Pheaders="id1[,id2]"` in the console.
After equals sign you should place indices of desired headers to be used. Indices must be separated by commas.

- 0 - "15 апреля 2016 г., 20:26 пользователь Someone Important <mmm@jjj.com> написал:"
- 1 - "29 февраля 2016 г., 17:56 пользователь Someone Important <mdrefha@juy.com> написал:"
- 2 - "Воскресенье, 13 марта 2016, 6:55 +10:00 от Павел Жук < ppp@zzz.com >:"
- 3 - "Воскресенье, 13 марта 2016, 7:09 +10:00 от Павел Жук <lkjk@jl.com>:"
- 4 - "On 04/15/2016 05:08 PM, Павел Жук wrote:"
- 5 - "El 02/04/16 a las 19:03, Zhuk Pavel escribió:"
- 6 - "On Mar 1, 2016, at 13:35, Павел Жук <pp@g.c> wrote:"
- 7 - "On Thu, Mar 31, 2016 at 4:04 PM, Zhuk Pavel <y-k@jjjjj.com> wrote:"
- 8 - "it is a very long text that isn't a really header"

To use all of them enter `gradlew runEditDistance` without any parameters.

### To run clustering example:
Enter `gradlew runClustering`. It is an example so it has only one datasource - `test.arff`.

### To test:
Enter `gradlew test` in the console.

