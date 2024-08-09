package org.zerock.chain.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.chain.dto.NoticeDTO;
import org.zerock.chain.dto.NoticeRequestDTO;
import org.zerock.chain.model.Notice;
import org.zerock.chain.repository.NoticeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private ModelMapper modelMapper;

    // 공지사항 전체 목록 조회
    @Override
    public List<NoticeDTO> getAllNotices() {
        return noticeRepository.findAll().stream()
                .map(notice -> modelMapper.map(notice, NoticeDTO.class))
                .collect(Collectors.toList());
    }

    // 개별 공지사항 상세 조회
    @Override
    public NoticeDTO getNoticeById(Long noticeNo) {
        Notice notice = noticeRepository.findById(noticeNo)
                .orElseThrow(() -> new RuntimeException("Notice not found with id: " + noticeNo));
        return modelMapper.map(notice, NoticeDTO.class);
    }

    // 새로운 공지사항 생성
    @Override
    public NoticeDTO createNotice(NoticeRequestDTO noticeRequestDTO) {
        Notice notice = modelMapper.map(noticeRequestDTO, Notice.class);
        notice = noticeRepository.save(notice);
        return modelMapper.map(notice, NoticeDTO.class);
    }

    // 기존 공지사항 수정
    @Override
    public NoticeDTO updateNotice(Long noticeNo, NoticeRequestDTO noticeRequestDTO) {
        Notice notice = noticeRepository.findById(noticeNo)
                .orElseThrow(() -> new RuntimeException("Notice not found with id: " + noticeNo));

        modelMapper.map(noticeRequestDTO, notice);
        notice = noticeRepository.save(notice);
        return modelMapper.map(notice, NoticeDTO.class);
    }

    // 공지사항 삭제
    @Override
    public void deleteNotice(Long noticeNo) {
        if (!noticeRepository.existsById(noticeNo)) {
            throw new RuntimeException("Notice not found with id: " + noticeNo);
        }
        noticeRepository.deleteById(noticeNo);
    }
}
