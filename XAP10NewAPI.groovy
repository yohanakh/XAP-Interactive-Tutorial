import com.gigaspaces.client.ChangeSet
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder
import com.gigaspaces.query.IdQuery
import com.gigaspaces.query.aggregators.AggregationResult
import com.gigaspaces.query.aggregators.AggregationSet
import com.j_spaces.core.client.SQLQuery
import demo.MultiplyLongChangeOperation
import demo.Person
import org.fusesource.jansi.AnsiConsole
import org.openspaces.admin.Admin
import org.openspaces.admin.AdminFactory

import java.util.concurrent.TimeUnit

import static org.openspaces.extensions.QueryExtension.max
import static org.openspaces.extensions.QueryExtension.maxEntry


static void print_color(text) {
    AnsiConsole.out.println(text.replace('|BLUE|', '\u001b[34;1m')
            .replace('|CYAN|', '\u001b[36;1m')
            .replace('|GREEN|', '\u001b[32;1m')
            .replace('|RED|', '\u001b[31;1m')
            .replace('|YELLOW|', '\u001b[33;1m')
            .replace('|PINK|', '\u001b[35;1m')
            .replace('|CLEAR|', '\u001b[0m'))
}

static void print_color_executing_body(text) {
    print_color(
            "|YELLOW|Executing:|CLEAR|" +
                    "\n" +
                    "|PINK|${text}|CLEAR|")
}

static void print_color_result() {
    print_color("|YELLOW|Result:|CLEAR|")
}

BufferedReader br = new BufferedReader(new InputStreamReader(System.in))
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

    def space = admin.getProcessingUnits().getProcessingUnit(gridname).getSpace().getGigaSpace()
    println ""

    print_color("|YELLOW|Introducing Person object to the space. Properties are: id, age and country.|CLEAR|")
    space.getTypeManager().registerTypeDescriptor(new SpaceTypeDescriptorBuilder("demo.Person").idProperty("id", false)
            .addFixedProperty("country", String.class)
            .addFixedProperty("age", Long.class)
            .addFixedProperty("id", Long.class).supportsDynamicProperties(true).create())
    println ""

    space.clear(new Object())

    print_color("|YELLOW|Writing 9 Person objects:|CLEAR|")
    print_color("\tid[0-2], age[10-12], country[UK]")
    print_color("\tid[3-5], age[13-15], country[U.S.A]")
    print_color("\tid[6-8], age[16-18], country[Other]")
    Person person;
    for (int i = 0; i < 9; i++) {
        person = new Person();
        person.setId(i);
        person.setAge(i + 10);
        person.setCountry((i < 3 ? "UK" : (i < 6 ? "U.S.A" : "Other")));
        space.write(person);
    }
    println ""
    println ""

    //Initialize SQLQuery
    print_color "|GREEN|Press ENTER to initialize SQLQuery to get Person objects with: |BLUE|country=\"UK\" OR country=\"U.S.A\"|CLEAR|"
    br.readLine()

    SQLQuery<Person> query = new SQLQuery<Person>(Person.class, "country=? OR country=? ");
    query.setParameter(1, "UK");
    query.setParameter(2, "U.S.A");
    print_color_executing_body("    SQLQuery<Person> query = new SQLQuery<Person>(Person.class,\"country=? OR country=? \");\n" +
            "    query.setParameter(1, \"UK\");\n" +
            "    query.setParameter(2, \"U.S.A\");")
    println ""
    println ""

    // Max value
    print_color("|GREEN|Press ENTER to retrieve the maximum value stored in the field \"age\"|CLEAR|")
    br.readLine()

    print_color_executing_body("    Number maxAgeInSpace = max(space, query, \"age\");\n" +
            "    println \"The max age is: \${maxAgeInSpace}\"")
    println ""

    print_color_result()
    Number maxAgeInSpace = max(space, query, "age");
    println "The max age is: ${maxAgeInSpace}"
    println ""
    println ""

    // Object with max value
    print_color("|GREEN|Press ENTER to retrieve the space object with the highest value for the field \"age\"|CLEAR|")
    br.readLine()

    print_color_executing_body("" +
            "   Person oldestPersonInSpace = maxEntry(space, query, \"age\");\n" +
            "    println \"Got Person with id: \${oldestPersonInSpace.getId()} and age: \${oldestPersonInSpace.getAge()}\"")

    println ""
    print_color_result()
    Person oldestPersonInSpace = maxEntry(space, query, "age");
    println "Got Person with id: ${oldestPersonInSpace.getId()} and age: ${oldestPersonInSpace.getAge()}"
    println ""
    println ""

    //Compound Aggregation
    print_color("|GREEN|Press ENTER to execute multiple aggregation operations|CLEAR|")
    br.readLine()

    print_color_executing_body("    AggregationResult aggregationResult = space.aggregate(query, new AggregationSet().minEntry(\"age\").average(\"age\").minValue(\"age\"));\n" +
            "\n" +
            "    Person youngest = (Person) aggregationResult.get(0);\n" +
            "    Number average = (Number) aggregationResult.get(1);\n" +
            "    Number minValue = (Number) aggregationResult.get(2);\n" +
            "    println \"Multiple aggregation result:\\n\" +\n" +
            "            \"   Youngest(minEntry): \${youngest}\\n\" +\n" +
            "            \"   Average of ages(average): \${average}\\n\" +\n" +
            "            \"   Minimum value of age(minValue): \${minValue}\"")
    println ""
    print_color_result()
    AggregationResult aggregationResult = space.aggregate(query, new AggregationSet().minEntry("age").average("age").minValue("age"));

    Person youngest = (Person) aggregationResult.get(0);
    Number average = (Number) aggregationResult.get(1);
    Number minValue = (Number) aggregationResult.get(2);
    println "Multiple aggregation result:\n" +
            "   Youngest(minEntry): ${youngest}\n" +
            "   Average of ages(average): ${average}\n" +
            "   Minimum value of age(minValue): ${minValue}"
    println ""
    println ""

    //Change API
    print_color("|GREEN|Press ENTER to perform a change operation|CLEAR|")
    br.readLine()

    print_color("|YELLOW|* Writing one Person object with id:50, age:50 and country: UK|CLEAR|")
    print_color("|YELLOW|* Changes to be made: age 50->60, country \"UK\"->\"U.S.A\"|CLEAR|")
    println ""

    Person changePerson = new Person();
    changePerson.setId(50)
    changePerson.setAge(50)
    changePerson.setCountry("UK")
    space.write(changePerson)

    print_color_executing_body("" +
            "   IdQuery<Person> idQuery = new IdQuery<Person>(Person.class, new Long(50))\n" +
            "   before = space.read(idQuery)\n" +
            "   println \"Before change, age: \${before.getAge()}, country: \${before.getCountry()}\"\n" +
            "\n" +
            "   space.change(idQuery, new ChangeSet().increment(\"age\", new Long(10)).set(\"country\", \"U.S.A\"))\n" +
            "\n" +
            "   after = space.read(idQuery)\n" +
            "   println \"After change, age: \${after.getAge()}, country: \${after.getCountry()}\"")
    println ""
    print_color_result()
    IdQuery<Person> idQuery = new IdQuery<Person>(Person.class, new Long(50))
    before = space.read(idQuery)
    println "   Before change, age: ${before.getAge()}, country: ${before.getCountry()}"
    space.change(idQuery, new ChangeSet().increment("age", new Long(10)).set("country", "U.S.A"))
    after = space.read(idQuery)
    println "   After change, age: ${after.getAge()}, country: ${after.getCountry()}"
    println ""
    println ""

    //New Change API - custom change
    print_color("|GREEN|Press ENTER to perform a custom change operation|CLEAR|")
    br.readLine()

    print_color_executing_body("" +
            "   before = space.read(idQuery)\n" +
            "   println \"   Before change, age: ${before.getAge()}, country: ${before.getCountry()}\"\n" +
            "\n" +
            "   space.change(idQuery, new ChangeSet().custom(new MultiplyLongChangeOperation(\"age\", 2)));\n" +
            "\n" +
            "   after = space.read(idQuery)\n" +
            "   println \"   After change, age: ${after.getAge()}, country: ${after.getCountry()}\"")
    println ""

    print_color_result()
    before = space.read(idQuery)
    println "   Before change, age: ${before.getAge()}, country: ${before.getCountry()}"
    space.change(idQuery, new ChangeSet().custom(new MultiplyLongChangeOperation("age", 2)));
    after = space.read(idQuery)
    println "   After change, age: ${after.getAge()}, country: ${after.getCountry()}"
    println ""
    println ""

} catch (Exception e) {
    e.printStackTrace()
    print_color("|RED|Error occurred: " + e.toString() + "|CLEAR|")
}
br.close();