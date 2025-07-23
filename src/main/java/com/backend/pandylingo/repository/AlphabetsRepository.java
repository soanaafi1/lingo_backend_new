package com.backend.pandylingo.repository;

import com.backend.pandylingo.model.Alphabets;
import com.backend.pandylingo.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface AlphabetsRepository extends JpaRepository<Alphabets, UUID> {
    List<Alphabets> findByLanguage(Language language);
}