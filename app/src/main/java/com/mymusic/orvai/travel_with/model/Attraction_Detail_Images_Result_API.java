package com.mymusic.orvai.travel_with.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Attraction_Detail_Images_Result_API {

    @SerializedName("response")
    @Expose
    public ADI_Response response;

    public ADI_Response getResponse() {
        return response;
    }

    public class ADI_Response {

        @SerializedName("header")
        @Expose
        public ADI_Header header;
        @SerializedName("body")
        @Expose
        public ADI_Body body;

        public ADI_Header getHeader() {
            return header;
        }

        public ADI_Body getBody() {
            return body;
        }
    }

    public class ADI_Body {

        @SerializedName("items")
        @Expose
        public ADI_Items items;
        @SerializedName("numOfRows")
        @Expose
        public Integer numOfRows;
        @SerializedName("pageNo")
        @Expose
        public Integer pageNo;
        @SerializedName("totalCount")
        @Expose
        public Integer totalCount;

        public ADI_Items getItems() {
            return items;
        }

        public Integer getNumOfRows() {
            return numOfRows;
        }

        public Integer getPageNo() {
            return pageNo;
        }

        public Integer getTotalCount() {
            return totalCount;
        }
    }

    public class ADI_Header {

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

    public class ADI_Items {

        @SerializedName("item")
        @Expose
        public List<ADI_Item> item = null;

        public List<ADI_Item> getItem() {
            return item;
        }
    }

    public class ADI_Item {

        @SerializedName("contentid")
        @Expose
        public Integer contentid;
        @SerializedName("originimgurl")
        @Expose
        public String originimgurl;
        @SerializedName("serialnum")
        @Expose
        public String serialnum;
        @SerializedName("smallimageurl")
        @Expose
        public String smallimageurl;
        @SerializedName("imgname")
        @Expose
        public String imgname;

        public Integer getContentid() {
            return contentid;
        }

        public String getOriginimgurl() {
            return originimgurl;
        }

        public String getSerialnum() {
            return serialnum;
        }

        public String getSmallimageurl() {
            return smallimageurl;
        }

        public String getImgname() {
            return imgname;
        }
    }


}
