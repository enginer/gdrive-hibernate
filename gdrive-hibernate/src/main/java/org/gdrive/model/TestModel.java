package org.gdrive.model;

import javax.persistence.*;

@Entity
@Table(name = "tBo5uJGxMODcmQPp9zqxnoA")
public class TestModel {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    @Column(name = "whatareyouthinkaboutit")//"What are you think about it?")
    private String test;
    @Column(name = "rus")
    private String testRu;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getTestRu() {
        return testRu;
    }

    public void setTestRu(String testRu) {
        this.testRu = testRu;
    }
}
