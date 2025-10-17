package com.platformone.booking.clients;

import com.platformone.booking.external.Train;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "train")
public interface TrainClient {
    @GetMapping("/train/{trainId}")
    Train getTrainById(@PathVariable long trainId);
}
