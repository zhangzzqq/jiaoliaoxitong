package com.example.msystem.model;

import org.litepal.crud.DataSupport;

public class Material extends DataSupport {

    private String strnID;
    private String strStationName;

    private String strnQty;

    private String strPartNo;

    private String strReelID;

    private String status;


    public String getnID() {
        return strnID;
    }

    public void setnID(String nID) {
        this.strnID = nID;
    }

    public String getStrStationName() {
        return strStationName;
    }

    public void setStrStationName(String strStationName) {
        this.strStationName = strStationName;
    }

    public String getnQty() {
        return strnQty;
    }

    public void setnQty(String nQty) {
        this.strnQty = nQty;
    }

    public String getStrPartNo() {
        return strPartNo;
    }

    public void setStrPartNo(String strPartNo) {
        this.strPartNo = strPartNo;
    }

    public String getStrReelID() {
        return strReelID;
    }

    public void setStrReelID(String strReelID) {
        this.strReelID = strReelID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
