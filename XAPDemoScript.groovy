import com.gigaspaces.document.SpaceDocument
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder
import com.j_spaces.core.client.SQLQuery
import demo.EngineerPojo
import org.openspaces.admin.Admin
import org.openspaces.admin.AdminFactory
import org.openspaces.core.GigaSpace
import org.fusesource.jansi.AnsiConsole
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.util.concurrent.TimeUnit

public class Demo {


    SpaceDocument document;

    EngineerPojo engineer;

    private void writePojos(GigaSpace gigaSpace) {
        printCommands('gigaSpace.write(new EngineerPojo(123, "Me", "groovy"));' +
                'gigaSpace.write(new EngineerPojo(345, "you", "java"));');

        gigaSpace.write(new EngineerPojo(123, "Me", "groovy"));
        gigaSpace.write(new EngineerPojo(345, "you", "java"));
    }

    private void writeDocument1(GigaSpace gigaSpace) {
        printCommands('document.setProperty("id", 789);' +
                'document.setProperty("name", "he");' +
                'document.setProperty("age", 21);' +
                'gigaSpace.write(document);');

        document.setProperty("id", 789);
        document.setProperty("name", "he");
        document.setProperty("age", 21);
        gigaSpace.write(document);
    }

    private void writeDocument2(GigaSpace gigaSpace) {
        printCommands('document.setProperty("id", 743);' +
                'document.setProperty("name", "she");' +
                'document.setProperty("age", 21);' +
                'gigaSpace.write(document);');

        document.setProperty("id", 743);
        document.setProperty("name", "she");
        document.setProperty("age", 21);
        gigaSpace.write(document);
    }

    private void testReadPojo(GigaSpace gigaSpace) {
        printCommands('engineer = gigaSpace.read(new EngineerPojo(123));' +
                'System.out.println(engineer);');
        print_color "_YResult: _X"

        engineer = gigaSpace.read(new EngineerPojo(123));
        System.out.println(engineer);
    }

    private void testReadDocument(GigaSpace gigaSpace) {
        printCommands('document.setProperty("id", 345);' +
                'SpaceDocument engineerDoc = gigaSpace.read(document);' +
                'System.out.println(engineerDoc);');
        print_color "_YResult: _X"

        document.setProperty("id", 345);
        SpaceDocument engineerDoc = gigaSpace.read(document);

        System.out.println(engineerDoc);
    }

    private void testReadSQLQuery(GigaSpace gigaSpace) {
        printCommands('engineer = gigaSpace.read(new SQLQuery<EngineerPojo>(EngineerPojo.class,' +
                '"id=789 AND name=\'he\'"));' +
                'System.out.println(engineer);');

        print_color "_YResult: _X"
        engineer = gigaSpace.read(new SQLQuery<EngineerPojo>(EngineerPojo.class,
                "id=789 AND name='he'"));
        System.out.println(engineer);
    }

    private void testReadJDBC() throws Exception {
        printCommands('Class.forName("com.j_spaces.jdbc.driver.GDriver");' +
                'Connection connection = null;' +
                'connection = DriverManager.getConnection("jdbc:gigaspaces:url:" + getRemoteSpaceURL());' +
                'Statement statement = connection.createStatement();' +
                'statement.execute("SELECT * FROM demo.EngineerPojo WHERE age=21");');
        print_color "_YResults: _X"
        Class.forName("com.j_spaces.jdbc.driver.GDriver");
        Connection connection = null;
        connection = DriverManager.getConnection("jdbc:gigaspaces:url:" + getRemoteSpaceURL());
        Statement statement = connection.createStatement();
        statement.execute("SELECT * FROM demo.EngineerPojo WHERE age=21");

        ResultSet resultSet = statement.getResultSet();
        int count = 0;
        while (resultSet.next()) {
            count++;
            System.out.println("JDBC: id=" + resultSet.getInt("id") + " name=" + resultSet.getString("name") + " age=" + resultSet.getInt("age"));
        }
    }

    private String getRemoteSpaceURL() {
        return "jini://*/*/myDataGrid?locators=" + System.getenv("LOOKUPLOCATORS")+"&groups="+System.getenv("LOOKUPGROUPS");
    }

    private void testReadJDBC2() throws Exception {
        printCommands('Class.forName("com.j_spaces.jdbc.driver.GDriver");' +
                'String url = getRemoteSpaceURL();' +
                'System.out.println("permutation url == " + url);' +
                'Connection connection = DriverManager.getConnection("jdbc:gigaspaces:url:" + url);' +
                'Statement statement = connection.createStatement();' +
                'statement.execute("SELECT * FROM demo.EngineerPojo");');
        print_color "_YResult: _X"

        Class.forName("com.j_spaces.jdbc.driver.GDriver");
        String url = getRemoteSpaceURL();
        System.out.println("permutation url == " + url);
        Connection connection = DriverManager.getConnection("jdbc:gigaspaces:url:" + url);
        Statement statement = connection.createStatement();
        statement.execute("SELECT * FROM demo.EngineerPojo");

        ResultSet resultSet = statement.getResultSet();
        int count = 0;
        while (resultSet.next()) {
            count++;
            System.out.println("JDBC: id=" + resultSet.getInt("id") + " name=" + resultSet.getString("name") + " age=" + resultSet.getInt("age"));
        }
    }

    private void printCommands(String message) {
        print_color "_YExecuting: _X"
        for (String s : message.split(";")) {
            print_color "_P\t" + s + ";_X"
        }

    }

    public void run(GigaSpace gigaSpace) throws Exception {

        this.document = new SpaceDocument("demo.EngineerPojo");
        gigaSpace.getTypeManager().registerTypeDescriptor(new SpaceTypeDescriptorBuilder("demo.EngineerPojo").idProperty("id").supportsDynamicProperties(true).create())

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in))

        print_color "_BPress ENTER to write Pojo objects_X"
        br.readLine()
        writePojos(gigaSpace);
        println ""

        print_color "_BPress ENTER to read Pojo objects_X"
        br.readLine()
        testReadPojo(gigaSpace);
        println ""

        print_color "_BPress ENTER to read Document objects_X"
        br.readLine()
        testReadDocument(gigaSpace);
        println ""

        print_color "_BPress ENTER to write Document object_X"
        br.readLine()
        writeDocument1(gigaSpace);
        println ""

        print_color "_BPress ENTER to read using JDBC_X"
        br.readLine()
        testReadJDBC2();
        println ""

        print_color "_BPress ENTER to read using SQLQuery_X"
        br.readLine()
        testReadSQLQuery(gigaSpace);
        println ""

        print_color "_BPress ENTER to write Document object_X"
        br.readLine()
        writeDocument2(gigaSpace);
        println ""

        print_color "_BPress ENTER to read using JDBC_X"
        br.readLine()
        testReadJDBC();
        println ""

        print_color "_BPress ENTER to exit_X"
        br.readLine()

    }
    static void print_color(text) {
        AnsiConsole.out.println(text.replace('_B', '\u001b[32;1m')
                .replace('_G', '\u001b[32;1m')
                .replace('_R', '\u001b[31;1m')
                .replace('_Y', '\u001b[33;1m')
                .replace('_P', '\u001b[35;1m')
                .replace('_X', '\u001b[0m'))
    }
}


try {
    def lookuplocators = System.getenv("LOOKUPLOCATORS")
	def lookupgroups = System.getenv("LOOKUPGROUPS")
    def gridname = "myDataGrid"
    Admin admin = new AdminFactory().useDaemonThreads(true).addLocators(lookuplocators).addGroups(lookupgroups).createAdmin();
    def pus = admin.getProcessingUnits().waitFor(gridname, 10, TimeUnit.SECONDS);
    if (pus == null) {
        Demo.print_color("_RUnable to find myDataGrid processing unit_X")
        System.exit(1)
    }
    if (! pus.waitFor(1)) {
        Demo.print_color("_RUnable to find myDataGrid instances_X")
        System.exit(1)
    }

    println "Found " + pus.getInstances().length + " space instances";

    def gigaSpace = admin.getProcessingUnits().getProcessingUnit(gridname).getSpace().getGigaSpace()
    def demo = new Demo()
    demo.run(gigaSpace)
} catch (Exception e) {
    e.printStackTrace()
    Demo.print_color("_RError occurred: " + e.toString()+"_X")
}
