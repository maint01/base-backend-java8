package vn.com.lifesup.hackathon.dto.base;

import java.util.List;

public class ResultSelectDTO {
    List<? extends Object> listData;
    Object totalItem;

    public ResultSelectDTO() {
    }

    public List<? extends Object> getListData() {
        return this.listData;
    }

    public void setListData(List<? extends Object> listData) {
        this.listData = listData;
    }

    public Object getTotalItem() {
        return this.totalItem;
    }

    public void setTotalItem(Object totalItem) {
        this.totalItem = totalItem;
    }
}
