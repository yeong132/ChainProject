package org.zerock.chain.pse.service;

import org.zerock.chain.pse.dto.FavoriteQnaDTO;
import org.zerock.chain.pse.dto.FavoriteQnaRequestDTO;

import java.util.List;

public interface FavoriteQnaService {
    List<FavoriteQnaDTO> getAllFAQs();
    FavoriteQnaDTO getFAQById(Long faqNo);
    FavoriteQnaDTO createFAQ(FavoriteQnaRequestDTO favoriteQnaRequestDTO);
    // FAQ 수정 메소드
    void updateFaq(Long faqNo, String faqName, String faqContent);
    // FAQ 삭제 메소드
    void deleteFaq(Long faqNo);
}
