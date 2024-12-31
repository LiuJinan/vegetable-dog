package cn.liujinnan.vegetable.dog.utils.tableImg;

import lombok.Data;

import java.awt.*;

@Data
public class Cell {

    /**
     * 内容
     */
    private String content;

    /**
     * 字体。默认宋体
     */
    private Font font = new Font("宋体", Font.BOLD, 20);

    private Color color = Color.BLACK;

    private AlignmentEnum alignment =  AlignmentEnum.LEFT;
}
