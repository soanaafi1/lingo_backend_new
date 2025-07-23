package com.backend.pandylingo.controller;

import com.backend.pandylingo.dto.AlphabetDetailDTO;
import com.backend.pandylingo.dto.AlphabetListDTO;
import com.backend.pandylingo.model.Language;
import com.backend.pandylingo.service.AlphabetsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/alphabets")
public class AlphabetsController {
    private final AlphabetsService alphabetsService;

    public AlphabetsController(AlphabetsService alphabetsService) {
        this.alphabetsService = alphabetsService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<AlphabetListDTO>> getAlphabetList(
            @RequestParam String lang) {
        Language language = Language.fromCode(lang);
        return ResponseEntity.ok(alphabetsService.getAlphabetListByLanguage(language));
    }

    @GetMapping("/{id}")
    public AlphabetDetailDTO getAlphabetDetails(
            @PathVariable UUID id) {
        return alphabetsService.getAlphabetDetails(id);
    }
}