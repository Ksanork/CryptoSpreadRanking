package mj.kangarecruitmenttask.cryptospreadranking.controller;

import mj.kangarecruitmenttask.cryptospreadranking.exception.MarketApiException;
import mj.kangarecruitmenttask.cryptospreadranking.service.SpreadRankingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/spread")
public class SpreadRankingController {

    private final SpreadRankingService spreadRankingService;

    public SpreadRankingController(SpreadRankingService spreadRankingService) {
        this.spreadRankingService = spreadRankingService;
    }

    @GetMapping("/ranking")
    public ResponseEntity<?> getMapping() {
        return ResponseEntity.ok(spreadRankingService.getRanking());
    }

    @PostMapping("/calculate")
    public ResponseEntity<?> calcluate() {
        try {
            spreadRankingService.calculateRanking();
            return ResponseEntity.ok("Ranking has been created");
        } catch (MarketApiException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Problem with Kanga API");
        }
    }
}
