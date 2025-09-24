package com.platformone.train.service.impl;

import com.platformone.train.entity.Route;
import com.platformone.train.entity.Train;
import com.platformone.train.repository.TrainRepository;
import com.platformone.train.service.TrainService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainServiceImpl implements TrainService {
    private final TrainRepository trainRepository;

    public TrainServiceImpl(TrainRepository trainRepository) {
        this.trainRepository = trainRepository;
    }

    @Override
    public Train getTrainById(long trainId) {
        return trainRepository.findById(trainId).orElse(null);
    }

    @Override
    public Train createTrain(Train newTrain) {
        return trainRepository.save(newTrain);
    }

    @Override
    public Optional<Train> updateTrain(long trainId, Train updatedTrain) {
        return trainRepository.findById(trainId).map(train -> {
            train.setName(updatedTrain.getName());
            train.setType(updatedTrain.getType());
            train.getRoutes().clear();
            if(!updatedTrain.getRoutes().isEmpty()){
                for(Route route: updatedTrain.getRoutes()){
                    train.addRoute(route);
                }
            }
            return trainRepository.save(train);
        });
    }

    @Override
    public boolean deleteTrain(long trainId) {
        Train train=getTrainById(trainId);
        if(train==null) return false;
        trainRepository.deleteById(trainId);
        return true;
    }

    @Override
    public List<Train> getTrainBySrcDest(String src, String dest) {
        return trainRepository.findTrainsBySrcAndDest(src,dest);
    }
}
