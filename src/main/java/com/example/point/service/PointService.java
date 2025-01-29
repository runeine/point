package com.example.point.service;

import com.example.point.dto.PointRequest;
import com.example.point.dto.PointResponse;
import com.example.point.entity.Point;

public interface PointService {

    public Point savePoint(PointRequest request) throws Exception;

    public PointResponse usePoint(PointRequest request) throws Exception;

    public Point cancelSavedPoint(PointRequest request) throws Exception;

    public PointResponse cancelUsedPoint(PointRequest request) throws Exception;


    


}