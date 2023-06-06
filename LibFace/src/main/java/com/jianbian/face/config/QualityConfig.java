package com.jianbian.face.config;

import org.json.JSONObject;

public class QualityConfig {
    // 光照
    public float minIllum;  // 最大
    public float maxIllum;  // 最小
    // 模糊
    public float blur;
    // 遮挡
    public float leftEyeOcclusion;       // 左眼
    public float rightEyeOcclusion;      // 右眼
    public float noseOcclusion;          // 鼻子
    public float mouseOcclusion;         // 嘴巴
    public float leftContourOcclusion;   // 左脸颊
    public float rightContourOcclusion;  // 右脸颊
    public float chinOcclusion;          // 下巴
    // 姿态角
    public int pitch;   // 上下角
    public int yaw;     // 左右角
    public int roll;    // 旋转角
    
}
