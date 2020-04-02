package com.fosuchao.community.entity;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/2 15:36
 */

public class Page {

    // 当前页码
    private int current = 1;

    // 数量
    private int limit = 10;

    // 总行数
    private int rows;

    // 查询路劲
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取当前页的起始行
     * @return int
     */
    public int getOffset() {
        return (current - 1) * limit;
    }

    /**
     * 获取总页数
     * @return int
     */
    public int getTotal() {
        if (rows % limit == 0) {
            return rows / limit;
        } else {
            return rows / limit + 1;
        }
    }

    /**
     * 获取起始页码
     * @return int
     */
    public int getFrom() {
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    /**
     * 获取最后一页
     * @return int
     */
    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        return to > total ? total : to;
    }
}
