package com.driver.services;

import com.driver.models.Card;
import com.driver.models.CardStatus;
import com.driver.models.Student;
import com.driver.repositories.CardRepository;
import com.driver.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {


    @Autowired
    CardService cardService4;

    @Autowired
    StudentRepository studentRepository4;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    CardService cardService;

    public Student getDetailsByEmail(String email){

        Student student = studentRepository4.findByEmailId(email);
        return student;
    }

    public Student getDetailsById(int id){

        Student student = studentRepository4.findById(id).get();
        return student;
    }

    //doubt: why need to save twice ? if saving 
    public void createStudent(Student student){
        studentRepository4.save(student);
        Card card = cardService.createAndReturn(student);
        studentRepository4.setCard(card.getId(), student.getId());
        // student.setCard(card);
        // studentRepository4.save(student);
        
    }

    public void updateStudent(Student student){
        // Student stu = studentRepository4.findById(student.getId()).get();
        // stu.setName(student.getName());
        // stu.setAge(student.getAge());
        // stu.setCountry(student.getCountry());
        // stu.setEmailId(student.getEmailId());

        // studentRepository4.save(stu);
        studentRepository4.updateStudentDetails(student);

        // studentRepository4.updateStudentDetails(student);
    }

    public void deleteStudent(int id){
        Card card = studentRepository4.findById(id).get().getCard();

        card.setCardStatus(CardStatus.DEACTIVATED);
        cardRepository.save(card);

        studentRepository4.deleteCustom(id);
    }
}
