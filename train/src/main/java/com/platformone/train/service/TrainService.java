package com.platformone.train.service;

import com.platformone.train.entity.Train;

import java.util.Optional;

public interface TrainService {
    Train getTrainById(long trainId);

    Train createTrain(Train newTrain);

    Optional<Train> updateTrain(long trainId, Train updatedTrain);

    boolean deleteTrain(long trainId);
}
