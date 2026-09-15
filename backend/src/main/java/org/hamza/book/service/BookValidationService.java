package org.hamza.book.service;


import org.hamza.book.enums.SectionType;
import org.hamza.book.exception.InvalidBookException;
import org.hamza.book.model.BookEntity;
import org.hamza.book.model.SectionEntity;
import org.hamza.book.model.OptionEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookValidationService {

    public void validateBook(BookEntity book) {
        List<SectionEntity> sections = book.getSections();

        if (CollectionUtils.isEmpty(sections)) {
            throw new InvalidBookException("Book must contain at least one section.");
        }

        validateBeginningSections(sections);
        validateEndingSections(sections);

        Set<Long> validSectionIds = extractValidSectionIds(sections);
        validateSectionIntegrity(sections, validSectionIds);
    }

    private void validateBeginningSections(List<SectionEntity> sections) {
        long beginCount = sections.stream()
                .filter(s -> SectionType.BEGIN.equals(s.getType()))
                .count();

        if (beginCount != 1) {
            throw new InvalidBookException("Book must have exactly one beginning section, found: " + beginCount);
        }
    }

    private void validateEndingSections(List<SectionEntity> sections) {
        boolean hasEnding = sections.stream()
                .anyMatch(s -> SectionType.END.equals(s.getType()) || CollectionUtils.isEmpty(s.getOptions()));

        if (!hasEnding) {
            throw new InvalidBookException("Book must have at least one ending section.");
        }
    }

    private Set<Long> extractValidSectionIds(List<SectionEntity> sections) {
        return sections.stream()
                .map(SectionEntity::getOriginalSectionId)
                .collect(Collectors.toSet());
    }

    private void validateSectionIntegrity(List<SectionEntity> sections, Set<Long> validSectionIds) {
        for (SectionEntity section : sections) {
            boolean isEndSection = SectionType.END.equals(section.getType());
            List<OptionEntity> options = section.getOptions();

            // Non-ending sections must have options
            if (!isEndSection && CollectionUtils.isEmpty(options)) {
                throw new InvalidBookException("Non-ending section with ID " + section.getOriginalSectionId() + " has no options.");
            }

            // Options must point to valid destination section IDs
            if (!CollectionUtils.isEmpty(options)) {
                for (OptionEntity option : options) {
                    if (!validSectionIds.contains(option.getGotoId())) {
                        throw new InvalidBookException("Section " + section.getOriginalSectionId()
                                + " contains an option pointing to invalid gotoId: " + option.getGotoId());
                    }
                }
            }
        }
    }
}