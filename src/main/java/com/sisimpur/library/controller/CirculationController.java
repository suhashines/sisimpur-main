package com.sisimpur.library.controller;

import com.sisimpur.library.service.CirculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/circulation")
@RequiredArgsConstructor

public class CirculationController {

    private final CirculationService circulationService;

    @PostMapping("/borrow")
    public ResponseEntity<Map<String, Object>> borrowBooks(@RequestBody Map<String, Object> request) {
        try {
            Long userId = ((Number) request.get("user_id")).longValue();
            List<Integer> bookIdsInt = (List<Integer>) request.get("book_ids");

            // Convert List<Integer> to List<Long>
            List<Long> bookIds = bookIdsInt.stream().map(Integer::longValue).toList();

            Map<String, Object> response = circulationService.borrowBooks(userId, bookIds);

            if (!(boolean) response.get("success")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Invalid request format"));
        }
    }

    @PostMapping("/return")
    public ResponseEntity<Map<String, Object>> returnBooks(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("user_id").toString());
        List<Long> bookIds = ((List<Integer>) request.get("book_ids")).stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());

        Map<String, Object> response = circulationService.returnBooks(userId, bookIds);

        if (!(boolean) response.get("success")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.ok(response);
    }

}
