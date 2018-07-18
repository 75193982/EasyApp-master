package com.liaoliao.chat.model;

import com.chad.library.adapter.base.entity.SectionEntity;

/**
 * Created by lv on 2018/7/16 for EasyApp-master
 */
public class HserSection extends SectionEntity<UserBaoTitle> {


    public HserSection(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public HserSection(UserBaoTitle userBaoTitle) {
        super(userBaoTitle);
    }
    public int scrollOffset ,scrollPosition;
}
