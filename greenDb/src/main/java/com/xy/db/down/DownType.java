package com.xy.db.down;

public enum DownType {
    DOWN_ING(1),PAUSE(2),SUCESS(3),ERROR(4);
    public int status;
    DownType(int status) {
        this.status = status;
    }
}
