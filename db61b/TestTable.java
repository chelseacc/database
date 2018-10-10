package db61b;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;

public class TestTable {
        /** Testing the table.*/

    @Test
    public void testColumns() {
        Table a = new Table(new String[]{"SID", "CCN", "Grade"});
        assertEquals(3, a.columns());
    }

    @Test
    public void testGetTitle() {
        Table a = new Table(new String[]
        {"SID", "Lastname", "Firstname", "SemEnter"});
        assertEquals("Firstname", a.getTitle(2));
    }

    @Test
    public void testFindColumn() {
        Table a = new Table(new String[]{"CCN", "Num", "Dept", "Time"});
        assertEquals(2, a.findColumn("Dept"));
        assertEquals(-1, a.findColumn("Sem"));
    }

    @Test
    public void testAdd() {
        String[] columnTitles = {"SID", "Lastname", "Firstname",
            "SemEnter", "YearEnter", "Major"};
        Table testing = new Table(columnTitles);

        String[] line1 = {"101", "Knowles", "Jason", "F", "2003", "EECS"};
        testing.add(line1);

        assertEquals(false, testing.add(line1));
        assertEquals(1, testing.size());

        testing.print();
    }

    @Test
    public void testReadTable() {
        Table a = Table.readTable("schedule");
        a.print();
    }

    @Test
    public void testWriteTable() {
        Table a = Table.readTable("schedule");
        a.writeTable("testingWriteTable");
    }

    @Test
    public void testSelectWithOneTableNoCond() {
        Table schedule = new Table(new String[]
            {"CCN", "Num", "Dept", "Time", "Room", "Sem", "Year"});
        String[] first = new String[]
            {"21228", "61A", "EECS", "2-3 MWF", "1 Pimentel", "F", "2003"};
        String[] second = new String[]
            {"21231", "61A", "EECS", "1-2 MWF", "2 Pimentel", "S", "2004"};
        schedule.add(first);
        schedule.add(second);

        List<String> colNames = new ArrayList<String>();
        colNames.add("CCN");
        colNames.add("Time");

        List<Condition> cond = new ArrayList<Condition>();
        Column col1 = new Column("CCN", schedule);
        Condition check1 = new Condition(col1 , ">", "21105");
        cond.add(check1);

        Table newSchedule = schedule.select(colNames, cond);
        newSchedule.print();
    }

    @Test
    public void testSelectWithOneTableAndCond() {
        Table schedule = new Table(new String[]
            {"CCN", "Num", "Dept", "Time", "Room", "Sem", "Year"});
        String[] first = new String[]
            {"21228", "61A", "EECS", "2-3 MWF", "1 Pimentel", "F", "2003"};
        String[] second = new String[]
            {"21231", "61A", "EECS", "1-2 MWF", "2 Pimentel", "S", "2004"};
        schedule.add(first);
        schedule.add(second);

        List<String> colNames = new ArrayList<String>();
        colNames.add("CCN");
        colNames.add("Time");

        List<Condition> cond = new ArrayList<Condition>();
        Column col1 = new Column("CCN", schedule);
        Condition check1 = new Condition(col1 , ">", "21105");
        cond.add(check1);

        Table newSchedule = schedule.select(colNames, cond);
        newSchedule.print();
    }

    @Test
    public void testSelectWithTwoTable() {
        Table students = Table.readTable("students");
        ArrayList<String> columns = new ArrayList<String>();
        columns.add("SID");
        columns.add("Firstname");
        columns.add("Grade");

        Table enrolled = Table.readTable("enrolled");
        ArrayList<Condition> cond = new ArrayList<Condition>();
        Column one = new Column("Major", students);
        Condition condition = new Condition(one, "=", "EECS");
        cond.add(condition);

        Table finalOne = students.select(enrolled, columns, cond);
        finalOne.print();
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(TestTable.class));
    }
}

