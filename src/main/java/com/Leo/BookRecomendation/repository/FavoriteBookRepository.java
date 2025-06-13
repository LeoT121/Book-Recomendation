package com.Leo.BookRecomendation.repository;

import com.Leo.BookRecomendation.entity.FavoriteBook;
import com.Leo.BookRecomendation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteBookRepository extends JpaRepository<FavoriteBook, Long> {
    
    List<FavoriteBook> findByUserOrderByDateDesc(User user);
    
    Optional<FavoriteBook> findByUserAndBookId(User user, String bookId);

    Optional<FavoriteBook> findByUserAndId(User user, Long Id);
    
    boolean existsByUserAndBookId(User user, String bookId);
}