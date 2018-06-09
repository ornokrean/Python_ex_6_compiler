package oop.ex6.main.Compiler;


import com.sun.istack.internal.NotNull;
import oop.ex6.main.Variables.Variable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileCompiler {

	protected ArrayList<String> code = new ArrayList<>();

	protected HashSet<String> functionsList;

//    private BufferedReader codeReader;

	protected HashSet<Variable> vars;

	private BlockCompiler[] mySubBlocks;

	public FileCompiler() {
	}

	public FileCompiler(BufferedReader codeReader) throws IOException, Exception {
		initiateCompiler(codeReader);
	}

	/*
	 * this function checks if given line is valid according to the codeLinePattern and notCommentPattern
	 * @param line the line to check
	 * @return true iff the line is valid.
	 */
	private boolean validateLine(String line) {
//		Pattern codeLinePattern = Pattern.compile("(?:(?:(?:void|if|while).*\\{)|\\}|[\\S^\\/]{2}.*;)");
//		Pattern notCommentPattern = Pattern.compile("([^\\/]{2}.*|\\})");

		Pattern codeLinePattern = Pattern.compile("(.*(?:(?:(?:void|if|while).*\\{)|\\}|[\\S^\\/]*.*[;]?))");


//		Pattern notCommentPattern = Pattern.compile("([^/]{2}.*|})");
		Matcher m = codeLinePattern.matcher(line);
		if (m.matches()) {
//			Matcher m2 = notCommentPattern.matcher(line);
			return m.matches();
		}
		return false;
	}

	private void initiateCompiler(BufferedReader codeReader) throws IOException, Exception {
		String codeLine;
		Map<Character,Integer> counter = new HashMap<>();
		counter.put('{',0);
		counter.put('}',0);
		counter.put('(',0);
		counter.put(')',0);
		while ((codeLine = codeReader.readLine()) != null) {
			changeCounter(counter, codeLine);

			if (validateLine(codeLine)) {
				code.add(codeLine);
			}
		}
		// check counter at the end
		if (!counter.get('{').equals(counter.get('}')) ||
				!counter.get('(').equals(counter.get(')')))
			throw new Exception("problem with {}()");
		for (String c : code) {
			System.out.println(c);
		}

		codeReader.close();
	}

	private void changeCounter(Map counter, String line) throws Exception {
		Pattern notCommentPattern = Pattern.compile("([^/]{2}.*|})");
		Matcher m2 = notCommentPattern.matcher(line);
		if (!m2.matches())
			return;
		int count;


			if ((count = line.length() - line.replace(par[i], "").length()) > 0) {
				counter[0] += ((-1)^i)*count;


		if (counter[0] < 0 || counter[1] < 0)
			throw new Exception("problem with {}()");


	}

	public void compile() {


//		for (BlockCompiler block : mySubBlocks) {
//			block.compile();
//		}
	}

}
