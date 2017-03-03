package db;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.StringJoiner;

public class Parser {
    // Various common constructs, simplifies parsing.
    private static final String REST  = "\\s*(.*)\\s*",
            COMMA = "\\s*,\\s*",
            AND   = "\\s+and\\s+";

    // \s is whitespace, \S is anything but whitespace 

    // Stage 1 syntax, contains the command name.
    private static final Pattern CREATE_CMD = Pattern.compile("create table " + REST),
            LOAD_CMD   = Pattern.compile("load " + REST),
            STORE_CMD  = Pattern.compile("store " + REST),
            DROP_CMD   = Pattern.compile("drop table " + REST),
            INSERT_CMD = Pattern.compile("insert into " + REST),
            PRINT_CMD  = Pattern.compile("print " + REST),
            SELECT_CMD = Pattern.compile("select " + REST);

    // Stage 2 syntax, contains the clauses of commands.
    private static final Pattern CREATE_NEW  = Pattern.compile("(\\S+)\\s+\\(\\s*(\\S+\\s+\\S+\\s*" +
            "(?:,\\s*\\S+\\s+\\S+\\s*)*)\\)"),
            SELECT_CLS  = Pattern.compile("([^,]+?(?:,[^,]+?)*)\\s+from\\s+" +
                    "(\\S+\\s*(?:,\\s*\\S+\\s*)*)(?:\\s+where\\s+" +
                    "([\\w\\s+\\-*/'<>=!.]+?(?:\\s+and\\s+" +
                    "[\\w\\s+\\-*/'<>=!.]+?)*))?"),
            CREATE_SEL  = Pattern.compile("(\\S+)\\s+as select\\s+" +
                    SELECT_CLS.pattern()),
            INSERT_CLS  = Pattern.compile("(\\S+)\\s+values\\s+(.+?" +
                    "\\s*(?:,\\s*.+?\\s*)*)");

    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("Expected a single query argument");
            return;
        }
        System.out.println("evaluating");
        eval(args[0]);
    }

    //need constructor for anything???

    public static void eval(String query) {
        Matcher m;
        if ((m = CREATE_CMD.matcher(query)).matches()) {
            createTable(m.group(1));
        } else if ((m = LOAD_CMD.matcher(query)).matches()) {
            loadTable(m.group(1));
        } else if ((m = STORE_CMD.matcher(query)).matches()) {
            storeTable(m.group(1));
        } else if ((m = DROP_CMD.matcher(query)).matches()) {
            dropTable(m.group(1));
        } else if ((m = INSERT_CMD.matcher(query)).matches()) {
            insertRow(m.group(1));
        } else if ((m = PRINT_CMD.matcher(query)).matches()) {
            printTable(m.group(1));
        } else if ((m = SELECT_CMD.matcher(query)).matches()) {
            select(m.group(1));
        } else {
            System.err.printf("Malformed query: %s\n", query);
        }
    }

    public static Object[] createTable(String expr) {
        Matcher m;
        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            //index 1 is name, index 2 is list of columns
            System.out.println(m.group(1));
            System.out.println(m.group(2));
            String[] columnsStrings = m.group(2).split(COMMA);
            Object[] result = {"create new", m.group(1), m.group(2).split(COMMA)};
            return result;
        } else if ((m = CREATE_SEL.matcher(expr)).matches()) {
            //index 1: name, index 2: expressions(cols), index 3: tables, index 4: conds
            //not sure if the split comma thing is correct but i think it is
            System.out.println(m.group(1));
            System.out.println(m.group(2));
            System.out.println(m.group(3));
            System.out.println(m.group(4));
            Object[] result = {"create selected", m.group(1), m.group(2).split(COMMA), m.group(3).split(COMMA), m.group(4).split(COMMA)};
            return result;
        } else {
            System.err.printf("Malformed create: %s\n", expr);
            return new Object[1]; //i guess????
        }
    }

    public static Object[] loadTable(String name) {
        Object[] result = {"load", name};
        return result;
    }

    public static Object[]  storeTable(String name) {
        Object[] result = {"store", name};
        return result;
    }

    public static Object[] dropTable(String name) {
        Object[] result = {"drop", name};
        return result;
    }

    public static Object[] insertRow(String expr) {
        Matcher m = INSERT_CLS.matcher(expr);
        if (!m.matches()) {
            System.err.printf("Malformed insert: %s\n", expr);
            return new Object[1];
        }
        //index 1 is table, index 2 is row to be inserted
        Object[] result = {"insert", m.group(1), m.group(2)};
        return result;
    }

    public static Object[] printTable(String name) {
        Object[] result = {"print", name};
        return result;
    }

    public static Object[] select(String expr) {
        Matcher m = SELECT_CLS.matcher(expr);
        if (!m.matches()) {
            System.err.printf("Malformed select: %s\n", expr);
            return new Object[1];
        }
        //index 1 is expressions, index 2 is tables to be joined, index 3 is conditions to filter
        System.out.println(m.group(1));
        System.out.println(m.group(2));
        System.out.println(m.group(3));
        Object[] result =  {"select", m.group(1), m.group(2), m.group(3)};
        return result;
    }
}