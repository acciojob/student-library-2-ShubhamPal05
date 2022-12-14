package com.driver.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
// @NoArgsConstructor
@Table
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String emailId;
    private String name;
    private int age; // in case we want to check on the basis of age while issuing

    private String country;

    // alter table student add foreign key constraint card references Card(id)
    // @OneToOne(mappedBy = "student", cascade = CascadeType.ALL)

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL)
    @JoinColumn   // join this column to the primary key of Card table
    @JsonIgnoreProperties("student")
    private Card card;


    @CreationTimestamp
    private Date createdOn;

    @UpdateTimestamp
    private Date updatedOn;

    public Student(){

    }


    public Student(String email, String name, int age, String country){

        this.emailId = email;
        this.name = name;
        this.age = age;
        this.country = country;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", email='" + emailId + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", country='" + country + '\'' +
                ", createdOn=" + createdOn +
                ", updatedOn=" + updatedOn +
                '}';
    }


}
