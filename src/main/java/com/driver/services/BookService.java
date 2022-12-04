package com.driver.services;

import com.driver.models.Author;
import com.driver.models.Book;
import com.driver.repositories.AuthorRepository;
import com.driver.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {


    @Autowired
    BookRepository bookRepository2;

    @Autowired
    AuthorRepository authorRepository;

    public void createBook(Book book){
        book.setAvailable(true);
        book = bookRepository2.save(book);
        Author author = book.getAuthor();
        List<Book> bookList = author.getBooksWritten();
        bookList.add(book);
        author.setBooksWritten(bookList);
        authorRepository.save(author);
    }

    public List<Book> getBooks(String genre, boolean available, String author){
        List<Book> books = new ArrayList<>();

        if(author == null && genre == null){
            return bookRepository2.findByAvailability(available);
        }

        else if(author == null){
            return bookRepository2.findBooksByGenre(genre, available);
        }
        else if(genre == null){
            return bookRepository2.findBooksByAuthor(author, available);
        }
        else{
            return books;
        }

        // books = bookRepository2.findAll().stream().filter(book -> book.getGenre().toString().equals(genre)&&
        //                                                             book.getAuthor().toString().equals(author)&&
        //                                                             book.isAvailable())
        //                                                             .collect(Collectors.toList());

        // return books;
    }
}
