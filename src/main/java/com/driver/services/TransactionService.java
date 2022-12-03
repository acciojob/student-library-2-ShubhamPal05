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

        if(book == null || !book.isAvailable())
            throw new Exception("Book is either unavailable or not present");
        
        else if(card == null || card.getCardStatus().equals(CardStatus.DEACTIVATED))
            throw new Exception("Card is invalid");

        else if(card.getBooks().size() >= max_allowed_books)
            throw new Exception("Book limit has reached for this card");
        else{
            //saving book in card
            List<Book> bookList = card.getBooks();
            bookList.add(book);
            card.setBooks(bookList);
            cardRepository5.save(card);
            bookRepository5.save(book);

            Transaction transaction = Transaction.builder()
                                            .card(card)
                                            .book(book)
                                            .isIssueOperation(true)
                                            .transactionStatus(TransactionStatus.SUCCESSFUL)
                                            .build();
                
            transaction = transactionRepository5.save(transaction);
            return transaction.getTransactionId();
        }

    }

    public Transaction returnBook(int cardId, int bookId) throws Exception{

        List<Transaction> transactions = transactionRepository5.find(cardId, bookId, TransactionStatus.SUCCESSFUL, true);
        Transaction transaction = transactions.get(transactions.size() - 1);

        Card card = cardRepository5.findById(cardId).get();
        Book book = bookRepository5.findById(bookId).get();

        //for the given transaction calculate the fine amount considering the book has been returned exactly when this function is called
        int days =new Date().compareTo(transaction.getTransactionDate());
        int fine = 0;
        if(days > getMax_allowed_days){
            fine = (days-getMax_allowed_days)* fine_per_day;
        }

        //make the book available for other users
        book.setAvailable(true);

        //make a new transaction for return book which contains the fine amount as well
        Transaction newTransaction = Transaction.builder()
        .card(card)
        .book(book)
        .isIssueOperation(false)
        .transactionStatus(TransactionStatus.SUCCESSFUL)
        .build();

        newTransaction.setFineAmount(fine);
        newTransaction = transactionRepository5.save(newTransaction);

        //Transaction returnBookTransaction  = null;
        return newTransaction; //return the transaction after updating all details
    }
}
