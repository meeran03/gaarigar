package com.gianteyes.gaarigar.rating;

import com.gianteyes.gaarigar.rating.dto.RatingRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rate")
public class RatingController {
    @Autowired
    private RatingService ratingService;

    @PostMapping("/")
    public void rate(@RequestBody @Valid RatingRequestDto rateObj)
    {
        this.ratingService.rate(rateObj);
    }

}
