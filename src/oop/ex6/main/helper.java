//package oop.ex6.main;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.util.ArrayList;
//import java.util.Scanner;
//
//public class helper {
//
//
//
//
//
//
//    package filesprocessing;
//
//import java.io.FileNotFoundException;
//import filesprocessing.exceptions.*;
//import java.util.ArrayList;
//import java.io.File;
//import java.util.Scanner;
//
//    public class DirectoryProcessor {
//        /**
//         *  A class that operate filter and order operations on files in a given directory
//         *  by a given command file
//         */
//
//        private final static int INPUT_SIZE = 2;
//        private final static int COMMAND_FILE = 1;
//        private final static int SECTION_DATA_SIZE = 4;
//        private final static String FILTER_LINE = "FILTER";
//        private final static String DEFAULT_ORDER = "abs";
//        private final static String BAD_INPUT_ERROR = "ERROR: Wrong usage. Should receive 2 arguments";
//        private final static String BAD_COMMAND_FILE_ERROR = "ERROR: The command file doesn't " +
//                "exist or it's directory";
//        private final static String BAD_SOURCE_DIRECTORY_ERROR = "ERROR: No files in sourcedir";
//
//
//        /*
//         * Output to the screen an error message and exit from the program.
//         * @param errorMessage The error message that is printed to the screen.
//         */
//        private static void generateException(String errorMessage) {
//            System.err.println(errorMessage);
//            System.exit(0);
//        }
//
//        /*
//         * Check if the input is valid. In case that the input is not valid print error message to the
//         * screen and exit.
//         * @param input The input that the program gets.
//         */
//        private static void checkInput(String[] input) {
//            if (input.length != INPUT_SIZE) {
//                System.out.println(BAD_INPUT_ERROR);
//                System.exit(0);
//            }
//        }
//
//        /*
//         * The method returns all the files in the given directory.
//         * @param sourcePath The path of the given directory.
//         * @return All the files in the given directory.
//         */
//        private static ArrayList<File> readDirectory(String sourcePath) {
//            ArrayList<File> files = new ArrayList<File>();
//            File sourceDir = new File(sourcePath);
//            try {
//                //check that the given path is directory and not empty.
//                if (!sourceDir.isDirectory() || sourceDir.length() == 0) {
//                    throw new BadSourceDirectoryException(BAD_SOURCE_DIRECTORY_ERROR);
//                }
//                for (File file : sourceDir.listFiles()) {
//                    if (file.isFile()) {
//                        files.add(file);
//                    }
//                }
//            } catch (BadSourceDirectoryException e) {
//                generateException(e.getMessage());
//            }
//            return files;
//        }
//
//
//        /*
//         * The method get the command file path and try to read it and to return it's line.
//         * @param commandPath The path of the command file.
//         * @return All the lines at the command file.
//         */
//        private static ArrayList<String> readCommandFile(String path) {
//            ArrayList<String> lines = new ArrayList<String>();
//            File file = new File(path);
//            try {
//                if (!file.isFile()) {
//                    throw new FileNotFoundException();
//                }
//                Scanner sc = new Scanner(file);
//                while (sc.hasNextLine()) { //read the file's lines
//                    lines.add(sc.nextLine());
//                }
//                sc.close();
//            } catch (FileNotFoundException e) {
//                generateException(BAD_COMMAND_FILE_ERROR);
//            }
//            return lines;
//        }
//
//
//        /*
//         * Check if the order of the given section is default order.
//         * @param commandLines The command lines that the order type is written by.
//         * @param sectionData An array that holds the data of the section.
//         * @param i Indicates on the line that is checked in the command file.
//         * @param j Indicates the field of the section that is updated.
//         * @return True if the order is default false otherwise.
//         */
//        private static boolean isDefaultOrder(ArrayList<String> commandLines,
//                                              String[] sectionData, int i, int j) {
//            if (j == SECTION_DATA_SIZE - 1) { // check if the order type is given
//                if (commandLines.get(i).equals(FILTER_LINE)) {
//                    sectionData[SECTION_DATA_SIZE - 1] = DEFAULT_ORDER;
//                    return true;
//                }
//            }
//            return false;
//        }
//
//        /*
//         * The method update the section data by changing the section data array.
//         * @param commandLines The command lines that the sections created by.
//         * @param sectionData An array that holds the data of the section.
//         * @param i The index that the current section starts with ib the command lines
//         * @return The index of the command line that the next section start from.
//         */
//        private static int updateSectionData(ArrayList<String> commandLines, String[] sectionData, int i) {
//            int j = 0;
//            while (j < SECTION_DATA_SIZE && i < commandLines.size()) {
//                if(isDefaultOrder(commandLines, sectionData, i, j)) {
//                    break;
//                }
//                sectionData[j] = commandLines.get(i);
//                j += 1;
//                i += 1;
//            }
//            if(j == SECTION_DATA_SIZE - 1) { // Check the case that the last line is ORDER
//                sectionData[j] = DEFAULT_ORDER;
//            }
//            return i;
//        }
//
//
//        /*
//         * Add c new section to the sections that created by the command file.
//         * @param sections The sections of the given command file.
//         * @param sectionData The data of the section that is created.
//         * @param index The index of the start line of the section in the command file.
//         */
//        private static void addSection(ArrayList<Section> sections, String[] sectionData, int index) {
//            try {
//                sections.add(new Section(index, sectionData));
//            }
//            catch (TypeTwoException e) {
//                generateException(e.getMessage());
//            }
//        }
//
//        /*
//         * Create section objects that performing the filter and order operations
//         * @param commandLines The lines of the command file
//         * @throws Exception If one of the FILTER or ORDER lines are incorrect it throws exceptions.
//         */
//        private static ArrayList<Section> getSections(ArrayList<String> commandLines) {
//            int i = 0;
//            int j = 0;
//            ArrayList<Section> sections = new ArrayList<Section>();
//            String[] sectionData;
//            while (i < commandLines.size()) { //pass all the command file lines
//                sectionData = new String[SECTION_DATA_SIZE]; // initialize data for each iteration
//                j = updateSectionData(commandLines, sectionData, i);
//                addSection(sections,sectionData, i);
//                i = j;
//            }
//            return sections;
//        }
//
//        /**
//         * The main method that runs the program. it's get the directory and the command file path and try to
//         * operate the lines in the command file on the files in the given directory.
//         * @param args Holds the path of the directory and the command files.
//         */
//        public static void main(String[] args) {
//            checkInput(args);
//            ArrayList<String> commandLines = readCommandFile(args[COMMAND_FILE]);
//            ArrayList<Section> sections = getSections(commandLines);
//            ArrayList<File> directoryFiles;
//            for (Section section: sections) {
//                directoryFiles = readDirectory(args[SOURCE_FOLDER]);
//                section.doAction(directoryFiles);
//            }
//        }
//    }
//
//
//
//
//
//
//
//}
