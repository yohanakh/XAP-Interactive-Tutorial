package demo;

import java.io.Serializable;
import java.util.Map;
import com.gigaspaces.annotation.pojo.SpaceDynamicProperties;
import com.gigaspaces.annotation.pojo.SpaceId;

public class EngineerPojo implements Serializable {

	private Integer id;
	private String name;
	private String language;
	private Map<String, Object> dynamicProperties;

	public EngineerPojo(){}

	public EngineerPojo(Integer id){
		this.id = id;
	}

	public EngineerPojo(Integer id, String name, String language){
		this.id = id;
		this.name = name;
		this.language = language;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@SpaceId
	public Integer getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLanguage() {
		return language;
	}

	public void setDynamicProperties(Map<String, Object> dynamicProperties) {
		this.dynamicProperties = dynamicProperties;
	}

	@SpaceDynamicProperties
	public Map<String, Object> getDynamicProperties() {
		return dynamicProperties;
	}

	@Override
	public String toString() {
		String string = 
			"engineer: id=" + getId() + 
			" name=" + getName() + 
			" language=" + getLanguage();

		if(dynamicProperties != null){
			string += " " + dynamicProperties;
		}
		return string;
	}

}