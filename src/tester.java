import oop.ex6.main.Compiler.FileCompiler;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;

public class tester {
	int number;
	int fileNum;
	String prefPath;
	ArrayList<String[]> filelines = new ArrayList<>();

	static int fakeMain(String[] args) {
		try {
			if (args.length != 1) {
				throw new IOException();
			}
			BufferedReader reader = new BufferedReader(new FileReader(args[0]));
			FileCompiler compiler = new FileCompiler(reader);

			compiler.compile();
			return 0;
		}catch (IOException e) {
			System.err.println(e.getMessage());
			return 2;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return 1;
		}
	}
	@Test
	public void tests() throws FileNotFoundException, IOException {
		BufferedReader redaer = new BufferedReader(new FileReader
				("/Users/or/Desktop/CS/Java/ex6/ex6_files/sjavac_tests.txt"));
		String line;
		while ((line = redaer.readLine()) != null) {
			filelines.add(line.split(" "));
		}
		prefPath = "/Users/or/Desktop/CS/Java/ex6/ex6_files/tests/";
		System.out.println("Starting tests...");
		fileNum = 0;
		for (int i = 0; i < 506; i++) {
			number = i;
			String path = prefPath + "test" + number + ".sjava";
			if ((new File(path)).exists()) {


				String testInfo = " (";

				for (String s : filelines.get(fileNum)) {
					testInfo += " " + s;
				}
				testInfo += " )";

				System.out.print("now testing file " + number + " ... ");
				int ans = fakeMain(new String[]{path});
				boolean check = (ans == Integer.parseInt(filelines.get(fileNum)[1]));
				fileNum++;
				Assert.assertTrue("Test number " + number + " failed, should print " + filelines
						.get(fileNum)[1] + " but printed " + ans + testInfo, check);
				System.out.println(" Passed :)");
			}
		}
		System.out.println("All test passed :)");
	}
	@Test
	public void test() {


	}


}
