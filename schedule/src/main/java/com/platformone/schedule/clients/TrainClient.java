package com.platformone.schedule.clients;

import com.platformone.schedule.external.Train;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "train")
public interface TrainClient {

    @GetMapping("/train/search")
    public List<Train> getTrainBySrcDest(@RequestParam String src, @RequestParam String dest);
}