package com.mymusic.orvai.travel_with.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Attraction_Result_API {

    @SerializedName("response")
    private Response_API response;

    public Response_API getResponse() {
        return response;
    }

    public void setResponse(Response_API response) {
        this.response = response;
    }

    public class Response_API {

        @SerializedName("header")
        @Expose
        private Header_API header;
        @SerializedName("body")
        @Expose
        private Body_API body;

        public Header_API getHeader() {
            return header;
        }

        public void setHeader(Header_API header) {
            this.header = header;
        }

        public Body_API getBody() {
            return body;
        }

        public void setBody(Body_API body) {
            this.body = body;
        }

    }

    public class Header_API {

        @SerializedName("resultCode")
        @Expose
        private String resultCode;
        @SerializedName("resultMsg")
        @Expose
        private String resultMsg;

        public String getResultCode() {
            return resultCode;
        }

        public void setResultCode(String resultCode) {
            this.resultCode = resultCode;
        }

        public String getResultMsg() {
            return resultMsg;
        }

        public void setResultMsg(String resultMsg) {
            this.resultMsg = resultMsg;
        }

    }

    public class Body_API {

        @SerializedName("items")
        @Expose
        private Items items;
        @SerializedName("numOfRows")
        @Expose
        private Integer numOfRows;
        @SerializedName("pageNo")
        @Expose
        private Integer pageNo;
        @SerializedName("totalCount")
        @Expose
        private Integer totalCount;

        public Items getItems() {
            return items;
        }

        public void setItems(Items items) {
            this.items = items;
        }

        public Integer getNumOfRows() {
            return numOfRows;
        }

        public void setNumOfRows(Integer numOfRows) {
            this.numOfRows = numOfRows;
        }

        public Integer getPageNo() {
            return pageNo;
        }

        public void setPageNo(Integer pageNo) {
            this.pageNo = pageNo;
        }

        public Integer getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(Integer totalCount) {
            this.totalCount = totalCount;
        }

    }

    public class Items {

        @SerializedName("item")
        @Expose
        private List<Item> item = null;

        public List<Item> getItem() {
            return item;
        }

        public void setItem(List<Item> item) {
            this.item = item;
        }

    }

    public class Item {

        @SerializedName("addr1")
        @Expose
        private String addr1;
        @SerializedName("addr2")
        @Expose
        private String addr2;
        @SerializedName("areacode")
        @Expose
        private Integer areacode;
        @SerializedName("cat1")
        @Expose
        private String cat1;
        @SerializedName("cat2")
        @Expose
        private String cat2;
        @SerializedName("cat3")
        @Expose
        private String cat3;
        @SerializedName("contentid")
        @Expose
        private String contentid;
        @SerializedName("contenttypeid")
        @Expose
        private String contenttypeid;
        @SerializedName("createdtime")
        @Expose
        private String createdtime;
        @SerializedName("firstimage")
        @Expose
        private String firstimage;
        @SerializedName("firstimage2")
        @Expose
        private String firstimage2;
        @SerializedName("mapx")
        @Expose
        private Double mapx;
        @SerializedName("mapy")
        @Expose
        private Double mapy;
        @SerializedName("mlevel")
        @Expose
        private Integer mlevel;
        @SerializedName("modifiedtime")
        @Expose
        private String modifiedtime;
        @SerializedName("readcount")
        @Expose
        private Integer readcount;
        @SerializedName("sigungucode")
        @Expose
        private Integer sigungucode;
        @SerializedName("tel")
        @Expose
        private String tel;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("zipcode")
        @Expose
        private String zipcode;

        public String getAddr1() {
            return addr1;
        }

        public void setAddr1(String addr1) {
            this.addr1 = addr1;
        }

        public String getAddr2() {
            return addr2;
        }

        public void setAddr2(String addr2) {
            this.addr2 = addr2;
        }

        public Integer getAreacode() {
            return areacode;
        }

        public void setAreacode(Integer areacode) {
            this.areacode = areacode;
        }

        public String getCat1() {
            return cat1;
        }

        public void setCat1(String cat1) {
            this.cat1 = cat1;
        }

        public String getCat2() {
            return cat2;
        }

        public void setCat2(String cat2) {
            this.cat2 = cat2;
        }

        public String getCat3() {
            return cat3;
        }

        public void setCat3(String cat3) {
            this.cat3 = cat3;
        }

        public String getContentid() {
            return contentid;
        }

        public void setContentid(String contentid) {
            this.contentid = contentid;
        }

        public String getContenttypeid() {
            return contenttypeid;
        }

        public void setContenttypeid(String contenttypeid) {
            this.contenttypeid = contenttypeid;
        }

        public String getCreatedtime() {
            return createdtime;
        }

        public void setCreatedtime(String createdtime) {
            this.createdtime = createdtime;
        }

        public String getFirstimage() {
            return firstimage;
        }

        public void setFirstimage(String firstimage) {
            this.firstimage = firstimage;
        }

        public String getFirstimage2() {
            return firstimage2;
        }

        public void setFirstimage2(String firstimage2) {
            this.firstimage2 = firstimage2;
        }

        public Double getMapx() {
            return mapx;
        }

        public void setMapx(Double mapx) {
            this.mapx = mapx;
        }

        public Double getMapy() {
            return mapy;
        }

        public void setMapy(Double mapy) {
            this.mapy = mapy;
        }

        public Integer getMlevel() {
            return mlevel;
        }

        public void setMlevel(Integer mlevel) {
            this.mlevel = mlevel;
        }

        public String getModifiedtime() {
            return modifiedtime;
        }

        public void setModifiedtime(String modifiedtime) {
            this.modifiedtime = modifiedtime;
        }

        public Integer getReadcount() {
            return readcount;
        }

        public void setReadcount(Integer readcount) {
            this.readcount = readcount;
        }

        public Integer getSigungucode() {
            return sigungucode;
        }

        public void setSigungucode(Integer sigungucode) {
            this.sigungucode = sigungucode;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getZipcode() {
            return zipcode;
        }

        public void setZipcode(String zipcode) {
            this.zipcode = zipcode;
        }

    }

}