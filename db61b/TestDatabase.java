package db61b;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestDatabase {
    @Test
    public void testDataBase() {
        Database a = new Database();
        Table firstTable = new Table(new String[] {"CCN", "Num", "Dept"});
        String[] one = new String[] {"1", "11", "a"};
        String[] two = new String[] {"2", "22", "b"};
        String[] three = new String[] {"3", "33", "c"};
        firstTable.add(one);
        firstTable.add(two);
        firstTable.add(three);
        Table secondTable = new Table(new String[]
            {"names", "numbers", "heart"});
        String[] o = new String[] {"1", "11", "a"};
        String[] t = new String[] {"2", "22", "b"};
        String[] th = new String[] {"3", "33", "c"};
        secondTable.add(o);
        secondTable.add(t);
        secondTable.add(th);
        a.put("random", secondTable);
        a.put("schedule", firstTable);
        Table getSchedule = a.get("schedule");
        Table getRandom = a.get("random");
        getSchedule.print();
        getRandom.print();
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(TestDatabase.class));
    }
}

