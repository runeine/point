package com.example.point.dto;

import com.example.point.entity.Point;

import lombok.Data;

@Data
public class PointResponse {

    private String resultCode; // 결과 코드
    private String resultMessage; // 결과 메시지
    private Point point; // 포인트 정보

    public static PointResponse success(String resultCode, String resultMessage , Point point) {
        PointResponse response = new PointResponse();
        response.setResultCode(resultCode);
        response.setResultMessage(resultMessage);
        response.setPoint(point);
        return response;
    }
    
    


}
