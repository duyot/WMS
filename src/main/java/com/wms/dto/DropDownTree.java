package com.wms.dto;

import java.util.List;

public class DropDownTree {
    String title;
    List<DataAttribute> dataAttrs;
    List<DropDownTree> data;

    public DropDownTree(String title, List<DataAttribute> dataAttrs, List<DropDownTree> data) {
        this.title = title;
        this.dataAttrs = dataAttrs;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public List<DataAttribute> getDataAttrs() {
        return dataAttrs;
    }

    public void setDataAttrs(List<DataAttribute> dataAttrs) {
        this.dataAttrs = dataAttrs;
    }

    public List<DropDownTree> getData() {
        return data;
    }

    public void setData(List<DropDownTree> data) {
        this.data = data;
    }

    class DataAttribute {
        String title;
        String data;

        public DataAttribute(String title, String data) {
            this.title = title;
            this.data = data;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}
