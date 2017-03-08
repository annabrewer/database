package db;
import org.junit.*;

/**
 * Created by Anna on 3/7/17.
 */
public class DatabaseTests {

    public static void main(String[] args) {
        test();
    }

    public void test() {
        Database dbase = new Database();

        String a = dbase.transact("load fans");
        String b = dbase.transact("load teams");
        String c = dbase.transact("load records");
        String d = dbase.transact("load badTable");
        AssertEquals(d, "Unable to open file 'badTable.tbl'", bleh);

        String fans = dbase.transact("print fans");
        String fansExpected = "Lastname string,Firstname string,TeamName string\n" +
        "'Lee', 'Maurice', 'Mets'\n" +
        "'Lee', 'Maurice', 'Steelers'\n"+
        "'Ray', 'Mitas', 'Patriots'\n"+
        "'Hwang', 'Alex', 'Cloud9'\n"+
        "'Rulison', 'Jared', 'EnVyUs'\n"+
        "'Fang', 'Vivian', 'Golden Bears'\n";
        AssertEquals(fans, fansExpected, fans);

        String lee = dbase.transact("select Firstname,Lastname,TeamName from fans where Lastname >= 'Lee'");
        String leeExpected = "Firstname string, Lastname string, TeamName string\n"+
        "'Maurice', 'Lee', 'Mets'\n"+
        "'Maurice', 'Lee', 'Steelers'\n"+
        "'Mitas', 'Ray', 'Patriots'\n"+
        "'Jared', 'Rulison', 'EnVyUs'\n";
        AssertEquals(lee, leeExpected, lee);

        String mascots = dbase.transact("select Mascot,YearEstablished from teams where YearEstablished > 1942");
        String mascotsExpected = "Mascot string, YearEstablished int\n"+
        "'Mr. Met', 1962\n"+
        "'Pat Patriot', 1960\n"+
        "NOVALUE, 2012\n"+
        "NOVALUE, 2007\n";
        String seasons = dbase.transact("create table seasonRatios as select City,Season,Wins/Losses as Ratio from teams,records");
        String seasonsPrint = dbase.transact("print seasonRatios");
        String seasonsPrintExpected = "City string, Season int,Ratio int\n"+
        "'New York', 2015, 1\n"+
        "'New York', 2014, 0\n"+
        "'New York', 2013, 0\n"+
        "'Pittsburgh', 2015, 1\n"+
        "'Pittsburgh', 2014, 2\n"+
        "'Pittsburgh', 2013, 1\n"+
        "'New England', 2015, 3\n"+
        "'New England', 2014, 3\n"+
        "'New England', 2013, 3\n"+
        "'Berkeley', 2016, 0\n"+
        "'Berkeley', 2015, 1\n"+
        "'Berkeley', 2014, 0\n";
        AssertEquals(seasonsPrint, seasonsPrintExpected, seasonsPrint);

        String fails = dbase.transact("select City,Season,Ratio from seasonRatios where Ratio < 1");
        String failsExpected = "City string, Season int,Ratio int\n"+
        "'New York', 2014, 0\n"+
        "'New York', 2013, 0\n"+
        "'Berkeley', 2016, 0\n"+
        "'Berkeley', 2014, 0\n";
        AssertEquals(fails, failsExpected, fails);

        String e = dbase.transact("store seasonRatios");
        String f = dbase.transact("store badTable");
        AssertEquals(f, "Unable to open file 'badTable.tbl'", f);
    }
}
