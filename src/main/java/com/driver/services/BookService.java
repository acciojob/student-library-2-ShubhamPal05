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
        Author author1=null;
        Author author = book.getAuthor();

        if(author != null)
            author1 = authorRepository.findById(book.getAuthor().getId()).get();

        // if(author == null){
        //     System.out.println("author is null");
        // }
        // else if(author1 != null){
        //     System.out.println(author1.getId()+" "+ author1.getName() +" "+ author1.getEmail());
        // }

        // if(author1 == null){
        //     System.out.println("author1 is null");
        // }

        // System.out.println("here it is printed");

        if(author1 != null){

            List<Book> bookList = author1.getBooksWritten();

            if(bookList == null){
                bookList = new ArrayList<>();
            }
            book.setAvailable(true);
            book = bookRepository2.save(book);
            bookList.add(book);
            author1.setBooksWritten(bookList);
            authorRepository.save(author1);
        }
        else
            bookRepository2.save(book);

//------------------
        // if(author != null){
        //     if(author.getBooksWritten() == null){
        //         author.setBooksWritten(new ArrayList<>());
        //         }
        //         author.getBooksWritten().add(book);
        // }
        // else {
        //     System.out.println("author is null");
        // }
        // bookRepository2.save(book);

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
