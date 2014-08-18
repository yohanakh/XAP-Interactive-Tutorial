package demo;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceIndex;

/**
 * Created by yohana on 8/17/14.
 */
@SpaceClass
public class Person {
    private Long id;
    private Long age;
    private String country;

    @SpaceId(autoGenerate=false)
    public Long getId() {
        return id;
    }

    public Person setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getAge() {
        return age;
    }

    public Person setAge(Long age) {
        this.age = age;
        return this;
    }

    @SpaceIndex
    public String getCountry() {
        return country;
    }

    public Person setCountry(String country) {
        this.country = country;
        return this;
    }
}
