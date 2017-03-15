package com.zemult.merchant.util;


import com.zemult.merchant.R;
import com.zemult.merchant.model.FilterEntity;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.M_News;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.model.M_Userinfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据源
 */
public class ModelUtil {
    // 广告数据
    public static List<String> getAdEmptyData() {
        List<String> adList = new ArrayList<>();
        adList.add("");
        return adList;
    }

    // 排序数据
    public static List<FilterEntity> getSexData() {
        List<FilterEntity> list = new ArrayList<>();
        list.add(new FilterEntity("全部", -1, true, R.mipmap.quanbu_icon_nor, R.mipmap.quanbu_icon_sel));
        list.add(new FilterEntity("男生", 0, false, R.mipmap.man_icon_nor, R.mipmap.man_icon_sel));
        list.add(new FilterEntity("女生", 1, false, R.mipmap.woman_icon_nor, R.mipmap.woman_icon_sel));
        return list;
    }
    public static List<FilterEntity> getTypeData() {
        List<FilterEntity> list = new ArrayList<>();
        list.add(new FilterEntity("全部", -1, true,  R.mipmap.quanbu_icon_nor, R.mipmap.quanbu_icon_sel));
        list.add(new FilterEntity("分享", 0, false, R.mipmap.fenxiang_icon_nor, R.mipmap.fenxiang_icon_sel));
        list.add(new FilterEntity("语音", 1, false, R.mipmap.yuying_icon_nor, R.mipmap.yuying_icon_sel));
        list.add(new FilterEntity("投票", 2, false, R.mipmap.toupiao_icon_nor, R.mipmap.toupiao_icon_sel));
        list.add(new FilterEntity("交易", 3, false, R.mipmap.xiaofei_icon_nor, R.mipmap.xiaofei_icon_sel));
        return list;
    }
    public static List<FilterEntity> getFriendData() {
        List<FilterEntity> list = new ArrayList<>();
        list.add(new FilterEntity("热门", 0, true, R.mipmap.haoyou_icon_nor, R.mipmap.haoyou_icon_sel));
        list.add(new FilterEntity("最新", 1, false, R.mipmap.moshenren_icon_nor, R.mipmap.moshenren_icon_sel));
        return list;
    }
    public static List<FilterEntity> getSortData() {
        List<FilterEntity> list = new ArrayList<>();
        list.add(new FilterEntity("最新发布", 0, true,  R.mipmap.quanbu_icon_nor));
        list.add(new FilterEntity("人气最高", 1, false, R.mipmap.renqi_icon));
        list.add(new FilterEntity("距离最近", 2, false, R.mipmap.place_icon));
        return list;
    }
    // 排序数据
    public static List<FilterEntity> getReportData() {
        List<FilterEntity> list = new ArrayList<>();
        list.add(new FilterEntity("色情低俗", 1));
        list.add(new FilterEntity("广告骚扰", 2));
        list.add(new FilterEntity("政治敏感", 3));
        list.add(new FilterEntity("欺诈骗钱", 4));
        list.add(new FilterEntity("违法(暴力恐怖、违禁品)", 5));
        list.add(new FilterEntity("抄袭、诽谤、冒用", 6));
        return list;
    }

    // 暂无数据
    public static List<M_News> getNoDataEntity(int height) {
        List<M_News> list = new ArrayList<>();
        M_News entity = new M_News();
        entity.setNoData(true);
        entity.setHeight(height);
        list.add(entity);
        return list;
    }
    // 暂无数据
    public static List<M_Task> getNoDataTaskEntity(int height) {
        List<M_Task> list = new ArrayList<>();
        M_Task entity = new M_Task();
        entity.setNoData(true);
        entity.setHeight(height);
        list.add(entity);
        return list;
    }
    // 暂无数据
    public static List<M_Merchant> getNoDataMerchantEntity(int height) {
        List<M_Merchant> list = new ArrayList<>();
        M_Merchant entity = new M_Merchant();
        entity.setNoData(true);
        entity.setHeight(height);
        list.add(entity);
        return list;
    }
    // 暂无数据
    public static List<M_Userinfo> getNoDataUserEntity(int height) {
        List<M_Userinfo> list = new ArrayList<>();
        M_Userinfo entity = new M_Userinfo();
        entity.setNoData(true);
        entity.setHeight(height);
        list.add(entity);
        return list;
    }

    // 首页右上角按钮数据
    public static List<FilterEntity> getHomeRightData(boolean showRedDot) {
        List<FilterEntity> list = new ArrayList<>();
        list.add(new FilterEntity("待结账单", 0, showRedDot, R.mipmap.yuyuemaidan_home_icon));
        list.add(new FilterEntity("扫一扫", 1, R.mipmap.saomiao_home_icon));
        list.add(new FilterEntity("发起预邀", 2, R.mipmap.yuyao_icon));
        list.add(new FilterEntity("推荐给好友", 3, R.mipmap.fenxiang_icon));
        return list;
    }
}
