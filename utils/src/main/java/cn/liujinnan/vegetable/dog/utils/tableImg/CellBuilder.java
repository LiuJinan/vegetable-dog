package cn.liujinnan.vegetable.dog.utils.tableImg;

import java.awt.*;


public class CellBuilder {

    private CellBuilder() {
    }

    private String content;

    private Font font = new Font("宋体", Font.BOLD, 20);

    private Color color = Color.BLACK;

    private AlignmentEnum alignment =  AlignmentEnum.LEFT;

    public static CellBuilder builder(String content){
        CellBuilder cellBuilder = new CellBuilder();
        cellBuilder.content = content;
        return cellBuilder;
    }

    public CellBuilder color(Color color){
        this.color = color;
        return this;
    }

    public CellBuilder colorRed(){
        this.color = Color.RED;
        return this;
    }

    public CellBuilder colorGreen(){
        this.color = Color.GREEN;
        return this;
    }

    public CellBuilder right(){
        this.alignment = AlignmentEnum.RIGHT;
        return this;
    }

    public CellBuilder center(){
        this.alignment = AlignmentEnum.CENTER;
        return this;
    }

    public Cell build(){
        Cell cell = new Cell();
        cell.setFont(this.font);
        cell.setContent(this.content);
        cell.setColor(this.color);
        cell.setAlignment(this.alignment);
        return cell;
    }
}
