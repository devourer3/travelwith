package com.mymusic.orvai.travel_with.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attraction_Detail_Common_Result_API {

    @SerializedName("response")
    @Expose
    public ADC_Response response;

    public ADC_Response getResponse() {
        return response;
    }

    public class ADC_Response {

        @SerializedName("header")
        @Expose
        public ADC_Header header;
        @SerializedName("body")
        @Expose
        public ADC_Body body;

        public ADC_Header getHeader() {
            return header;
        }

        public ADC_Body getBody() {
            return body;
        }
    }

    public class ADC_Body {

        @SerializedName("items")
        @Expose
        public ADC_Items items;
        @SerializedName("numOfRows")
        @Expose
        public int numOfRows;
        @SerializedName("pageNo")
        @Expose
        public int pageNo;
        @SerializedName("totalCount")
        @Expose
        public int totalCount;

        public ADC_Items getItems() {
            return items;
        }

        public int getNumOfRows() {
            return numOfRows;
        }

        public int getPageNo() {
            return pageNo;
        }

        public int getTotalCount() {
            return totalCount;
        }
    }

    public class ADC_Header {

        @SerializedName("resultCode")
        @Expose
        public String resultCode;
        @SerializedName("resultMsg")
        @Expose
        public String resultMsg;

        public String getResultCode() {
            return resultCode;
        }

        public String getResultMsg() {
            return resultMsg;
        }
    }

    public class ADC_Items {

        @SerializedName("item")
        @Expose
        public ADC_Item item;

        public ADC_Item getItem() {
            return item;
        }
    }

    public class ADC_Item {

        @SerializedName("addr1")
        @Expose
        public String addr1;
        @SerializedName("addr2")
        @Expose
        public String addr2;
        @SerializedName("contentid")
        @Expose
        public int contentid;
        @SerializedName("contenttypeid")
        @Expose
        public int contenttypeid;
        @SerializedName("firstimage")
        @Expose
        public String firstimage;
        @SerializedName("firstimage2")
        @Expose
        public String firstimage2;
        @SerializedName("mapx")
        @Expose
        public String mapx;
        @SerializedName("mapy")
        @Expose
        public String mapy;
        @SerializedName("mlevel")
        @Expose
        public int mlevel;
        @SerializedName("overview")
        @Expose
        public String overview;
        @SerializedName("zipcode")
        @Expose
        public String zipcode;

        public String getAddr1() {
            return addr1;
        }

        public String getAddr2() {
            return addr2;
        }

        public int getContentid() {
            return contentid;
        }

        public int getContenttypeid() {
            return contenttypeid;
        }

        public String getFirstimage() {
            return firstimage;
        }

        public String getFirstimage2() {
            return firstimage2;
        }

        public String getMapx() {
            return mapx;
        }

        public String getMapy() {
            return mapy;
        }

        public int getMlevel() {
            return mlevel;
        }

        public String getOverview() {
            return overview;
        }

        public String getZipcode() {
            return zipcode;
        }
    }




}



