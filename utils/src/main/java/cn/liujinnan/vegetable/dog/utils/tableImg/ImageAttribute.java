package cn.liujinnan.vegetable.dog.utils.tableImg;

import lombok.Data;


@Data
public class ImageAttribute {

    /**
     * 图片大小
     */
    private Integer imgWidth;
    private Integer imgHeight;

    /**
     * 单元格大小
     */
    private Integer cellWidth;
    private Integer cellHeight;

    /**
     * 表格行列
     */
    private Integer tableColNum;
    private Integer tableRowNum;

    /**
     * 表格距离左边边距
     */
    private Integer widthDistance;
    /**
     * 表格距离上边距
     */
    private Integer heightDistance;

    /**
     * 字体在单元格的偏移量
     */
    private Integer widthFontDistance;
    private Integer heightFontDistance;
}
