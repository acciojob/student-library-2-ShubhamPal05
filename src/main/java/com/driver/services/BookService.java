package com.driver.services;

import com.driver.models.Book;
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

    public void createBook(Book book){
        bookRepository2.save(book);
    }

    public List<Book> getBooks(String genre, boolean available, String author){
        List<Book> books = new ArrayList<>();

        books = bookRepository2.findAll().stream().filter(book -> book.getGenre().toString().equals(genre)&&
                                                                    book.getAuthor().toString().equals(author)&&
                                                                    book.isAvailable())
                                                                    .collect(Collectors.toList());

        return books;
    }
}
