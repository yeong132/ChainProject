package org.zerock.chain.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.chain.dto.RankDTO;
import org.zerock.chain.entity.Rank;
import org.zerock.chain.repository.RankRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class RankService {

    @Autowired
    private RankRepository rankRepository;

    @Autowired
    private ModelMapper modelMapper;

    // 모든 직급 목록을 조회하여 DTO 리스트로 반환합니다.
    public List<RankDTO> getAllRanks() {
        List<Rank> ranks = rankRepository.findAll();
        return ranks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 주어진 직급 번호로 직급을 조회하여 DTO로 반환합니다.
    public RankDTO getRankById(int rankNo) {
        Optional<Rank> rank = rankRepository.findById(rankNo);
        return rank.map(this::convertToDTO).orElse(null);
    }

    // 새로운 직급을 생성합니다.
    public RankDTO createRank(RankDTO rankDTO) {
        Rank rank = convertToEntity(rankDTO);
        rankRepository.save(rank);
        return convertToDTO(rank);
    }

    // 기존 직급 정보를 업데이트합니다.
    public RankDTO updateRank(int rankNo, RankDTO rankDTO) {
        Optional<Rank> existingRankOptional = rankRepository.findById(rankNo);
        if (existingRankOptional.isPresent()) {
            Rank existingRank = existingRankOptional.get();
            modelMapper.map(rankDTO, existingRank); // DTO의 값으로 엔티티를 업데이트
            rankRepository.save(existingRank);
            return convertToDTO(existingRank);
        } else {
            return null; // 존재하지 않는 경우
        }
    }

    // 주어진 직급 번호로 직급을 삭제합니다.
    public void deleteRank(int rankNo) {
        rankRepository.deleteById(rankNo);
    }

    // Rank 엔티티를 RankDTO로 변환합니다.
    private RankDTO convertToDTO(Rank rank) {
        return modelMapper.map(rank, RankDTO.class);
    }

    // RankDTO를 Rank 엔티티로 변환합니다.
    private Rank convertToEntity(RankDTO rankDTO) {
        return modelMapper.map(rankDTO, Rank.class);
    }
}
