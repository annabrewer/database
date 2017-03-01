package db;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
    private static final Pattern CREATE_NEW  = Pattern.compile("(\\S+)\\s+\\((\\S+\\s+\\S+\\s*" +
            "(?:,\\s*\\S+\\s+\\S+\\s*)*)\\)"),
            SELECT_CLS  = Pattern.compile("([^,]+?(?:,[^,]+?)*)\\s+from\\s+" +
                    "(\\S+\\s*(?:,\\s*\\S+\\s*)*)(?:\\s+where\\s+" +
                    "([\\w\\s+\\-*/'<>=!.]+?(?:\\s+and\\s+" +
                    "[\\w\\s+\\-*/'<>=!.]+?)*))?"),
            CREATE_SEL  = Pattern.compile("(\\S+)\\s+as select\\s+" +
                    SELECT_CLS.pattern()),
            INSERT_CLS  = Pattern.compile("(\\S+)\\s+values\\s+(.+?" +
                    "\\s*(?:,\\s*.+?\\s*)*)");

    /*public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Expected a single query argument");
            return;
        }

        eval(args[0]);
    }*/

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

    public static void createTable(String expr) {
        Matcher m;
        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            createNewTable(m.group(1), m.group(2).split(COMMA));
        } else if ((m = CREATE_SEL.matcher(expr)).matches()) {
            createSelectedTable(m.group(1), m.group(2), m.group(3), m.group(4));
        } else {
            System.err.printf("Malformed create: %s\n", expr);
        }
    }

    public static void createNewTable(String name, String[] cols) {
        StringJoiner joiner = new StringJoiner(", ");
        for (int i = 0; i < cols.length-1; i++) {
            joiner.add(cols[i]);
        }

        String colSentence = joiner.toString() + " and " + cols[cols.length-1];
        System.out.printf("You are trying to create a table named %s with the columns %s\n", name, colSentence);
    }

    public static void createSelectedTable(String name, String exprs, String tables, String conds) {
        System.out.printf("You are trying to create a table named %s by selecting these expressions:" +
                " '%s' from the join of these tables: '%s', filtered by these conditions: '%s'\n", name, exprs, tables, conds);
    }

    public static void loadTable(String name) {
        System.out.printf("You are trying to load the table named %s\n", name);
    }

    public static void storeTable(String name) {
        System.out.printf("You are trying to store the table named %s\n", name);
    }

    public static void dropTable(String name) {
        System.out.printf("You are trying to drop the table named %s\n", name);
    }

    public static void insertRow(String expr) {
        Matcher m = INSERT_CLS.matcher(expr);
        if (!m.matches()) {
            System.err.printf("Malformed insert: %s\n", expr);
            return;
        }

        System.out.printf("You are trying to insert the row \"%s\" into the table %s\n", m.group(2), m.group(1));
    }

    public static void printTable(String name) {
        System.out.printf("You are trying to print the taprivate static final String REST  = \"\\\\s*(.*)\\\\s*\",\n" +
                "                                COMMA = \"\\\\s*,\\\\s*\",\n" +
                "                                AND   = \"\\\\s+and\\\\s+\";\n" +
                "    \n" +
                "    // \\s is whitespace, \\S is anything but whitespace \n" +
                "\n" +
                "    // Stage 1 syntax, contains the command name.\n" +
                "    private static final Pattern CREATE_CMD = Pattern.compile(\"create table \" + REST),\n" +
                "                                 LOAD_CMD   = Pattern.compile(\"load \" + REST),\n" +
                "                                 STORE_CMD  = Pattern.compile(\"store \" + REST),\n" +
                "                                 DROP_CMD   = Pattern.compile(\"drop table \" + REST),\n" +
                "                                 INSERT_CMD = Pattern.compile(\"insert into \" + REST),\n" +
                "                                 PRINT_CMD  = Pattern.compile(\"print \" + REST),\n" +
                "                                 SELECT_CMD = Pattern.compile(\"select \" + REST);\n" +
                "\n" +
                "    // Stage 2 syntax, contains the clauses of commands.\n" +
                "    private static final Pattern CREATE_NEW  = Pattern.compile(\"(\\\\S+)\\\\s+\\\\((\\\\S+\\\\s+\\\\S+\\\\s*\" +\n" +
                "                                               \"(?:,\\\\s*\\\\S+\\\\s+\\\\S+\\\\s*)*)\\\\)\"),\n" +
                "                                 SELECT_CLS  = Pattern.compile(\"([^,]+?(?:,[^,]+?)*)\\\\s+from\\\\s+\" +\n" +
                "                                               \"(\\\\S+\\\\s*(?:,\\\\s*\\\\S+\\\\s*)*)(?:\\\\s+where\\\\s+\" +\n" +
                "                                               \"([\\\\w\\\\s+\\\\-*/'<>=!.]+?(?:\\\\s+and\\\\s+\" +\n" +
                "                                               \"[\\\\w\\\\s+\\\\-*/'<>=!.]+?)*))?\"),\n" +
                "                                 CREATE_SEL  = Pattern.compile(\"(\\\\S+)\\\\s+as select\\\\s+\" +\n" +
                "                                                   SELECT_CLS.pattern()),\n" +
                "                                 INSERT_CLS  = Pattern.compile(\"(\\\\S+)\\\\s+values\\\\s+(.+?\" +\n" +
                "                                               \"\\\\s*(?:,\\\\s*.+?\\\\s*)*)\");\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        if (args.length != 1) {\n" +
                "            System.err.println(\"Expected a single query argument\");\n" +
                "            return;\n" +
                "        }\n" +
                "\n" +
                "        eval(args[0]);\n" +
                "    }\n" +
                "\n" +
                "    private static void eval(String query) {\n" +
                "        Matcher m;\n" +
                "        if ((m = CREATE_CMD.matcher(query)).matches()) {\n" +
                "             createTable(m.group(1));\n" +
                "        } else if ((m = LOAD_CMD.matcher(query)).matches()) {\n" +
                "             loadTable(m.group(1));\n" +
                "        } else if ((m = STORE_CMD.matcher(query)).matches()) {\n" +
                "             storeTable(m.group(1));\n" +
                "        } else if ((m = DROP_CMD.matcher(query)).matches()) {\n" +
                "             dropTable(m.group(1));\n" +
                "        } else if ((m = INSERT_CMD.matcher(query)).matches()) {\n" +
                "             insertRow(m.group(1));\n" +
                "        } else if ((m = PRINT_CMD.matcher(query)).matches()) {\n" +
                "             printTable(m.group(1));\n" +
                "        } else if ((m = SELECT_CMD.matcher(query)).matches()) {\n" +
                "             select(m.group(1));\n" +
                "        } else {\n" +
                "            System.err.printf(\"Malformed query: %s\\n\", query);\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private static void createTable(String expr) {\n" +
                "        Matcher m;\n" +
                "        if ((m = CREATE_NEW.matcher(expr)).matches()) {\n" +
                "            createNewTable(m.group(1), m.group(2).split(COMMA));\n" +
                "        } else if ((m = CREATE_SEL.matcher(expr)).matches()) {\n" +
                "            createSelectedTable(m.group(1), m.group(2), m.group(3), m.group(4));\n" +
                "        } else {\n" +
                "            System.err.printf(\"Malformed create: %s\\n\", expr);\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private static void createNewTable(String name, String[] cols) {\n" +
                "        StringJoiner joiner = new StringJoiner(\", \");\n" +
                "        for (int i = 0; i < cols.length-1; i++) {\n" +
                "            joiner.add(cols[i]);\n" +
                "        }\n" +
                "\n" +
                "        String colSentence = joiner.toString() + \" and \" + cols[cols.length-1];\n" +
                "        System.out.printf(\"You are trying to create a table named %s with the columns %s\\n\", name, colSentence);\n" +
                "    }\n" +
                "\n" +
                "    private static void createSelectedTable(String name, String exprs, String tables, String conds) {\n" +
                "        System.out.printf(\"You are trying to create a table named %s by selecting these expressions:\" +\n" +
                "                \" '%s' from the join of these tables: '%s', filtered by these conditions: '%s'\\n\", name, exprs, tables, conds);\n" +
                "    }\n" +
                "\n" +
                "    private static void loadTable(String name) {\n" +
                "        System.out.printf(\"You are trying to load the table named %s\\n\", name);\n" +
                "    }\n" +
                "\n" +
                "    private static void storeTable(String name) {\n" +
                "        System.out.printf(\"You are trying to store the table named %s\\n\", name);\n" +
                "    }\n" +
                "\n" +
                "    private static void dropTable(String name) {\n" +
                "        System.out.printf(\"You are trying to drop the table named %s\\n\", name);\n" +
                "    }\n" +
                "\n" +
                "    private static void insertRow(String expr) {\n" +
                "        Matcher m = INSERT_CLS.matcher(expr);\n" +
                "        if (!m.matches()) {\n" +
                "            System.err.printf(\"Malformed insert: %s\\n\", expr);\n" +
                "            return;\n" +
                "        }\n" +
                "\n" +
                "        System.out.printf(\"You are trying to insert the row \\\"%s\\\" into the table %s\\n\", m.group(2), m.group(1));\n" +
                "    }\n" +
                "\n" +
                "    private static void printTable(String name) {\n" +
                "        System.out.printf(\"You are trying to print the table named %s\\n\", name);\n" +
                "    }\n" +
                "\n" +
                "    private static void select(String expr) {\n" +
                "        Matcher m = SELECT_CLS.matcher(expr);\n" +
                "        if (!m.matches()) {\n" +
                "            System.err.printf(\"Malformed select: %s\\n\", expr);\n" +
                "            return;\n" +
                "        }\n" +
                "\n" +
                "        select(m.group(1), m.group(2), m.group(3));\n" +
                "    }\n" +
                "\n" +
                "    private static void select(String exprs, String tables, String conds) {\n" +
                "        System.out.printf(\"You are trying to select these expressions:\" +\n" +
                "                \" '%s' from the join of these tables: '%s', filtered by these conditions: '%s'\\n\", exprs, tables, conds);\n" +
                "    }\n" +
                "}\nble named %s\n", name);
    }

    public static void select(String expr) {
        Matcher m = SELECT_CLS.matcher(expr);
        if (!m.matches()) {
            System.err.printf("Malformed select: %s\n", expr);
            return;
        }

        select(m.group(1), m.group(2), m.group(3));
    }

    public static void select(String exprs, String tables, String conds) {
        System.out.printf("You are trying to select these expressions:" +
                " '%s' from the join of these tables: '%s', filtered by these conditions: '%s'\n", exprs, tables, conds);
    }
}