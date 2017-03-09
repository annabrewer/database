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
        return parser.eval(query);
    }

    /*private String createTable(String cmd) {
        return parser.createTable(cmd);
    }*/
}
