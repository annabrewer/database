package db;

import com.sun.rowset.internal.Row;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class Database {

    private HashMap<String, Table> tables;
    private Parser parser;

    public Database() {
        tables = new HashMap<>();
        parser = new Parser(tables);
    }

    public String transact(String query) {
        return "";
    }

    private String createTable(String cmd) {
        return parser.createTable(cmd);
    }

    private String select(String cmd) {
        return parser.select(cmd);
    }

    private String printTable(String cmd) {
        return parser.printTable(cmd);
    }

    private String loadTable(String cmd) {
        return parser.loadTable(cmd);
    }

    private String dropTable(String cmd) {
        return parser.dropTable(cmd);
    }

    private String insertValues(String cmd) {
        return parser.insertRow(cmd);
    }

}
