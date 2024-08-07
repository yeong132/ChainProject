package org.zerock.chain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zerock.chain.dto.RankDTO;
import org.zerock.chain.service.RankService;

import java.util.List;

@RestController
@RequestMapping("/ranks")
public class RankController {

    @Autowired
    private RankService rankService;

    @GetMapping
    public List<RankDTO> getAllRanks() {
        return rankService.getAllRanks();
    }

    @GetMapping("/{rankNo}")
    public RankDTO getRankById(@PathVariable int rankNo) {
        return rankService.getRankById(rankNo);
    }

    @PostMapping
    public RankDTO createRank(@RequestBody RankDTO rankDTO) {
        return rankService.createRank(rankDTO);
    }

    @PutMapping("/{rankNo}")
    public RankDTO updateRank(@PathVariable int rankNo, @RequestBody RankDTO rankDTO) {
        return rankService.updateRank(rankNo, rankDTO);
    }

    @DeleteMapping("/{rankNo}")
    public void deleteRank(@PathVariable int rankNo) {
        rankService.deleteRank(rankNo);
    }
}
