package com.backend.pandylingo.service;

import com.backend.pandylingo.dto.AlphabetDetailDTO;
import com.backend.pandylingo.dto.AlphabetListDTO;
import com.backend.pandylingo.exception.NotFoundException;
import com.backend.pandylingo.model.Alphabets;
import com.backend.pandylingo.model.Language;
import com.backend.pandylingo.repository.AlphabetsRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AlphabetsService {
    private final AlphabetsRepository alphabetsRepository;

    public AlphabetsService(AlphabetsRepository alphabetsRepository) {
        this.alphabetsRepository = alphabetsRepository;
    }

    public List<AlphabetListDTO> getAlphabetListByLanguage(Language language) {
        List<Alphabets> alphabets = alphabetsRepository.findByLanguage(language);
        return alphabets.stream().map(this::convertToListDTO).collect(Collectors.toList());
    }

    public AlphabetDetailDTO getAlphabetDetails(UUID id) {
        Alphabets alphabet = alphabetsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Alphabet not found"));
        return convertToDetailDTO(alphabet);
    }

    private AlphabetListDTO convertToListDTO(Alphabets alphabet) {
        return AlphabetListDTO.builder()
                .id(alphabet.getId().toString())
                .character(String.valueOf(alphabet.getCharacter()))
                .pronunciation(alphabet.getPronunciation())
                .difficulty(alphabet.getDifficulty().toString().toLowerCase())
                .build();
    }

    private AlphabetDetailDTO convertToDetailDTO(Alphabets alphabet) {
        return AlphabetDetailDTO.builder()
                .id(alphabet.getId().toString())
                .character(String.valueOf(alphabet.getCharacter()))
                .name(alphabet.getName())
                .pronunciation(alphabet.getPronunciation())
                .ipa(alphabet.getIpa())
                .mouthPosition(alphabet.getMouthPosition())
                .tonguePosition(alphabet.getTonguePosition())
                .commonMispronunciations(alphabet.getCommonMispronunciations() != null ?
                        List.of(alphabet.getCommonMispronunciations().split(",")) : List.of())
                .audioUrl(alphabet.getAudioUrl())
                .audioUrlSlow(alphabet.getSlowAudioUrl())
                .audioUrlNative(alphabet.getNativeAudioUrl())
                .examples(List.of()) // Placeholder for examples
                .similarTo(alphabet.getSimilarTo())
                .difficulty(alphabet.getDifficulty().toString().toLowerCase())
                .build();
    }
}