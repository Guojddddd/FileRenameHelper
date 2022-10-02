package filerenamehelper.service;

import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author guo
 */
@Slf4j
public class Service {
	private List<List<Pair<File, File>>> pairLists;
	private Map<String, Comparator<File>> sortAlgorthms;
	private List<String> extensionNames = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp");

	public Service() {
		sortAlgorthms = new HashMap<>();
		sortAlgorthms.put("Dict Sequ", (a, b) -> {
			String nameA = a.getName();
			String nameB = b.getName();
			return nameA.compareTo(nameB);
		});
		sortAlgorthms.put("Length First Dict Sequ", (a, b) -> {
			String nameA = a.getName();
			String nameB = b.getName();
			if (nameA.length() != nameB.length()) {
				return nameA.length() - nameB.length();
			} else {
				return nameA.compareTo(nameB);
			}
		});
		sortAlgorthms.put("Number First Dict Sequ", (a, b) -> {
			String nameA = a.getName();
			String nameB = b.getName();
			int indexA = 0, indexB = 0;

			while (indexA < nameA.length() && indexB < nameB.length()) {
				String wordA = getWord(nameA, indexA);
				String wordB = getWord(nameB, indexB);

				if (isDigit(wordA) && isDigit(wordB)) {
					int numberCompareResult = digitCompare(wordA, wordB);
					if (numberCompareResult != 0) {
						return numberCompareResult;
					}
				} else {
					int strCompareResult = wordA.compareTo(wordB);
					if (strCompareResult != 0) {
						return strCompareResult;
					}
				}

				indexA += wordA.length();
				indexB += wordB.length();
			}

			return indexA - indexB;
		});
	}

	public void clear() {
		pairLists = null;
	}

	public String preView(String roots, String year, String month, String day, String sequ, String no, String sortAlgorithm, String mode) {
		pairLists = new ArrayList<>();

		Scanner scanner = new Scanner(roots);
		int index = 1;
		if ("sequ".equals(mode)) {
			index = Integer.parseInt(sequ);
		} else if ("no".equals(mode)) {
			index = Integer.parseInt(no);
		}
		while (scanner.hasNext()) {
			String line = scanner.nextLine();
			if ("".equals(line.trim())) {
				continue;
			}

			File dirFile = new File(line);
			if (dirFile.exists() && dirFile.isDirectory()) {
				File[] children = dirFile.listFiles();

				if (children == null) {
					continue;
				}

				List<File> childrenList = Arrays.stream(children).filter(f -> extensionNames.contains(String.valueOf(StringUtils.getFilenameExtension(f.getName())).toLowerCase())).collect(Collectors.toList());
				childrenList.sort(sortAlgorthms.get(sortAlgorithm));

				List<Pair<File, File>> pairList = new ArrayList<>();
				int noindex = Integer.parseInt(no);
				for (File oriFile : childrenList) {
					String oriName = oriFile.getName();
					String extension = StringUtils.getFilenameExtension(oriName);
					extension = extension == null ? "" : extension;
					String newName = oriName;

					if ("sequ".equals(mode)) {
						newName = String.format("%s-%s-%s.%02d.%02d.%s", year, month, day, index, noindex, extension);
						noindex += 1;
					} else if ("no".equals(mode)) {
						newName = String.format("%s-%s-%s.%s.%02d.%s", year, month, day, sequ, index, extension);
						index += 1;
					}

					File newFile = new File(oriFile.getParentFile(), newName);
					pairList.add(new Pair<>(oriFile, newFile));
				}

				if ("sequ".equals(mode)) {
					index += 1;
				}
				pairLists.add(pairList);
			}
		}

		StringBuilder sb = new StringBuilder();
		boolean flag = false;
		for (List<Pair<File, File>> pairList : pairLists) {
			sb.append("----\n");
			for (Pair<File, File> pair : pairList) {
//				System.out.println(pair.getValue().getAbsolutePath() + " <- " + pair.getKey().getAbsolutePath());
				flag = true;
				sb.append(pair.getValue().getName() + " <- " + pair.getKey().getName() + "\n");
			}
		}

		return flag ? sb.toString() : null;
	}

	public boolean executeRename() {
		try {
			if (pairLists != null) {
				for (List<Pair<File, File>> pairList : pairLists) {
					for (Pair<File, File> pair : pairList) {
						pair.getKey().renameTo(pair.getValue());
					}
				}
			}

			return true;
		} catch (Exception e) {
			log.error("改名报错", e);
			return false;
		}
	}

	private String getWord(String str, int startIndex) {
		if (str != null && startIndex < str.length()) {
			int endIndex = startIndex + 1;
			while (endIndex < str.length()) {
				if (Character.isDigit(str.charAt(endIndex)) != Character.isDigit(str.charAt(startIndex))) {
					break;
				} else {
					endIndex += 1;
				}
			}

			return str.substring(startIndex, endIndex);
		} else {
			return null;
		}
	}

	private boolean isDigit(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	private int digitCompare(String a, String b) {
		int maxLength = Math.max(a.length(), b.length());
		int indexA = a.length() - maxLength;
		int indexB = b.length() - maxLength;

		for (int i = 0; i < maxLength; i++) {
			char charA = indexA >= 0 ? a.charAt(indexA) : '0';
			char charB = indexB >= 0 ? b.charAt(indexB) : '0';
			if (charA != charB) {
				return charA - charB;
			} else {
				indexA ++;
				indexB ++;
			}
		}
		return 0;
	}
}
