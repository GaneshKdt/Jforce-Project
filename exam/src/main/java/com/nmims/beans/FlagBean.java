package com.nmims.beans;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
//@JsonTypeName("FlagBean-Type")

//spring security related changes rename FlagBean to FlagExamBean
@RedisHash
public class FlagBean  implements Serializable{

private static final long serialVersionUID = 1L;

@Id
private String key;
private String value;
public String getKey() {
	return key;
}
public void setKey(String key) {
	this.key = key;
}
public String getValue() {
	return value;
}
public void setValue(String value) {
	this.value = value;
}



}
