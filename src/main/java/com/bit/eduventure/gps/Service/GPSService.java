package com.bit.eduventure.gps.Service;

import com.bit.eduventure.gps.Repository.GPSRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GPSService {

    private GPSRepository gpsRepository;

    @Autowired
    public GPSService(GPSRepository gpsRepository){
        this.gpsRepository = gpsRepository;
    }


}
