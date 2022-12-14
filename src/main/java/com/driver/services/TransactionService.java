package com.driver.services;

import com.driver.models.Book;
import com.driver.models.Card;
import com.driver.models.CardStatus;
import com.driver.models.Transaction;
import com.driver.models.TransactionStatus;
import com.driver.repositories.BookRepository;
import com.driver.repositories.CardRepository;
import com.driver.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ch.qos.logback.core.joran.conditional.ElseAction;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    BookRepository bookRepository5;

    @Autowired
    CardRepository cardRepository5;

    @Autowired
    TransactionRepository transactionRepository5;

    @Value("${books.max_allowed}")
    public int max_allowed_books;

    @Value("${books.max_allowed_days}")
    public int getMax_allowed_days;

    @Value("${books.fine.per_day}")
    public int fine_per_day;

    public String issueBook(int cardId, int bookId) throws Exception 
    {
        //check whether bookId and cardId already exist
        //conditions required for successful transaction of issue book:
        //1. book is present and available
        // If it fails: throw new Exception("Book is either unavailable or not present");
        //2. card is present and activated
        // If it fails: throw new Exception("Card is invalid");
        //3. number of books issued against the card is strictly less than max_allowed_books
        // If it fails: throw new Exception("Book limit has reached for this card");
        //If the transaction is successful, save the transaction to the list of transactions and return the id

        //Note that the error message should match exactly in all cases


        Book book = bookRepository5.findById(bookId).get();
        Card card = cardRepository5.findById(cardId).get();

        if(book == null || !book.isAvailable()){
            createNewTransaction(card, book, true, TransactionStatus.FAILED);
            throw new Exception("Book is either unavailable or not present");
        }

        
        else if(card == null || card.getCardStatus().equals(CardStatus.DEACTIVATED)){
            createNewTransaction(card, book, true, TransactionStatus.FAILED);
            throw new Exception("Card is invalid");
        }

        else if(card.getBooks().size() >= max_allowed_books){
            createNewTransaction(card, book, true, TransactionStatus.FAILED);
            throw new Exception("Book limit has reached for this card");
        }

        else{
            //saving book in card
            List<Book> bookList = card.getBooks();
            if(bookList == null){
                bookList = new ArrayList<>();
            }
            bookList.add(book);
            card.setBooks(bookList);
            book.setCard(card);
            book.setAvailable(false);
            bookRepository5.save(book);
            cardRepository5.save(card);
            return createNewTransaction(card, book, true, TransactionStatus.SUCCESSFUL).getTransactionId();
        }

    }
    public Transaction createNewTransaction(Card card, Book book, boolean isIssue, TransactionStatus tStatus){

        Transaction transaction = new Transaction();
        transaction.setCard(card);
        transaction.setBook(book);
        transaction.setIssueOperation(isIssue);
        transaction.setTransactionStatus(tStatus);
        transaction = transactionRepository5.save(transaction);
        return transaction;
    }

    public Transaction returnBook(int cardId, int bookId) throws Exception{

        List<Transaction> transactions = transactionRepository5.find(cardId, bookId, TransactionStatus.SUCCESSFUL, true);
        Transaction transaction = transactions.get(transactions.size() - 1);

        Card card = cardRepository5.findById(cardId).get();
        Book book = bookRepository5.findById(bookId).get();

        //for the given transaction calculate the fine amount considering the book has been returned exactly when this function is called
        //converting new date to temporal
        LocalDate date1 = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate date2 = transaction.getTransactionDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        //calculating days between two temporal dates
        long days = ChronoUnit.DAYS.between(date2, date1);

        //int days =new Date().compareTo(transaction.getTransactionDate());
        int fine = 0;
        if(days > getMax_allowed_days){
            fine = (int)(days - getMax_allowed_days)* fine_per_day;
        }

        //make the book available for other users
        book.setAvailable(true);
        book.setCard(null);
        //make a new transaction for return book which contains the fine amount as well
        Transaction newTransaction = createNewTransaction(card, book, false, TransactionStatus.SUCCESSFUL);

        newTransaction.setFineAmount(fine);
        newTransaction = transactionRepository5.save(newTransaction);

        //Transaction returnBookTransaction  = null;
        return newTransaction; //return the transaction after updating all details
    }
}
