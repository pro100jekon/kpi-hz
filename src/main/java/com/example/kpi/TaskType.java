package com.example.kpi;

import com.mongodb.WriteConcern;

public enum TaskType {

    WRITE_CONCERN_1(new WriteConcern(1, 1000)), WRITE_CONCERN_3(new WriteConcern(3, 1000)), WRITE_CONCERN_MAJORITY(WriteConcern.MAJORITY);

    public WriteConcern getWriteConcern() {
        return writeConcern;
    }

    private final WriteConcern writeConcern;

    TaskType(WriteConcern writeConcern) {
        this.writeConcern = writeConcern;
    }

    public static TaskType fromArgs(String[] args) {
        if (args.length == 0) return WRITE_CONCERN_1;
        switch (args[0]) {
            case "w1" -> {
                return WRITE_CONCERN_1;
            }
            case "w3" -> {
                return WRITE_CONCERN_3;
            }
            case "majority" -> {
                return WRITE_CONCERN_MAJORITY;
            }
        }
        return WRITE_CONCERN_1;
    }
}
