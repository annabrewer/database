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

        String g =  dbase.transact("create table fans (Lastname string, Firstname string, TeamName string)");
        g = dbase.transact("insert into fans values 'Lee','Maurice','Mets'");
        g = dbase.transact("insert into fans values 'Lee','Maurice','Steelers'");
        g = dbase.transact("insert into fans values 'Ray','Mitas','Patriots'");
        g = dbase.transact("insert into fans values 'Hwang','Alex','Cloud9'");
        g = dbase.transact("insert into fans values 'Rulison','Jared','EnVyUs'");
        g = dbase.transact("insert into fans values 'Fang','Vivian','Golden Bears'");

        g = dbase.transact("create table teams (TeamName string,City string,Sport string,YearEstablished int,Mascot string,Stadium string)");
        g = dbase.transact("insert into teams values ‘Mets’,’New York’,’MLB Baseball’,1962,’Mr. Met’,’Citi Field’");
        g = dbase.transact("insert into teams values ‘Steelers’,‘Pittsburgh’,‘NFL Football’,1933,‘Steely McBeam’,‘Heinz Field’");
        g = dbase.transact("insert into teams values ‘Patriots’,‘New England’,‘NFL Football’,1960,‘Pat Patriot’,‘Gillette Stadium’");
        g = dbase.transact("insert into teams values ‘Cloud9’,‘Los Angeles’,‘eSports’,2012, NOVALUE, NOVALUE");
        g = dbase.transact("insert into teams values ‘EnVyUs’,‘Charlotte’,‘eSports’,2007, NOVALUE, NOVALUE");
        g = dbase.transact("insert into teams values ‘Golden Bears’,‘Berkeley’,‘NCAA Football’,1886,‘Oski’,‘Memorial Stadium’");

        g = dbase.transact("create table records (TeamName string,Season int,Wins int,Losses int,Ties int)");
        g = dbase.transact("insert into records values ‘Golden Bears’, 2016,  5, 7, 0");
        g = dbase.transact("insert into records values ‘Golden Bears’, 2015, 8, 5, 0");
        g = dbase.transact("insert into records values ‘Golden Bears’, 2014, 5, 7, 0");
        g = dbase.transact("insert into records values ‘Steelers’, 2015, 10, 6, 0");
        g = dbase.transact("insert into records values ‘Steelers’, 2014, 11, 5, 0");
        g = dbase.transact("insert into records values ‘Steelers’, 2013, 8, 8, 0");
        g = dbase.transact("insert into records values ‘Mets’, 2015, 90, 72, 0");
        g = dbase.transact("insert into records values ‘Mets’, 2014, 79, 83, 0");
        g = dbase.transact("insert into records values ‘Mets’, 2013, 74, 88, 0");
        g = dbase.transact("insert into records values ‘Patriots’, 2015, 12, 4, 0");
        g = dbase.transact("insert into records values ‘Patriots’, 2014, 12, 4, 0");
        g = dbase.transact("insert into records values ‘Patriots’, 2013, 12, 4, 0");

        g = dbase.transact("store fans");
        g = dbase.transact("store records");
        g = dbase.transact("store teams");

        String a = dbase.transact("load fans");
        String b = dbase.transact("load teams");
        String c = dbase.transact("load records");
        String d = dbase.transact("load badTable");
        System.out.print("Actual: "+ d);
        System.out.print("Expected: "+ "Unable to open file 'badTable.tbl'");
        //AssertEquals(d, "Unable to open file 'badTable.tbl'", bleh);

        String fans = dbase.transact("print fans");
        String fansExpected = "Lastname string,Firstname string,TeamName string\n" +
        "'Lee', 'Maurice', 'Mets'\n" +
        "'Lee', 'Maurice', 'Steelers'\n"+
        "'Ray', 'Mitas', 'Patriots'\n"+
        "'Hwang', 'Alex', 'Cloud9'\n"+
        "'Rulison', 'Jared', 'EnVyUs'\n"+
        "'Fang', 'Vivian', 'Golden Bears'\n";
        System.out.print("Actual: "+fans);
        System.out.print("Expected: "+fansExpected);
        //AssertEquals(fans, fansExpected, fans);

        String lee = dbase.transact("select Firstname,Lastname,TeamName from fans where Lastname >= 'Lee'");
        String leeExpected = "Firstname string, Lastname string, TeamName string\n"+
        "'Maurice', 'Lee', 'Mets'\n"+
        "'Maurice', 'Lee', 'Steelers'\n"+
        "'Mitas', 'Ray', 'Patriots'\n"+
        "'Jared', 'Rulison', 'EnVyUs'\n";
        System.out.print("Actual: "+ lee);
        System.out.print("Expected: "+ leeExpected);

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
        System.out.print("Actual: "+ seasonsPrint);
        System.out.print("Expected: "+ seasonsPrintExpected);
        //AssertEquals(seasonsPrint, seasonsPrintExpected, seasonsPrint);

        String fails = dbase.transact("select City,Season,Ratio from seasonRatios where Ratio < 1");
        String failsExpected = "City string, Season int,Ratio int\n"+
        "'New York', 2014, 0\n"+
        "'New York', 2013, 0\n"+
        "'Berkeley', 2016, 0\n"+
        "'Berkeley', 2014, 0\n";
        System.out.print("Actual: "+ fails);
        System.out.print("Expected: "+ failsExpected);
        //AssertEquals(fails, failsExpected, fails);

        String e = dbase.transact("store seasonRatios");
        String f = dbase.transact("store badTable");
    }
}
