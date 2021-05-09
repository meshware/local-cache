package io.meshware.cache.sample.springboot.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class TestEntity implements Serializable, Cloneable {

    private Long id;

    private String name;

    private Date createTime;

    private Integer age;

    private List<TestEntity> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public List<TestEntity> getChildren() {
        return children;
    }

    public void setChildren(List<TestEntity> children) {
        this.children = children;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
