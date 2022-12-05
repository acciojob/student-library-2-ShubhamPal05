package com.driver.repositories;

import com.driver.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface StudentRepository extends JpaRepository<Student, Integer> {

    @Modifying
    @Query("update Student s set s.emailId = :#{#std.emailId}, " +
            "s.name = :#{#std.name}, " +
            "s.age = :#{#std.age} ," +
            "s.country = :#{#std.country} " +
            "where s.id = :#{#std.id}")
    // @Query("update student u set u.name=:#{#std.name}, u.age=#{#std.age}, u.country = :#{#std.country}, u.email_id = :#{#std.emailId} where id=:#{#std.id}" , nativeQuery = true)
    int updateStudentDetails(Student std);


    @Modifying
    @Query("delete from Student s where s.id =:id")
    void deleteCustom(int id);

    @Modifying
    @Query(value = "update student s set s.card_id =:cardId where s.id =:stuId", nativeQuery = true)
    int setCard(int cardId, int stuId);

    @Query(value = "select * from Student s where s.email_id= :email", nativeQuery = true)
    Student findByEmailId(String email);
}
